package com.sourcepoint.gdpr_cmplibrary;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.sourcepoint.gdpr_cmplibrary.exception.*;
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

    private OkHttpClient httpClient;
    private static final String wrapper_url = "https://cdn.privacy-mgmt.com/wrapper/tcfv2/v1/gdpr/";
    private static final String baseMsgUrl = wrapper_url + "message-url?inApp=true";
    private static final String baseNativeMsgUrl = wrapper_url + "native-message?inApp=true";
    private static final String baseSendConsentUrl = wrapper_url + "consent?inApp=true";
    private static final String baseSendCustomConsentsUrl = wrapper_url +  "custom-consent?inApp=true";

    private String requestUUID = "";

    SourcePointClientConfig config;

    ConnectivityManager connectivityManager;

    private Logger logger;

    private String getRequestUUID(){
        if(!requestUUID.isEmpty()) return requestUUID;
        requestUUID =  UUID.randomUUID().toString();
        return requestUUID;
    }

    SourcePointClient(OkHttpClient httpClient, SourcePointClientConfig config, ConnectivityManager connectivityManager, Logger logger) {
        this.httpClient = httpClient;
        this.config = config;
        this.connectivityManager = connectivityManager;
        this.logger = logger;
    }

    private boolean hasLostInternetConnection() {
        if (this.connectivityManager == null) {
            return true;
        }
        NetworkInfo activeNetwork = this.connectivityManager.getActiveNetworkInfo();
        return activeNetwork == null || !activeNetwork.isConnectedOrConnecting();
    }

    void getMessage(boolean isNative, String consentUUID, String meta, String euconsent, GDPRConsentLib.OnLoadComplete onLoadComplete) throws ConsentLibException {
        if(hasLostInternetConnection()){
            throw new ConsentLibException.NoInternetConnectionException();
        }


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
                logger.error(new InvalidResponseWebMessageException(e, "Fail to get message from: " + url));
                Log.d(LOG_TAG, "Failed to load resource " + url + " due to " +   "url load failure :  " + e.getMessage());
                onLoadComplete.onFailure(new ConsentLibException(e, "Fail to get message from: " + url));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    String messageJson = response.body().string();
                    Log.i(LOG_TAG , messageJson);
                    onLoadComplete.onSuccess(messageJson);
                }else {
                    Log.d(LOG_TAG, "Failed to load resource " + url + " due to " + response.code() + ": " + response.message());
                    onLoadComplete.onFailure(new ConsentLibException("Fail to get message from: " + url));
                    logger.error(new InvalidResponseWebMessageException("Fail to get message from: " + url));
                }
            }
        });
    }

    String messageUrl(boolean isNative) {
        return isNative ? baseNativeMsgUrl : baseMsgUrl;
    }

    String customConsentsUrl(){
        return baseSendCustomConsentsUrl;
    }

    private JSONObject messageParams(String consentUUID, String meta, String euconsent) throws ConsentLibException {

        try {
            JSONObject params = new JSONObject();
            params.put("accountId", config.prop.getAccountId());
            params.put("euconsent", euconsent);
            params.put("propertyId", config.prop.getPropertyId());
            params.put("requestUUID", getRequestUUID());
            params.put("uuid", consentUUID);
            params.put("meta", meta);
            params.put("propertyHref", "https://" + config.prop.getPropertyName());
            params.put("campaignEnv", config.isStagingCampaign ? "stage" : "public");
            params.put("targetingParams", config.targetingParams);
            params.put("authId", config.authId);
            Log.i(LOG_TAG, params.toString());
            return params;
        } catch (JSONException e) {
            e.printStackTrace();
            logger.error(new InvalidResponseConsentException(e, "Error building message bodyJson in sourcePointClient"));
            throw new ConsentLibException(e, "Error building message bodyJson in sourcePointClient");
        }
    }

    String consentUrl(){
        return baseSendConsentUrl;
    }


    void sendConsent(JSONObject params, GDPRConsentLib.OnLoadComplete onLoadComplete) throws ConsentLibException {
        if (hasLostInternetConnection())
            throw new ConsentLibException.NoInternetConnectionException();

        String url = consentUrl();
        try {
            params.put("requestUUID", getRequestUUID());
        } catch (JSONException e) {
            logger.error(new InvalidRequestException(e, "Error adding param requestUUID."));
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
                logger.error(new InvalidRequestException(e, "Fail to send consent to: " + url));
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
                    logger.error(new InvalidResponseConsentException("Fail to send consent to: " + url));
                }
            }
        });
    }

    void sendCustomConsents(JSONObject params, GDPRConsentLib.OnLoadComplete onLoadComplete) throws ConsentLibException {
        if (hasLostInternetConnection())
            throw new ConsentLibException.NoInternetConnectionException();

        String url = customConsentsUrl();
        try {
            params.put("requestUUID", getRequestUUID());
        } catch (JSONException e) {
            logger.error(new InvalidRequestException(e, "Error adding param requestUUID."));
            throw new ConsentLibException(e, "Error adding param requestUUID.");
        }
        Log.d(LOG_TAG, "Sending custom consents to: " + url);
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
                logger.error(new InvalidRequestException(e, "Fail to send custom consent to: " + url));
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
                    logger.error(new InvalidResponseCustomConsent("Fail to send custom consent to: " + url));
                }
            }
        });
    }
}
