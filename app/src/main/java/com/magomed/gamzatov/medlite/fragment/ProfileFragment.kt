package com.magomed.gamzatov.medlite.fragment

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageLoader
import com.magomed.gamzatov.medlite.R
import com.magomed.gamzatov.medlite.adapter.RVHistoryAdapter
import com.magomed.gamzatov.medlite.model.GetVisit
import com.magomed.gamzatov.medlite.model.Profile
import com.magomed.gamzatov.medlite.network.GetVisitRequest
import com.magomed.gamzatov.medlite.network.ProfileRequest
import com.magomed.gamzatov.medlite.network.ServiceGenerator
import com.magomed.gamzatov.medlite.network.VolleySingleton
import kotlinx.android.synthetic.main.fragment_profile.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileFragment : Fragment() {

    companion object {
    var imageLoader: ImageLoader? = null

        fun newInstance(): Fragment {
            val frag = ProfileFragment()
            val args = Bundle()
            frag.arguments = args
            val volleySingleton = VolleySingleton.getsInstance()
            imageLoader = volleySingleton?.getImageLoader()
            return frag
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val profileRequest = ServiceGenerator.createService(ProfileRequest::class.java)
        val pref = PreferenceManager.getDefaultSharedPreferences(activity.baseContext)
        val call = profileRequest.get(pref.getString("cookie", ""), pref.getString("isMedic", ""), null)
        call.enqueue(object: Callback<Profile> {
            override fun onResponse(call: Call<Profile>?, response: Response<Profile>?) {
                if (response?.body() != null) {
                    val profile = response?.body()!!
                    val isMedic = pref.getString("isMedic", "")
                    val urlCert: String
                    val url: String

                    if(isMedic == "true") {
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

                }
            }

            override fun onFailure(call: Call<Profile>?, t: Throwable?) {
                Log.d("onFailure", t.toString())
            }

        })

        return inflater?.inflate(R.layout.fragment_profile, container, false) as View
    }

}