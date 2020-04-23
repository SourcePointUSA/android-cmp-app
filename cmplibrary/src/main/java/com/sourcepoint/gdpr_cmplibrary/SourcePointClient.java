package com.sourcepoint.gdpr_cmplibrary;

import android.util.Log;

import com.google.common.annotations.VisibleForTesting;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import cz.msebera.android.httpclient.Header;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

class SourcePointClient {
    private static final String LOG_TAG = "SOURCE_POINT_CLIENT";

    private static AsyncHttpClient http = new AsyncHttpClient();

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

        Retrofit retrofit = new Retrofit.Builder().baseUrl(url+"/").build();

        SourcePointInterface client = retrofit.create(SourcePointInterface.class);
        Call<ResponseBody> call = client.getGDPRStatus();

       call.enqueue(new Callback<ResponseBody>() {
           @Override
           public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
               Log.d("retrofit" , response.toString());
               try {
                   onLoadComplete.onSuccess(new JSONObject(response.body().string()).getString("gdprApplies"));
               } catch (Exception e) {
                   e.printStackTrace();
                   onLoadComplete.onFailure( new ConsentLibException(e, "Error trying to stringify bodyJson on getMessage in sourcePointClient."));
               }
           }

           @Override
           public void onFailure(Call<ResponseBody> call, Throwable throwable) {
               onLoadComplete.onFailure(new ConsentLibException(throwable.getMessage()));
           }
       });
    }

    void getMessage(boolean isNative, String consentUUID, String meta, GDPRConsentLib.OnLoadComplete onLoadComplete) throws ConsentLibException {
        String url = messageUrl(isNative);
        Log.d(LOG_TAG, "Getting message from: " + url);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(url+"/").build();

        SourcePointInterface client = retrofit.create(SourcePointInterface.class);

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),messageParams(consentUUID, meta).toString());
        Call<ResponseBody> call = client.getMessage(body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("retrofit" , response.toString());
                try {
                    onLoadComplete.onSuccess(new JSONObject(response.body().string()));
                } catch (Exception e) {
                    e.printStackTrace();
                    onLoadComplete.onFailure( new ConsentLibException(e, "Error trying to stringify consentjson on sendConsent in sourcePointClient."));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                onLoadComplete.onFailure(new ConsentLibException(throwable.getMessage()));
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

        Retrofit retrofit = new Retrofit.Builder().baseUrl(url+"/").build();

        SourcePointInterface client = retrofit.create(SourcePointInterface.class);

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),params.toString());
        Call<ResponseBody> call = client.sendConsent(body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("retrofit" , response.toString());
                try {
                    onLoadComplete.onSuccess(new JSONObject(response.body().string()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                onLoadComplete.onFailure(new ConsentLibException(throwable.getMessage()));
            }
        });
    }
}
