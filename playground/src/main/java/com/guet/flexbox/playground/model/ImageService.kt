package com.guet.flexbox.playground.model

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ImageService {

    @GET("/random.php?return=json")
    fun get(): Call<ACGImage>

    companion object {
        val random: ImageService = Retrofit.Builder()
                .baseUrl("http://acg.nmkjwl.cn")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ImageService::class.java)
    }
}