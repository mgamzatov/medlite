package com.magomed.gamzatov.medlite.network

import com.magomed.gamzatov.medlite.model.Login
import com.magomed.gamzatov.medlite.model.Message
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


interface LoginRequest {
    @POST("authentication")
    fun postJSON(@Header("isMedic") isMedic: String, @Body body: Login): Call<Message>
}