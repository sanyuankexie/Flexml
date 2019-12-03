package com.luke.skywalker.playground;

import com.luke.skywalker.NodeInfo;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MockService {
    @GET("/data")
    Call<Map<String, Object>> data();

    @GET("/layout")
    Call<NodeInfo> layout();
}
