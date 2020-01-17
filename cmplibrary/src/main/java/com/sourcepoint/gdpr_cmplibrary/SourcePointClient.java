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

    private static final String baseCmpUrl = "https://sourcepoint.mgr.consensu.org";

    private static final String baseMsgUrl = "https://wrapper-api.sp-prod.net/gdpr/message-url";

    private static final String baseSendConsentUrl = "https://wrapper-api.sp-prod.net/gdpr/consent";

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

    void getMessage(String consentUUID, String meta, GDPRConsentLib.OnLoadComplete onLoadComplete) throws ConsentLibException {
        //TODO inject real params to messageUrl
        String url = messageUrl();
        try {
            http.post(null, url, new StringEntity(messageParams(consentUUID, meta).toString()), "application/json", new ResponseHandler(url, onLoadComplete) {
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
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new ConsentLibException(e, "Error trying to stringify bodyJson on getMessage in sourcePointClient.");
        }
    }

    private String messageUrl() {
        HashSet<String> params = new HashSet<>();
        params.add("stage=" + (isStaging ? "stage" : "prod"));
        return baseMsgUrl + "?" + TextUtils.join("&", params);
    }

    private JSONObject messageParams(String consentUUID, String meta) throws ConsentLibException {

        try {
            JSONObject params = new JSONObject();
            params.put("accountId", accountId);
            params.put("propertyId", propertyId);
            params.put("requestUUID", requestUUID);
            params.put("uuid", consentUUID);
            params.put("meta", meta);
            params.put("propertyHref", "https://" + property);
            params.put("campaignEnv", isStagingCampaign ? "stage" : "public");
            params.put("targetingParams", targetingParams);
            params.put("authId", authId);
            params.put("resolved", false);
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
        try {
            params.put("requestUUID", getRequestUUID());
        } catch (JSONException e) {
            throw new ConsentLibException(e, "Error adding param requestUUID to sendConsentBody.");
        }
        StringEntity entity = null;
        try {
            entity = new StringEntity(params.toString());
        } catch (UnsupportedEncodingException e) {
            throw new ConsentLibException(e, "Error stringifing params for sending consent.");
        }
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
