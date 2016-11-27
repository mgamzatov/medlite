package com.magomed.gamzatov.medlite.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ServiceGenerator {

    companion object {
        val API_BASE_URL = "http://85.188.15.131:8080"
        val API_PREFIX_URL = "/"


        private val httpClient = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).connectTimeout(30, TimeUnit.SECONDS)

        private val builder = Retrofit.Builder().baseUrl(API_BASE_URL+API_PREFIX_URL).addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))

        fun <S> createService(serviceClass: Class<S>): S {
            val logging =  HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            httpClient.addInterceptor(logging)
            val retrofit = builder.client(httpClient.build()).build()
            return retrofit.create(serviceClass)
        }
    }
}