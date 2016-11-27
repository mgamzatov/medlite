package com.magomed.gamzatov.medlite.fragment

import android.app.Dialog
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatButton
import android.util.Log
import android.widget.RatingBar
import com.magomed.gamzatov.medlite.R
import android.widget.TextView
import com.magomed.gamzatov.medlite.model.Message
import com.magomed.gamzatov.medlite.model.Profile
import com.magomed.gamzatov.medlite.model.Rating
import com.magomed.gamzatov.medlite.network.ProfileRequest
import com.magomed.gamzatov.medlite.network.RatingRequest
import com.magomed.gamzatov.medlite.network.ServiceGenerator
import kotlinx.android.synthetic.main.fragment_profile.*
import org.jetbrains.anko.enabled
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RateDialog : DialogFragment() {

    companion object {
        fun newInstance(visitId: Long, name: String?, phone: String?, rate: Int, charge: Int): RateDialog {
            val fragment = RateDialog()
            val args = Bundle()
            args.putLong("visitId", visitId)
            args.putString("name", name)
            args.putString("phone", phone)
            args.putInt("rate", rate)
            args.putInt("charge", charge)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater = activity.layoutInflater
        val view = inflater.inflate(R.layout.rate_dialog, null)
        val name = view.findViewById(R.id.name) as TextView
        val rate = view.findViewById(R.id.rating) as TextView
        val phone = view.findViewById(R.id.phone) as TextView
        val charge = view.findViewById(R.id.charge) as TextView
        val rateButton = view.findViewById(R.id.btn_rate) as AppCompatButton
        val ratingBar = view.findViewById(R.id.ratingBar) as RatingBar

        name.text = arguments.getString("name")
        phone.text = "Phone: ".plus(arguments.getString("phone"))
        rate.text = "Rating: ".plus(arguments.getInt("rate").toString())
        charge.text = "Charge: ".plus(arguments.getInt("charge").toString()).plus(" ₽")

        rateButton.setOnClickListener {
            rateButton.enabled = false
            val ratingRequest = ServiceGenerator.createService(RatingRequest::class.java)
            val pref = PreferenceManager.getDefaultSharedPreferences(activity.baseContext)
            val call = ratingRequest.postJSON(pref.getString("cookie", ""), pref.getString("isMedic", ""),
                    Rating(ratingBar.rating.toInt(), arguments.getLong("visitId")))
            call.enqueue(object: Callback<Message> {
                override fun onResponse(call: Call<Message>?, response: Response<Message>?) {
                    this@RateDialog.dismiss()
                }

                override fun onFailure(call: Call<Message>?, t: Throwable?) {
                    Log.d("onFailure", t.toString())
                    rateButton.enabled = true
                }

            })


        }

        val builder = AlertDialog.Builder(activity)
        builder.setView(view)

        return builder.create()
    }
}