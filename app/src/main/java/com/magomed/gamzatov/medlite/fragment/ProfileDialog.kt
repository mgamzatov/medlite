package com.magomed.gamzatov.medlite.fragment

import android.app.Dialog
import android.app.ProgressDialog
import android.media.MediaPlayer
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatButton
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.magomed.gamzatov.medlite.R
import android.widget.TextView
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageLoader
import com.magomed.gamzatov.medlite.model.AddVisit
import com.magomed.gamzatov.medlite.model.Message
import com.magomed.gamzatov.medlite.model.Profile
import com.magomed.gamzatov.medlite.network.AddVisitRequest
import com.magomed.gamzatov.medlite.network.ProfileRequest
import com.magomed.gamzatov.medlite.network.ServiceGenerator
import com.magomed.gamzatov.medlite.network.VolleySingleton
import org.jetbrains.anko.support.v4.longToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileDialog : DialogFragment() {

    companion object {
        var imageLoader: ImageLoader? = null

        fun newInstance(id: Long): ProfileDialog {
            val fragment = ProfileDialog()
            val args = Bundle()
            args.putLong("id", id)
            fragment.arguments = args
            val volleySingleton = VolleySingleton.getsInstance()
            imageLoader = volleySingleton?.getImageLoader()
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater = activity.layoutInflater
        val view = inflater.inflate(R.layout.fragment_profile, null)
        val name = view.findViewById(R.id.name) as TextView
        val rating = view.findViewById(R.id.rating) as TextView
        val phone = view.findViewById(R.id.phone) as TextView
        val charge = view.findViewById(R.id.charge) as TextView
        val avatar = view.findViewById(R.id.avatar) as ImageView
        val cert = view.findViewById(R.id.cert) as ImageView
        val callButon = view.findViewById(R.id.call) as AppCompatButton
        callButon.visibility = View.VISIBLE

        val profileRequest = ServiceGenerator.createService(ProfileRequest::class.java)
        val pref = PreferenceManager.getDefaultSharedPreferences(activity.baseContext)
        val call = profileRequest.get(pref.getString("cookie", ""), pref.getString("isMedic", ""), arguments.getLong("id"))
        call.enqueue(object: Callback<Profile> {
            override fun onResponse(call: Call<Profile>?, response: Response<Profile>?) {
                if (response?.body() != null) {
                    val profile = response?.body()!!
                    val isMedic = pref.getString("isMedic", "")
                    val urlCert: String
                    val url: String

                    if(isMedic != "true") {
                        urlCert = ServiceGenerator.API_BASE_URL + "/certificates/${profile.id}.jpg"
                        url = ServiceGenerator.API_BASE_URL + "/images/-${profile.id}.jpg"
                    } else {
                        urlCert = ""
                        url = ServiceGenerator.API_BASE_URL + "/images/${profile.id}.jpg"
                    }

                    imageLoader?.get(urlCert, object : ImageLoader.ImageListener {
                        override fun onResponse(response: ImageLoader.ImageContainer, isImmediate: Boolean) {
                            cert.setImageBitmap(response.bitmap)
                        }

                        override fun onErrorResponse(error: VolleyError) {

                        }
                    })

                    name.text = profile.name
                    rating.text = "Rating: ".plus(profile.rating.toString())
                    phone.text = "Phone: ".plus(profile.phone)
                    charge.text = "Call charge: ".plus(profile.callCharge.toString()).plus(" ₽")

                    imageLoader?.get(url, object : ImageLoader.ImageListener {
                        override fun onResponse(response: ImageLoader.ImageContainer, isImmediate: Boolean) {
                            avatar.setImageBitmap(response.bitmap)
                        }

                        override fun onErrorResponse(error: VolleyError) {

                        }
                    })

                    callButon.setOnClickListener {
                        val player = MediaPlayer.create(activity.baseContext, R.raw.heavymedic)
                        player.setOnPreparedListener(MediaPlayer::start)

                        val progressDialog = ProgressDialog(activity)
                        progressDialog.isIndeterminate = true
                        progressDialog.setMessage("Sending...")
                        progressDialog.show()

                        val visitRequest = ServiceGenerator.createService(AddVisitRequest::class.java)
                        val call2 = visitRequest.postJSON(pref.getString("cookie", ""), AddVisit(arguments.getLong("id")))
                        call2.enqueue(object: Callback<Message> {
                            override fun onResponse(call: Call<Message>?, response: retrofit2.Response<Message>?) {
                                Log.d("onResponse", response?.body().toString())
                                longToast("success")
                                progressDialog.dismiss()
                                this@ProfileDialog.dismiss()
                            }

                            override fun onFailure(call: Call<Message>?, t: Throwable?) {
                                longToast(t.toString())
                                Log.d("onFailure", t.toString())
                                progressDialog.dismiss()
                            }

                        })
                    }

                }
            }

            override fun onFailure(call: Call<Profile>?, t: Throwable?) {
                Log.d("onFailure", t.toString())
            }

        })



        val builder = AlertDialog.Builder(activity)
        builder.setView(view)

        return builder.create()
    }
}