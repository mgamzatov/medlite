package com.magomed.gamzatov.medlite.network

import com.magomed.gamzatov.medlite.model.Message
import com.magomed.gamzatov.medlite.model.Register
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface RegisterRequest {
    @POST("registration")
    fun postJSON(@Header("isMedic") isMedic: String, @Body body: Register): Call<Message>
}