package com.magomed.gamzatov.medlite.network

import com.magomed.gamzatov.medlite.model.GetVisit
import com.magomed.gamzatov.medlite.model.Message
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ConfirmVisitRequest {
    @POST("confirmVisit")
    fun postJSON(@Header("token") token: String, @Header("isMedic") isMedic: String, @Body body: GetVisit): Call<Message>
}
