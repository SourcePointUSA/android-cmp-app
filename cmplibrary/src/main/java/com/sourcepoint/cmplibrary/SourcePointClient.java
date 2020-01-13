package com.sourcepoint.cmplibrary;

import android.text.TextUtils;
import android.util.Log;

import com.google.common.annotations.VisibleForTesting;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

class SourcePointClient {
    private static final String LOG_TAG = "SOURCE_POINT_CLIENT";

    private static AsyncHttpClient http = new AsyncHttpClient();

    private static final String baseCmpUrl = "https://sourcepoint.mgr.consensu.org";

    private static final String baseMsgUrl = "https://wrapper-api.sp-prod.net/ccpa/message-url";

    private static final String baseSendConsentUrl = "https://wrapper-api.sp-prod.net/ccpa/consent";

    private int accountId;
    private String property;
    private int propertyId;
    private Boolean isStagingCampaign;
    private String requestUUID = "";

    private String getRequestUUID(){
        if(!requestUUID.isEmpty()) return requestUUID;
        requestUUID =  UUID.randomUUID().toString();
        return requestUUID;
    }

    class ResponseHandler extends JsonHttpResponseHandler {
        GDPRConsentLib.OnLoadComplete onLoadComplete;
        String url;

        ResponseHandler(String url, GDPRConsentLib.OnLoadComplete onLoadComplete) {
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
            int accountID,
            String property,
            int propertyId,
            boolean isStagingCampaign
    ) {
        this.isStagingCampaign = isStagingCampaign;
        this.accountId = accountID;
        this.propertyId = propertyId;
        this.property = property;
    }

    private String GDPRStatusUrl() {
        return baseCmpUrl + "/consent/v2/gdpr-status";
    }

    @VisibleForTesting
    void setHttpDummy(AsyncHttpClient httpClient) {
        http = httpClient;
    }

    void getGDPRStatus(GDPRConsentLib.OnLoadComplete onLoadComplete) {
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

    void getMessage(String consentUUID, String meta, GDPRConsentLib.OnLoadComplete onLoadComplete) {
        //TODO inject real params to messageUrl
        String url = messageUrl(consentUUID, meta);
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

    private String messageUrl(String consentUUID, String meta) {
        HashSet<String> params = new HashSet<>();
        params.add("propertyId=" + propertyId);
        params.add("accountId=" + accountId);
        params.add("propertyHref=http://" + property);
        params.add("requestUUID=" + requestUUID);
        params.add("alwaysDisplayDNS=" + "false");
        if(consentUUID != null) {
            params.add("uuid=" + consentUUID);
            // cannot send meta without uuid for some mysterious reason
            if(meta != null) params.add("meta=" + meta);
        }
        return baseMsgUrl + "?" + TextUtils.join("&", params);
    }

    private String consentUrl(){
        return baseSendConsentUrl;
    }


    void sendConsent(JSONObject params, GDPRConsentLib.OnLoadComplete onLoadComplete) throws UnsupportedEncodingException, JSONException {
        String url = consentUrl();
        params.put("requestUUID", getRequestUUID());
        StringEntity entity = new StringEntity(params.toString());
        http.post(null, url, entity, "application/json",  new ResponseHandler(url, onLoadComplete) {
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
}
