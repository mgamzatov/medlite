package com.magomed.gamzatov.medlite.network

import com.magomed.gamzatov.medlite.model.Profile
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header


interface ProfileRequest {
    @GET("getMedicById")
    fun get(@Header("token") token: String, @Header("isMedic") isMedic: String): Call<Profile>
}