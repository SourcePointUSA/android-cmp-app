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

    private static final String baseMsgUrl = "https://fake-wrapper-api.herokuapp.com/message";

    private URL mmsUrl, cmpUrl, messageUrl;
    private EncodedParam accountId, property, propertyId;
    private Boolean stagingCampaign, isShowPM;

    class ResponseHandler extends JsonHttpResponseHandler {
        //TODO: decouple from consentLib -> interface OnloadComplete should be in a separate file out of consentLib class
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

    class MessageResponseHandler extends JsonHttpResponseHandler {

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

    //TODO: make constant for base adress and extract url from user params
    private String messageUrl(String propertyId, String accountId, String propertyHref) {
        return baseMsgUrl;
    }

    @VisibleForTesting
    void setHttpDummy(AsyncHttpClient httpClient) {
        http = httpClient;
    }


    void getMessage(ConsentLib.OnLoadComplete onLoadComplete) {
        //TODO inject real params to messageUrl
        String url = messageUrl("", "", "");
        http.get(url, new ResponseHandler(url, onLoadComplete) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                onLoadComplete.onSuccess(response);
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
