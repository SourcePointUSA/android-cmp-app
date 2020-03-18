package com.sourcepoint.gdpr_cmplibrary;

import android.text.TextUtils;
import android.util.Log;

import com.google.common.annotations.VisibleForTesting;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

class SourcePointClient {
    private static final String LOG_TAG = "SOURCE_POINT_CLIENT";

    private static AsyncHttpClient http = new AsyncHttpClient();

    private static final String baseMsgUrl = "https://wrapper-api.sp-prod.net/tcfv2/v1/gdpr/message-url?inApp=true";
    private static final String baseNativeMsgUrl = "https://wrapper-api.sp-prod.net/tcfv2/v1/gdpr/native-message?inApp=true";
    private static final String baseSendConsentUrl = "https://wrapper-api.sp-prod.net/tcfv2/v1/gdpr/consent?inApp=true";

    private int accountId;
    private String property;
    private int propertyId;
    private Boolean isStagingCampaign, isStaging;
    private String requestUUID = "";
    private String targetingParams, authId;

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

    class MessageResponseHandler extends JsonHttpResponseHandler {

    }

    SourcePointClient(
            int accountID,
            String property,
            int propertyId,
            boolean isStagingCampaign,
            boolean isStaging,
            String targetingParams,
            String authId
    ) {
        this.isStagingCampaign = isStagingCampaign;
        this.isStaging = isStaging;
        this.accountId = accountID;
        this.propertyId = propertyId;
        this.property = property;
        this.targetingParams = targetingParams;
        this.authId = authId;
    }

    @VisibleForTesting
    void setHttpDummy(AsyncHttpClient httpClient) {
        http = httpClient;
    }

    void getMessage(boolean isNative, String consentUUID, String meta, String euconsent, GDPRConsentLib.OnLoadComplete onLoadComplete) throws ConsentLibException {
        String url = messageUrl(isNative);
        Log.d(LOG_TAG, "Getting message from: " + url);
        try {
            http.post(null, url, new StringEntity(messageParams(consentUUID, meta, euconsent).toString()), "application/json", new ResponseHandler(url, onLoadComplete) {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.i(LOG_TAG, response.toString());
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
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new ConsentLibException(e, "Error trying to stringify bodyJson on getMessage in sourcePointClient.");
        }
    }

    private String messageUrl(boolean isNative) {
        return isNative ? baseNativeMsgUrl : baseMsgUrl;
    }

    private JSONObject messageParams(String consentUUID, String meta, String euconsent) throws ConsentLibException {

        try {
            JSONObject params = new JSONObject();
            params.put("accountId", accountId);
            params.put("euconsent", euconsent);
            params.put("propertyId", propertyId);
            params.put("requestUUID", getRequestUUID());
            params.put("uuid", consentUUID);
            params.put("meta", meta);
            params.put("propertyHref", "https://" + property);
            params.put("campaignEnv", isStagingCampaign ? "stage" : "public");
            params.put("targetingParams", targetingParams);
            params.put("authId", authId);
            Log.i(LOG_TAG, params.toString());
            return params;
        } catch (JSONException e) {
            e.printStackTrace();
            throw new ConsentLibException(e, "Error bulding message bodyJson in sourcePointClient");
        }
    }

    private String consentUrl(){
        return baseSendConsentUrl;
    }


    void sendConsent(JSONObject params, GDPRConsentLib.OnLoadComplete onLoadComplete) throws ConsentLibException {
        String url = consentUrl();
        Log.d(LOG_TAG, "Sending consent to: " + url);
        try {
            params.put("requestUUID", getRequestUUID());
        } catch (JSONException e) {
            throw new ConsentLibException(e, "Error adding param requestUUID to sendConsentBody.");
        }
        StringEntity entity = null;
        try {
            Log.i(LOG_TAG, params.toString());
            entity = new StringEntity(params.toString());
        } catch (UnsupportedEncodingException e) {
            throw new ConsentLibException(e, "Error stringifing params for sending consent.");
        }
        http.post(null, url, entity, "application/json",  new ResponseHandler(url, onLoadComplete) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i(LOG_TAG, response.toString());
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
