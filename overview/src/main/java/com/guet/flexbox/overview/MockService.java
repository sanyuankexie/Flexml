package com.guet.flexbox.overview;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface MockService {
    @GET("/data")
    Call<Map<String, Object>> data();

    @GET("/layout")
    Call<ResponseBody> layout();
}
