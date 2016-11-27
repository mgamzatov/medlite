package com.magomed.gamzatov.medlite.network

import com.magomed.gamzatov.medlite.model.Message
import com.magomed.gamzatov.medlite.model.Rating
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


interface RatingRequest {
    @POST("setRating")
    fun postJSON(@Header("token") token: String, @Header("isMedic") isMedic: String, @Body body: Rating): Call<Message>
}