package com.sourcepoint.cmplibrary;

import android.text.TextUtils;
import android.util.Log;

import com.loopj.android.http.*;

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
    private EncodedParam accountId, site, encodedCmpOrigin, encodedMsgDomain;
    private Boolean stagingCampaign;

    private class ResponseHandler extends JsonHttpResponseHandler {
        ConsentLib.OnLoadComplete onLoadComplete;
        String url;

        ResponseHandler(String url, ConsentLib.OnLoadComplete onLoadComplete) {
            this.onLoadComplete = onLoadComplete;
            this.url = url;
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d(LOG_TAG, "Failed to load resource "+url+" due to "+statusCode+": "+errorResponse);
            onLoadComplete.onFailure(new ConsentLibException(throwable.getMessage()));
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            Log.d(LOG_TAG, "Failed to load resource "+url+" due to "+statusCode+": "+responseString);
            onLoadComplete.onFailure(new ConsentLibException(throwable.getMessage()));
        }
    }

    SourcePointClient(
            EncodedParam accountID,
            EncodedParam site,
            boolean stagingCampaign,
            URL mmsUrl,
            URL cmpUrl,
            URL messageUrl
    ) throws ConsentLibException.BuildException {
        this.stagingCampaign = stagingCampaign;
        this.accountId = accountID;
        this.site = site;
        this.mmsUrl = mmsUrl;
        this.cmpUrl = cmpUrl;
        this.messageUrl = messageUrl;
        this.encodedCmpOrigin = new EncodedParam("cmpUrl", cmpUrl.getHost());
        this.encodedMsgDomain = new EncodedParam("msgDomain", mmsUrl.getHost());
    }

    private String siteIdUrl() {
        return mmsUrl +"/get_site_data?"+"account_id="+accountId+"&href="+site;
    }

    private String GDPRStatusUrl() {
        return cmpUrl + "/consent/v2/gdpr-status";
    }

    private String customConsentsUrl(String consentUUID, String euConsent, String siteId, String[] vendorIds) {
        String consentParam = consentUUID == null ? "[CONSENT_UUID]" : consentUUID;
        String euconsentParam = euConsent == null ? "[EUCONSENT]" : euConsent;
        String customVendorIdString = URLEncoder.encode(TextUtils.join(",", vendorIds));

        return cmpUrl + "/consent/v2/" + siteId + "/custom-vendors?"+
                "customVendorIds=" + customVendorIdString +
                "&consentUUID=" + consentParam +
                "&euconsent=" + euconsentParam;
    }

    String messageUrl(EncodedParam targetingParams, EncodedParam debugLevel) {
        HashSet<String> params = new HashSet<>();
        params.add("_sp_accountId=" + String.valueOf(accountId));
        params.add("_sp_cmp_inApp=true");
        params.add("_sp_writeFirstPartyCookies=true");
        params.add("_sp_siteHref=" + site);
        params.add("_sp_msg_domain=" + encodedMsgDomain);
        params.add("_sp_cmp_origin=" + encodedCmpOrigin);
        params.add("_sp_msg_targetingParams=" + targetingParams);
        params.add("_sp_debug_level=" + debugLevel);
        params.add("_sp_msg_stageCampaign=" + stagingCampaign);

        String url = messageUrl + "?" + TextUtils.join("&", params);
        Log.i(LOG_TAG, "cpm url: " + url);
        return url;
    }

    void getSiteID(ConsentLib.OnLoadComplete onLoadComplete) {
        String url = siteIdUrl();
        http.get(url, new ResponseHandler(url, onLoadComplete) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    onLoadComplete.onSuccess(response.getString("site_id"));
                } catch (JSONException e) {
                    onFailure(statusCode, headers, e, response);
                }
            }
        });
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

    void getCustomConsents(String consentUUID, String euConsent, String siteId, String[] vendorIds, ConsentLib.OnLoadComplete onLoadComplete) {
        String url = customConsentsUrl(consentUUID, euConsent, siteId, vendorIds);
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
        });
    }
}
