package com.sourcepoint.gdpr_cmplibrary;

import android.util.Log;

import com.google.common.annotations.VisibleForTesting;

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

    protected static  String baseCmpUrl = "https://sourcepoint.mgr.consensu.org";

    protected static  String baseMsgUrl = "https://wrapper-api.sp-prod.net/gdpr/message-url";
    private static final String baseNativeMsgUrl = "https://wrapper-api.sp-prod.net/gdpr/native-message";



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

    @VisibleForTesting
    protected void setReuestedUUID(String requested_uuid) {
        this.requestUUID = requested_uuid;
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

    void getGDPRStatus(GDPRConsentLib.OnLoadComplete onLoadComplete) {
        String url = GDPRStatusUrl();

        Request request = new Request.Builder()
                .url(url).build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(LOG_TAG, "Failed to load resource " + url + " due to " +   "url load failure :  " + e.getMessage());
                onLoadComplete.onFailure(new ConsentLibException(e.getMessage()));
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    String messageString = response.body().string();
                    onLoadComplete.onSuccess(messageString);
                } else {
                    Log.d(LOG_TAG, "Failed to load resource " + url + " due to " + response.code() + ": " + response.message());
                    onLoadComplete.onFailure(new ConsentLibException(response.message()));
                }
            }});
    }

    void getMessage(boolean isNative, String consentUUID, String meta, GDPRConsentLib.OnLoadComplete onLoadComplete) throws ConsentLibException {
        String url = messageUrl(isNative);
        Log.d(LOG_TAG, "Getting message from: " + url);

        final MediaType mediaType= MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, messageParams(consentUUID ,meta).toString());

        Request request = new Request.Builder().url(url).post(body)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(LOG_TAG, "Failed to load resource " + url + " due to " +   "url load failure :  " + e.getMessage());
                onLoadComplete.onFailure(new ConsentLibException(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    String messageJson = response.body().string();
                    Log.i(LOG_TAG , messageJson);
                    try {
                        onLoadComplete.onSuccess(new JSONObject(messageJson));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        onLoadComplete.onFailure( new ConsentLibException(e, "Error while converting string to josn : "+e.getMessage()));
                    }
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

    private JSONObject messageParams(String consentUUID, String meta) throws ConsentLibException {

        try {
            JSONObject params = new JSONObject();
            params.put("accountId", accountId);
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
            throw new ConsentLibException(e, "Error adding param requestUUID to sendConsentBody.");
        }

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
                onLoadComplete.onFailure(new ConsentLibException(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    String messageJson = response.body().string();
                    Log.i(LOG_TAG , messageJson);
                    try {
                        onLoadComplete.onSuccess(new JSONObject(messageJson));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        onLoadComplete.onFailure( new ConsentLibException(e, "Error while converting string to josn : "+e.getMessage()));
                    }
                }else {
                    Log.d(LOG_TAG, "Failed to load resource " + url + " due to " + response.code() + ": " + response.message());
                    onLoadComplete.onFailure(new ConsentLibException(response.message()));
                }
            }
        });
    }
}
