package com.guet.flexbox.playground

import com.guet.flexbox.TemplateNode
import retrofit2.Call
import retrofit2.http.GET

interface MockService {
    @GET("/datasource")
    fun data(): Call<Map<String, Any>>

    @GET("/template")
    fun layout(): Call<TemplateNode>
}