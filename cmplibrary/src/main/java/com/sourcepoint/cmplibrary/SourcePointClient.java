package com.sourcepoint.cmplibrary;

import android.text.TextUtils;
import android.util.Log;

import com.google.common.annotations.VisibleForTesting;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLEncoder;
import java.util.HashSet;

import cz.msebera.android.httpclient.Header;

class SourcePointClient {
    private static final String LOG_TAG = "SOURCE_POINT_CLIENT";

    private static AsyncHttpClient http = new AsyncHttpClient();

    private URL mmsUrl, cmpUrl, messageUrl;
    private EncodedParam accountId, property, propertyId;
    private Boolean stagingCampaign, isShowPM;

    class ResponseHandler extends JsonHttpResponseHandler {
        ConsentLib.OnLoadComplete onLoadComplete;
        String url;

        ResponseHandler(String url, ConsentLib.OnLoadComplete onLoadComplete) {
            this.onLoadComplete = onLoadComplete;
            this.url = url;
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d(LOG_TAG, "Failed to load resource " + url + " due to " + statusCode + ": " + errorResponse);
            onLoadComplete.onFailure(new ConsentLibException(throwable.getMessage()));
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            Log.d(LOG_TAG, "Failed to load resource " + url + " due to " + statusCode + ": " + responseString);
            onLoadComplete.onFailure(new ConsentLibException(throwable.getMessage()));
        }
    }

    SourcePointClient(
            EncodedParam accountID,
            EncodedParam property,
            EncodedParam propertyId,
            boolean stagingCampaign,
            boolean isShowPM,
            URL mmsUrl,
            URL cmpUrl,
            URL messageUrl
    ) {
        this.stagingCampaign = stagingCampaign;
        this.isShowPM = isShowPM;
        this.accountId = accountID;
        this.propertyId = propertyId;
        this.property = property;
        this.mmsUrl = mmsUrl;
        this.cmpUrl = cmpUrl;
        this.messageUrl = messageUrl;
    }

    private String GDPRStatusUrl() {
        return cmpUrl + "/consent/v2/gdpr-status";
    }

    private String customConsentsUrl(String consentUUID, String euConsent, String propertyId, String[] vendorIds) {
        String consentParam = consentUUID == null ? "[CONSENT_UUID]" : consentUUID;
        String euconsentParam = euConsent == null ? "[EUCONSENT]" : euConsent;
        String customVendorIdString = URLEncoder.encode(TextUtils.join(",", vendorIds));

        return cmpUrl + "/consent/v2/" + propertyId + "/custom-vendors?" +
                "customVendorIds=" + customVendorIdString +
                "&consentUUID=" + consentParam +
                "&euconsent=" + euconsentParam;
    }

    //TODO: get url from endpoint
    public String messageUrl(EncodedParam targetingParams, EncodedParam authId, EncodedParam pmId) {
        HashSet<String> params = new HashSet<>();
        params.add("_sp_accountId=" + accountId);
        params.add("_sp_siteId=" + propertyId);
        params.add("_sp_siteHref=" + property);
        params.add("_sp_mms_Domain=" + mmsUrl);
        params.add("_sp_cmp_origin=" + cmpUrl);
        params.add("_sp_targetingParams=" + targetingParams);
        params.add("_sp_env=" + (stagingCampaign ? "stage" : "public"));
        params.add("_sp_PMId=" + pmId);
        params.add("_sp_runMessaging=" + (!isShowPM));
        params.add("_sp_showPM=" + isShowPM);

        if (authId != null) {
            params.add("_sp_authId=" + authId);
        }

        return "https://notice.sp-prod.net/?message_id=66281";
    }

    public String pmUrl(){
        return "https://pm.sourcepoint.mgr.consensu.org/?privacy_manager_id=5c0e81b7d74b3c30c6852301&site_id=2372&consent_origin=https://sourcepoint.mgr.consensu.org&consentUUID=ea448bec-1a6c-43f0-8d28-0ad88f6f7fe5&requestUUID=5107239d-99e2-4ef7-8d4a-d12c90858d13";
    }

    @VisibleForTesting
    void setHttpDummy(AsyncHttpClient httpClient) {
        http = httpClient;
    }

    void getGDPRStatus(ConsentLib.OnLoadComplete onLoadComplete) {
        String url = GDPRStatusUrl();
        http.get(url, new ResponseHandler(url, onLoadComplete) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    onLoadComplete.onSuccess(response.getString("gdprApplies"));
                } catch (JSONException e) {
                    onFailure(statusCode, headers, e, response);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(LOG_TAG, "Failed to load resource " + url + " due to " + statusCode + ": " + responseString);
                onLoadComplete.onFailure(new ConsentLibException(throwable.getMessage()));

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d(LOG_TAG, "Failed to load resource " + url + " due to " + statusCode + ": " + errorResponse);
                onLoadComplete.onFailure(new ConsentLibException(throwable.getMessage()));
            }
        });
    }

    private HashSet<Consent> getConsentFromResponse(JSONObject response, String type) throws JSONException {
        JSONArray consentsJSON = response.getJSONArray(type);
        HashSet<Consent> consents = new HashSet<>();
        for (int i = 0; i < consentsJSON.length(); i++) {
            switch (type) {
                case "consentedVendors": {
                    consents.add(new CustomVendorConsent(
                            consentsJSON.getJSONObject(i).getString("_id"),
                            consentsJSON.getJSONObject(i).getString("name")
                    ));
                    break;
                }
                case "consentedPurposes": {
                    consents.add(new CustomPurposeConsent(
                            consentsJSON.getJSONObject(i).getString("_id"),
                            consentsJSON.getJSONObject(i).getString("name")
                    ));
                    break;
                }
            }
        }
        return consents;
    }

    void getCustomConsents(String consentUUID, String euConsent, String propertyId, String[] vendorIds, ConsentLib.OnLoadComplete onLoadComplete) {
        String url = customConsentsUrl(consentUUID, euConsent, propertyId, vendorIds);
        http.get(url, new ResponseHandler(url, onLoadComplete) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                HashSet<Consent> consents = new HashSet<>();

                try {
                    consents.addAll(getConsentFromResponse(response, "consentedVendors"));
                    consents.addAll(getConsentFromResponse(response, "consentedPurposes"));

                    onLoadComplete.onSuccess(consents);
                } catch (JSONException e) {
                    onFailure(statusCode, headers, e, response);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(LOG_TAG, "Failed to load resource " + url + " due to " + statusCode + ": " + responseString);
                onLoadComplete.onFailure(new ConsentLibException(throwable.getMessage()));

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d(LOG_TAG, "Failed to load resource " + url + " due to " + statusCode + ": " + errorResponse);
                onLoadComplete.onFailure(new ConsentLibException(throwable.getMessage()));
            }
        });
    }
}
