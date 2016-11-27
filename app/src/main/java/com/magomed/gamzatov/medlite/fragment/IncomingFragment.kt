package com.magomed.gamzatov.medlite.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.magomed.gamzatov.medlite.R
import com.magomed.gamzatov.medlite.adapter.EmptyRecyclerViewAdapter
import com.magomed.gamzatov.medlite.adapter.ItemClickSupport
import com.magomed.gamzatov.medlite.adapter.RVHistoryAdapter
import com.magomed.gamzatov.medlite.model.GetVisit
import com.magomed.gamzatov.medlite.model.Message
import com.magomed.gamzatov.medlite.network.ConfirmVisitRequest
import com.magomed.gamzatov.medlite.network.GetVisitRequest
import com.magomed.gamzatov.medlite.network.ServiceGenerator
import org.jetbrains.anko.support.v4.longToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class IncomingFragment : Fragment() {

    companion object {
        fun newInstance(): Fragment {
            val frag = IncomingFragment()
            val args = Bundle()
            frag.arguments = args
            return frag
        }
    }

    var listVisit: ArrayList<GetVisit> = ArrayList()
    var rv: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = inflater?.inflate(R.layout.fragment_incoming, container, false) as View
        rv = view.findViewById(R.id.rv) as RecyclerView
        val llm = LinearLayoutManager(context)
        rv?.layoutManager = llm

        val getVisitRequest = ServiceGenerator.createService(GetVisitRequest::class.java)
        val pref = PreferenceManager.getDefaultSharedPreferences(activity.baseContext)
        val call = getVisitRequest.get(pref.getString("cookie", ""), pref.getString("isMedic", ""), "new")
        call.enqueue(object: Callback<ArrayList<GetVisit>> {
            override fun onResponse(call: Call<ArrayList<GetVisit>>?, response: Response<ArrayList<GetVisit>>?) {
                if (response?.body() != null && !response?.body()?.isEmpty()!!) {
                    listVisit = response?.body()!!
                    val adapter = RVHistoryAdapter(listVisit, pref.getString("isMedic", ""))
                    rv?.adapter = adapter
                } else {
                    val adapter = EmptyRecyclerViewAdapter()
                    rv?.adapter = adapter
                }
            }

            override fun onFailure(call: Call<ArrayList<GetVisit>>?, t: Throwable?) {
                Log.d("onFailure", t.toString())
            }

        })

        ItemClickSupport.addTo(rv as RecyclerView).setOnItemClickListener(object: ItemClickSupport.OnItemClickListener {
            override fun onItemClicked(recyclerView: RecyclerView, position: Int, v: View) {
                val visit = listVisit[position]
                val builder = AlertDialog.Builder(activity)
                builder.setMessage("Do you accept ${visit.patientName}'s request?")
                builder.setCancelable(true)
                builder.setPositiveButton("Yes", { dialog, which ->
                    setConfirmed(visit.id as Long, 1, visit, position)
                    dialog.dismiss()
                })
                builder.setNegativeButton("No", { dialog, which ->
                    setConfirmed(visit.id as Long, -1, visit, position)
                    dialog.dismiss()
                })
                val dialog = builder.create()
                dialog.show()
            }

        })

        return view
    }

    private fun setConfirmed(id: Long, confirmed: Int, visit: GetVisit, position: Int) {
        val progressDialog = ProgressDialog(activity)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage("Sending...")
        progressDialog.show()

        val confirmVisitRequest = ServiceGenerator.createService(ConfirmVisitRequest::class.java)
        val pref = PreferenceManager.getDefaultSharedPreferences(activity.baseContext)
        val call = confirmVisitRequest.postJSON(pref.getString("cookie", ""), pref.getString("isMedic", ""),
                GetVisit(id, null, null, null, null, null, null, confirmed, null, null))
        call.enqueue(object: Callback<Message> {
            override fun onResponse(call: Call<Message>?, response: retrofit2.Response<Message>?) {
                Log.d("onResponse", response?.body().toString())
                longToast("success")
                listVisit.remove(visit)
                rv?.adapter?.notifyItemRemoved(position)
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