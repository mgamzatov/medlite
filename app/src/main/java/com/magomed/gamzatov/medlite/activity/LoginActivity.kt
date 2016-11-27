package com.magomed.gamzatov.medlite.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.util.Patterns
import com.magomed.gamzatov.medlite.R
import com.magomed.gamzatov.medlite.model.Login
import com.magomed.gamzatov.medlite.model.Message
import com.magomed.gamzatov.medlite.network.LoginRequest
import com.magomed.gamzatov.medlite.network.ServiceGenerator
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.longToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_login.setOnClickListener {
            login()
        }

        link_signup.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun login() {
//        if(!validate()) {
//            return
//        }

        val progressDialog = ProgressDialog(this@LoginActivity)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage("Authenticating...")
        progressDialog.show()

        val email = input_email.text.toString()
        val password = input_password.text.toString()
        val isMedic = isMedic.isChecked.toString()

        val loginRequest = ServiceGenerator.createService(LoginRequest::class.java)
        val call = loginRequest.postJSON(isMedic, Login(email, password))
        call.enqueue(object: Callback<Message> {
            override fun onResponse(call: Call<Message>?, response: Response<Message>?) {
                onLoginSuccess(response, email, isMedic)
                progressDialog.dismiss()
            }

            override fun onFailure(call: Call<Message>?, t: Throwable?) {
                progressDialog.dismiss()
            }

        })
    }


    fun onLoginSuccess(response: Response<Message>?, email: String, isMedic: String) {
        if (response != null && response.body() != null) {
            longToast(response.message())
            Log.d("onResponse", response.message())

            val pref = PreferenceManager.getDefaultSharedPreferences(baseContext)
            pref.edit().putString("cookie", response.body().message)
                    .putString("email", email)
                    .putString("isMedic", isMedic)
                    .apply()

            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.putExtra("logged", true)
            startActivity(intent)
        }
    }

    private fun validate(): Boolean {
        var valid = true

        val email = input_email.text.toString()
        val password = input_password.text.toString()

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            input_email.error = "enter a valid email address"
            valid = false
        } else {
            input_email.error = null
        }

        if (password.isEmpty() || password.length < 3) {
            input_password.error = "at least 3 characters"
            valid = false
        } else {
            input_password.error = null
        }

        return valid
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}