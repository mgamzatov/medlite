package com.magomed.gamzatov.medlite.network

import com.magomed.gamzatov.medlite.model.Profile
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


interface ProfileRequest {
    @GET("getMedicById")
    fun get(@Header("token") token: String?, @Header("isMedic") isMedic: String, @Query("id") id: Long?): Call<Profile>
}