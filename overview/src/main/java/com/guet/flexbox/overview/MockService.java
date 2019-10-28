package com.guet.flexbox.overview;

import com.guet.flexbox.WidgetInfo;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MockService {
    @GET("/data")
    Call<Map<String, Object>> data();

    @GET("/layout")
    Call<WidgetInfo> layout();
}
