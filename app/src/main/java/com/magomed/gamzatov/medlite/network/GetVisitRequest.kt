package com.magomed.gamzatov.medlite.network

import com.magomed.gamzatov.medlite.model.GetVisit
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import java.util.*


interface GetVisitRequest {

    @GET("getVisits")
    fun get(@Header("token") token: String, @Header("isMedic") isMedic: String, @Query("mode") mode:String?): Call<ArrayList<GetVisit>>
}