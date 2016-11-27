package com.magomed.gamzatov.medlite.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.telephony.gsm.GsmCellLocation
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.github.salomonbrys.kotson.typeToken
import com.google.gson.Gson
import com.magomed.gamzatov.medlite.R
import com.magomed.gamzatov.medlite.adapter.ItemClickSupport
import com.magomed.gamzatov.medlite.adapter.RVListAdapter
import com.magomed.gamzatov.medlite.model.Message
import com.magomed.gamzatov.medlite.model.Nurse
import com.magomed.gamzatov.medlite.model.AddVisit
import com.magomed.gamzatov.medlite.network.AddVisitRequest
import com.magomed.gamzatov.medlite.network.ServiceGenerator
import com.magomed.gamzatov.medlite.network.VolleySingleton
import org.jetbrains.anko.support.v4.longToast
import retrofit2.Call
import retrofit2.Callback
import java.util.*


class ListFragment : Fragment() {

    companion object {
        fun newInstance(): Fragment {
            val frag = ListFragment()
            val args = Bundle()
            frag.arguments = args
            return frag
        }
    }

    private val gson = Gson()

    private val nurses = ArrayList<Nurse>()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = inflater?.inflate(R.layout.fragment_list, container, false) as View

        val url = ServiceGenerator.API_BASE_URL + ServiceGenerator.API_PREFIX_URL + "getMedics"

        val rv = view.findViewById(R.id.rv) as RecyclerView
        val llm = LinearLayoutManager(context)
        rv.layoutManager = llm

        val requestQueue = VolleySingleton.getsInstance()?.getRequestQueue()
        val stringRequest = StringRequest(Request.Method.GET, url, Response.Listener<kotlin.String> { response ->
            parseResponse(response)
            val adapter = RVListAdapter(nurses)
            rv.adapter = adapter
        }, Response.ErrorListener { error -> longToast("Error " + error) })
        requestQueue?.add(stringRequest)

        ItemClickSupport.addTo(rv).setOnItemClickListener(object: ItemClickSupport.OnItemClickListener {
            override fun onItemClicked(recyclerView: RecyclerView, position: Int, v: View) {
                val nurse = nurses[position]
                val builder = AlertDialog.Builder(activity)
                builder.setMessage("Send request to ${nurse.name} (${nurse.phone}) ?")
                builder.setCancelable(true)
                builder.setPositiveButton("Yes", { dialog, which ->
                    addVisit(nurse.id.toString())
                    dialog.dismiss()
                })
                builder.setNegativeButton("No", { dialog, which ->
                    dialog.dismiss()
                })
                val dialog = builder.create()
                dialog.show()
            }

        })

        return view
    }

    private fun parseResponse(response: String?) {
        val nurses = gson.fromJson<List<Nurse>>(response, typeToken<List<Nurse>>())

        nurses.forEach {
            it.photoUrl = ServiceGenerator.API_BASE_URL + "/images/-${it.id}.jpg"
        }

        this.nurses.addAll(nurses)
    }

    private fun addVisit(snippet: String) {
        val progressDialog = ProgressDialog(activity)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage("Sending...")
        progressDialog.show()

        val visitRequest = ServiceGenerator.createService(AddVisitRequest::class.java)
        val pref = PreferenceManager.getDefaultSharedPreferences(activity.baseContext)
        val call = visitRequest.postJSON(pref.getString("cookie", ""), AddVisit(snippet.toLong()))
        call.enqueue(object: Callback<Message> {
            override fun onResponse(call: Call<Message>?, response: retrofit2.Response<Message>?) {
                Log.d("onResponse", response?.body().toString())
                longToast("success")
                progressDialog.dismiss()
            }

            override fun onFailure(call: Call<Message>?, t: Throwable?) {
                longToast(t.toString())
                Log.d("onFailure", t.toString())
                progressDialog.dismiss()
            }

        })
    }

}