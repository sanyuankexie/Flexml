package com.guet.flexbox.preview;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MockService {
    @GET("/data")
    Call<Map<String, Object>> data();

    @GET("/layout")
    Call<byte[]> layout();
}
