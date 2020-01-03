package com.guet.flexbox.playground.model

import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

interface ImageService {

    @GET("/api/api.php?return=json")
    fun get(): Call<ACGImage>

    companion object {

        val random: ImageService = Retrofit.Builder()
                .baseUrl("https://api.ixiaowai.cn")
                .client(OkHttpClient.Builder()
                        .connectTimeout(1L, TimeUnit.SECONDS)
                        .build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ImageService::class.java)
    }
}