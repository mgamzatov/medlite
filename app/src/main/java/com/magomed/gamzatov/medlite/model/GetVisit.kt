package com.magomed.gamzatov.medlite.model

data class GetVisit(val id: Long?, val patientId: Long?, val patientName: String?, val patientPhone: String?,
                    val medicId: Long?, val medicName: String?, val medicPhone: String?,
                    val confirmed: Int?, val rating: Int?, val date: String?)