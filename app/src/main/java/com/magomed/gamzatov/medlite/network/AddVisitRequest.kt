package com.magomed.gamzatov.medlite.network

import com.magomed.gamzatov.medlite.model.Message
import com.magomed.gamzatov.medlite.model.AddVisit
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


interface AddVisitRequest {

    @POST("addVisit")
    fun postJSON(@Header("token") token: String, @Body body: AddVisit): Call<Message>
}