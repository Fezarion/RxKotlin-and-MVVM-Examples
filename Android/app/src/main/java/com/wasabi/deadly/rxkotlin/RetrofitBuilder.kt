package com.wasabi.deadly.rxkotlin

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

// Lazy global init of retrofitService
val retrofitService by lazy {
    RetrofitBuilder.create()
}

class RetrofitBuilder {
    companion object {
        fun create(): RetrofitService {
            /**
             * Check base URL matches the server's IP address
             * Also check res.xml/network_security_config's IP address also matches
             */
            val baseUrl = "http://192.168.0.132:5000"
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
            return retrofit.create(RetrofitService::class.java)
        }
    }
}