package com.magomed.gamzatov.medlite.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Base64
import android.util.Log
import android.view.View
import com.magomed.gamzatov.medlite.R
import com.magomed.gamzatov.medlite.model.Login
import com.magomed.gamzatov.medlite.model.Message
import com.magomed.gamzatov.medlite.model.Register
import com.magomed.gamzatov.medlite.network.LoginRequest
import com.magomed.gamzatov.medlite.network.RegisterRequest
import com.magomed.gamzatov.medlite.network.ServiceGenerator
import com.vansuita.library.IPickResult
import com.vansuita.library.PickImageDialog
import com.vansuita.library.PickSetup
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.longToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class RegisterActivity : AppCompatActivity(), IPickResult.IPickResultBitmap {

    var imagePicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btn_signup.setOnClickListener {
            register()
        }

        link_login.setOnClickListener {
            finish()
        }

        addPhoto.setOnClickListener {
            PickImageDialog.on(this@RegisterActivity, PickSetup())
        }

        isMedic.setOnClickListener {
            if(isMedic.isChecked) {
                input_callCharge.visibility = View.VISIBLE
            } else {
                input_callCharge.visibility = View.GONE
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 111) {
            if (resultCode == Activity.RESULT_OK) {
                registerOnServer()
                return
            } else {
                longToast("Your answer is wrong!")
                finish()
                return
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun registerOnServer() {
        val progressDialog = ProgressDialog(this@RegisterActivity)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage("Creating Account...")
        progressDialog.show()

        val name = input_name.text.toString()
        val email = input_email.text.toString()
        val password = input_password.text.toString()
        val phone = input_phone.text.toString()
        val isMedic = isMedic.isChecked.toString()
        val callCharge = input_callCharge.text.toString()
        val photo = if (imagePicked) (addPhoto.drawable as BitmapDrawable).bitmap.encodeToBase64() else ""

        val registerRequest = ServiceGenerator.createService(RegisterRequest::class.java)

        val call = registerRequest.postJSON(isMedic, Register(name, email, password, phone, photo, callCharge))
        call.enqueue(object : Callback<Message> {
            override fun onResponse(call: Call<Message>?, response: Response<Message>?) {
                if (response != null && response.body() != null) {
                    Log.d("register onResponse", response.body().message)
                    onRegisterSuccess(email, password, progressDialog, name, isMedic)
                }
            }

            override fun onFailure(call: Call<Message>?, t: Throwable?) {
                longToast(t.toString())
                Log.d("onFailure", t.toString())
                progressDialog.dismiss()
            }
        })
    }

    override fun onPickImageResult(bitmap: Bitmap?) {
        if(bitmap!=null) {
            addPhoto.setImageBitmap(bitmap)
            imagePicked = true
        }
    }

    private fun register() {
        if(!validate()) {
            return
        }

        if(isMedic.isChecked.toString() == "true") {
            startActivityForResult(Intent(this@RegisterActivity, PuzzleActivity::class.java), 111)
        } else {
            registerOnServer()
        }
    }

    private fun onRegisterSuccess(email: String, password: String, progressDialog: ProgressDialog, name: String, isMedic: String) {
        login(email, isMedic, name, password, progressDialog)
    }

    private fun login(email: String, isMedic: String, name: String, password: String, progressDialog: ProgressDialog) {
        val loginRequest = ServiceGenerator.createService(LoginRequest::class.java)
        val call = loginRequest.postJSON(isMedic, Login(email, password))
        call.enqueue(object : Callback<Message> {
            override fun onResponse(call: Call<Message>?, response: Response<Message>?) {
                if (response != null && response.body() != null) {
                    longToast(response.body().message)
                    Log.d("login onResponse", response.body().message)

                    val pref = PreferenceManager.getDefaultSharedPreferences(baseContext)
                    pref.edit().putString("cookie", response.body().message)
                            .putString("email", email)
                            .putString("name", name)
                            .putString("isMedic", isMedic)
                            .apply()

                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    intent.putExtra("logged", true)
                    startActivity(intent)
                }
                progressDialog.dismiss()
            }

            override fun onFailure(call: Call<Message>?, t: Throwable?) {
                longToast(t.toString())
                Log.d("onFailure", t.toString())
                progressDialog.dismiss()
            }
        })
    }

    private fun validate(): Boolean {
        var valid = true

        val name = input_name.text.toString()
        val email = input_email.text.toString()
        val password = input_password.text.toString()

        if (name.isEmpty()
                //|| name.length < 3
                ) {
            //input_name.error = "at least 3 characters"
            valid = false
        } else {
            input_name.error = null
        }

        if (email.isEmpty()
        //        || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        ) {
            //input_email.error = "enter a valid email address"
            valid = false
        } else {
            input_email.error = null
        }

        if (password.isEmpty()
        //        || password.length < 3
        ) {
          //  input_password.error = "at least 3 characters"
            valid = false
        } else {
            input_password.error = null
        }

        return valid
    }

    fun Bitmap.encodeToBase64(): String {
        val immagex = this
        val baos = ByteArrayOutputStream()
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        val imageEncoded = Base64.encodeToString(b, Base64.NO_WRAP)
        return imageEncoded
    }
}



