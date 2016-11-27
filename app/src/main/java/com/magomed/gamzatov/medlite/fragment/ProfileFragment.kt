package com.magomed.gamzatov.medlite.fragment

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.magomed.gamzatov.medlite.R
import com.magomed.gamzatov.medlite.adapter.RVHistoryAdapter
import com.magomed.gamzatov.medlite.model.GetVisit
import com.magomed.gamzatov.medlite.model.Profile
import com.magomed.gamzatov.medlite.network.GetVisitRequest
import com.magomed.gamzatov.medlite.network.ProfileRequest
import com.magomed.gamzatov.medlite.network.ServiceGenerator
import kotlinx.android.synthetic.main.fragment_profile.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileFragment : Fragment() {

    companion object {
        fun newInstance(): Fragment {
            val frag = ProfileFragment()
            val args = Bundle()
            frag.arguments = args
            return frag
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val profileRequest = ServiceGenerator.createService(ProfileRequest::class.java)
        val pref = PreferenceManager.getDefaultSharedPreferences(activity.baseContext)
        val call = profileRequest.get(pref.getString("cookie", ""), pref.getString("isMedic", ""))
        call.enqueue(object: Callback<Profile> {
            override fun onResponse(call: Call<Profile>?, response: Response<Profile>?) {
                if (response?.body() != null) {
                    person_name.text = response?.body()?.name
                    person_age.text = response?.body()?.phone
                    person_email.text = response?.body()?.email
                }
            }

            override fun onFailure(call: Call<Profile>?, t: Throwable?) {
                Log.d("onFailure", t.toString())
            }

        })

        return inflater?.inflate(R.layout.fragment_profile, container, false) as View
    }

}