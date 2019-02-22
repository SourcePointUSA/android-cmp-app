package com.sourcepoint.cmplibrary;

import android.text.TextUtils;
import android.util.Log;

import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;

import cz.msebera.android.httpclient.Header;

class SourcePointClient {
    private static final String LOG_TAG = "SOURCE_POINT_CLIENT";

    private static final String DEFAULT_STAGING_MMS_URL = "https://mms.sp-stage.net";
    private static final String DEFAULT_MMS_URL = "https://mms.sp-prod.net";

    private static final String DEFAULT_INTERNAL_CMP_URL = "https://cmp.sp-stage.net";
    private static final String DEFAULT_CMP_URL = "https://sourcepoint.mgr.consensu.org";

    private static AsyncHttpClient http = new AsyncHttpClient();

    private EncodedParam accountId, site;
    private Boolean staging;

    private class ResponseHandler extends JsonHttpResponseHandler {
        ConsentLib.OnLoadComplete onLoadComplete;
        String url;

        ResponseHandler(String url, ConsentLib.OnLoadComplete onLoadComplete) {
            this.onLoadComplete = onLoadComplete;
            this.url = url;
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d(LOG_TAG, "Failed to load resource "+url);
            onLoadComplete.onFailure(new ConsentLibException(throwable.getMessage()));
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            Log.d(LOG_TAG, "Failed to load resource "+url);
            onLoadComplete.onFailure(new ConsentLibException(throwable.getMessage()));
        }
    }

    private static class EncodedParam {
        private String value;

        EncodedParam(String name, String value) throws ConsentLibException.ApiException {
            this.value = encode(name, value);
        }

        private String encode(String attrName, String attrValue) throws ConsentLibException.ApiException {
            try {
                return URLEncoder.encode(attrValue, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new ConsentLibException().new ApiException("Unable to encode "+attrName+", with the value: "+attrValue+" when instantiating SourcePointClient");
            }
        }

        String getValue() { return value; }

        @Override
        public String toString() { return getValue(); }
    }

//    TODO: take page into account when building the site url
//    TODO: take custom domains into account (CMP, MMS, InAppMessaging)

    SourcePointClient(String accountID, String site, Boolean staging) throws ConsentLibException.ApiException {
        this.staging = staging;
        this.accountId = new EncodedParam("AccountID", accountID);
        this.site = new EncodedParam("SiteName", site);
    }

    private String siteIdUrl() {
        return (staging?DEFAULT_STAGING_MMS_URL:DEFAULT_MMS_URL)+"/get_site_data?"
                +"account_id="+accountId
                +"&href="+site;
    }

    private String CMPUrl() {
        return staging ? DEFAULT_INTERNAL_CMP_URL : DEFAULT_CMP_URL;
    }

    private String GDPRStatusUrl() {
        return CMPUrl() + "/consent/v2/gdpr-status";
    }

    private String customConsentsUrl(String consentUUID, String euConsent, String siteId, String[] vendorIds) {
        String consentParam = consentUUID == null ? "[CONSENT_UUID]" : consentUUID;
        String euconsentParam = euConsent == null ? "[EUCONSENT]" : euConsent;
        String customVendorIdString = URLEncoder.encode(TextUtils.join(",", vendorIds));

        return CMPUrl() + "/consent/v2/" + siteId + "/custom-vendors?"+
                "customVendorIds=" + customVendorIdString +
                "&consentUUID=" + consentParam +
                "&euconsent=" + euconsentParam;
    }

    public void getSiteID(ConsentLib.OnLoadComplete onLoadComplete) {
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

    public void getGDPRStatus(ConsentLib.OnLoadComplete onLoadComplete) {
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

    private HashSet<ConsentLib.Consent> getConsentFromResponse(JSONObject response, String type) throws JSONException {
        JSONArray consentsJSON = response.getJSONArray(type);
        HashSet<ConsentLib.Consent> consents = new HashSet<>();
        for (int i = 0; i < consentsJSON.length(); i++) {
            switch (type) {
                case "consentedVendors": {
                    consents.add(new ConsentLib.CustomVendorConsent(
                            consentsJSON.getJSONObject(i).getString("_id"),
                            consentsJSON.getJSONObject(i).getString("name")
                    ));
                    break;
                }
                case "consentedPurposes": {
                    consents.add(new ConsentLib.CustomPurposeConsent(
                            consentsJSON.getJSONObject(i).getString("_id"),
                            consentsJSON.getJSONObject(i).getString("name")
                    ));
                    break;
                }
            }
        }
        return consents;
    }

    public void getCustomConsents(String consentUUID, String euConsent, String siteId, String[] vendorIds, ConsentLib.OnLoadComplete onLoadComplete) {
        String url = customConsentsUrl(consentUUID, euConsent, siteId, vendorIds);
        http.get(url, new ResponseHandler(url, onLoadComplete) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                HashSet<ConsentLib.Consent> consents = new HashSet<>();

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
