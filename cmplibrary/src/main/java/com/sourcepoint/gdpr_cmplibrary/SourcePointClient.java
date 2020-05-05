package com.sourcepoint.gdpr_cmplibrary;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class SourcePointClient {
    private static final String LOG_TAG = "SOURCE_POINT_CLIENT";

    private OkHttpClient httpClient = new OkHttpClient();

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

    void getMessage(boolean isNative, String consentUUID, String meta, String euconsent, GDPRConsentLib.OnLoadComplete onLoadComplete) throws ConsentLibException {
        String url = messageUrl(isNative);
        Log.d(LOG_TAG, "Getting message from: " + url);

        final MediaType mediaType= MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, messageParams(consentUUID, meta, euconsent).toString());

        Request request = new Request.Builder().url(url).post(body)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(LOG_TAG, "Failed to load resource " + url + " due to " +   "url load failure :  " + e.getMessage());
                onLoadComplete.onFailure(new ConsentLibException(e, "Fail to send consent to: " + url));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    String messageJson = response.body().string();
                    Log.i(LOG_TAG , messageJson);
                    onLoadComplete.onSuccess(messageJson);
                }else {
                    Log.d(LOG_TAG, "Failed to load resource " + url + " due to " + response.code() + ": " + response.message());
                    onLoadComplete.onFailure(new ConsentLibException(response.message()));
                }
            }
        });
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
        try {
            params.put("requestUUID", getRequestUUID());
        } catch (JSONException e) {
            throw new ConsentLibException(e, "Error adding param requestUUID.");
        }
        Log.d(LOG_TAG, "Sending consent to: " + url);
        Log.d(LOG_TAG, params.toString());


        final MediaType mediaType= MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, params.toString());

        Request request = new Request.Builder().url(url).post(body)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(LOG_TAG, "Failed to load resource " + url + " due to " +   "url load failure :  " + e.getMessage());
                onLoadComplete.onFailure(new ConsentLibException(e, "Fail to send consent to: " + url));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    String messageJson = response.body().string();
                    Log.i(LOG_TAG , messageJson);
                    onLoadComplete.onSuccess(messageJson);
                }else {
                    Log.d(LOG_TAG, "Failed to load resource " + url + " due to " + response.code() + ": " + response.message());
                    onLoadComplete.onFailure(new ConsentLibException("Fail to send consent to: " + url));
                }
            }
        });
    }
}
