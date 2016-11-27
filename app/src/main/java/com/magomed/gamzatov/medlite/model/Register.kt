package com.magomed.gamzatov.medlite.model

data class Register(val name: String,
                    val email: String,
                    val password: String,
                    var phone: String?,
                    var image: String?,
                    var callCharge: String?,
                    var certificate: String?)