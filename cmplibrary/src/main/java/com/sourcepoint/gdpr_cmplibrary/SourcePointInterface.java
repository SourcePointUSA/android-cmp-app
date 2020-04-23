package com.sourcepoint.gdpr_cmplibrary;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface SourcePointInterface {

    @GET(".")
    Call<ResponseBody> getGDPRStatus();

    @Headers({"Accept: application/json"})
    @POST(".")
    Call<ResponseBody> getMessage(@Body RequestBody params);

    @Headers({"Accept: application/json"})
    @POST(".")
    Call<ResponseBody> sendConsent(@Body RequestBody params);
}
