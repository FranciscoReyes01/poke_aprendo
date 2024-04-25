package com.example.pokeaprendo.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    var client = OkHttpClient.Builder()
        .connectTimeout(100, TimeUnit.SECONDS)
        .readTimeout(100,TimeUnit.SECONDS)
        .build();

    private val retrofit = Retrofit
        .Builder()
        .baseUrl("https://pokeapi.co/api/v2/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    val consumirApi = retrofit.create(ApiService::class.java)
}