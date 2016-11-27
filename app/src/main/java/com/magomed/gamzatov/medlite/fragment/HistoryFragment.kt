package com.magomed.gamzatov.medlite.fragment

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
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
import com.magomed.gamzatov.medlite.network.GetVisitRequest
import com.magomed.gamzatov.medlite.network.ServiceGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class HistoryFragment : Fragment() {

    companion object {
        fun newInstance(): Fragment {
            val frag = HistoryFragment()
            val args = Bundle()
            frag.arguments = args
            return frag
        }
    }

    var visits: List<GetVisit> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = inflater?.inflate(R.layout.fragment_history, container, false) as View
        val rv = view.findViewById(R.id.rv) as RecyclerView
        val llm = LinearLayoutManager(context)
        rv.layoutManager = llm


        val getVisitRequest = ServiceGenerator.createService(GetVisitRequest::class.java)
        val pref = PreferenceManager.getDefaultSharedPreferences(activity.baseContext)
        var mode: String = ""
        if (pref.getString("isMedic", "") == "true") {
            mode = "notNew"
        } else {
            ItemClickSupport.addTo(rv).setOnItemClickListener(object: ItemClickSupport.OnItemClickListener {
                override fun onItemClicked(recyclerView: RecyclerView, position: Int, v: View) {
                    val ft = fragmentManager.beginTransaction()
                    val prev = fragmentManager.findFragmentByTag("dialog")
                    if (prev != null) {
                        ft.remove(prev)
                    }
                    ft.addToBackStack(null)

                    val visit = visits[position]

                    // Create and show the dialog.
                    if(visit.confirmed != 0) {
                        val newFragment = RateDialog.newInstance(visit.id!!, visit.medicName, visit.medicPhone, visit.rating!!, 500)
                        newFragment.show(ft, "dialog")
                    }
                }

            })
        }
        val call = getVisitRequest.get(pref.getString("cookie", ""), pref.getString("isMedic", ""), mode)
        call.enqueue(object: Callback<ArrayList<GetVisit>> {
            override fun onResponse(call: Call<ArrayList<GetVisit>>?, response: Response<ArrayList<GetVisit>>?) {
                if (response?.body() != null && !response?.body()?.isEmpty()!!) {
                    visits = response?.body()!!
                    val adapter = RVHistoryAdapter(visits, pref.getString("isMedic", ""))
                    rv.adapter = adapter
                } else {
                    val adapter = EmptyRecyclerViewAdapter()
                    rv.adapter = adapter
                }
            }

            override fun onFailure(call: Call<ArrayList<GetVisit>>?, t: Throwable?) {
                Log.d("onFailure", t.toString())
            }

        })


        return view
    }

}