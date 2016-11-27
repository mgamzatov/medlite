package com.magomed.gamzatov.medlite.adapter

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageLoader
import com.magomed.gamzatov.medlite.R
import com.magomed.gamzatov.medlite.model.GetVisit
import com.magomed.gamzatov.medlite.network.ServiceGenerator
import com.magomed.gamzatov.medlite.network.VolleySingleton
import org.jetbrains.anko.backgroundResource


class RVHistoryAdapter : RecyclerView.Adapter<RVHistoryAdapter.PersonViewHolder> {

    private val imageLoader: ImageLoader?
    val visits: List<GetVisit>
    val isMedic: String

    constructor(nurses: List<GetVisit>, isMedic: String) {
        this.visits = nurses
        this.isMedic = isMedic
        val volleySingleton = VolleySingleton.getsInstance()
        imageLoader = volleySingleton?.getImageLoader()
    }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PersonViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.item_list, parent, false)
        return PersonViewHolder(v)
    }

    override fun getItemCount(): Int {
        return visits.size
    }

    override fun onBindViewHolder(holder: PersonViewHolder?, position: Int) {
        val url: String

        if(isMedic != "true") {
            holder?.personName?.text = visits[position].medicName
            holder?.personAge?.text = visits[position].medicPhone
            url = ServiceGenerator.API_BASE_URL + "/images/-${visits[position].medicId}.jpg"
        } else {
            holder?.personName?.text = visits[position].patientName
            holder?.personAge?.text = visits[position].patientPhone
            url = ServiceGenerator.API_BASE_URL + "/images/${visits[position].patientId}.jpg"
        }
        holder?.personDate?.text = visits[position].date

        when (visits[position].confirmed) {
            -1 -> holder?.personConfirmed?.backgroundResource = R.mipmap.declined
            0 -> holder?.personConfirmed?.backgroundResource = R.mipmap.pending1
            1 -> holder?.personConfirmed?.backgroundResource = R.mipmap.accepted
        }

        imageLoader?.get(url, object : ImageLoader.ImageListener {
            override fun onResponse(response: ImageLoader.ImageContainer, isImmediate: Boolean) {
                holder?.personPhoto?.setImageBitmap(response.bitmap)
            }

            override fun onErrorResponse(error: VolleyError) {

            }
        })
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
    }

    class PersonViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var cv: CardView
        internal var personName: TextView
        internal var personAge: TextView
        internal var personPhoto: ImageView
        internal var personDate: TextView
        internal var personConfirmed: ImageView

        init {
            cv = itemView.findViewById(R.id.cv) as CardView
            personName = itemView.findViewById(R.id.person_name) as TextView
            personAge = itemView.findViewById(R.id.person_age) as TextView
            personPhoto = itemView.findViewById(R.id.person_photo) as ImageView
            personDate = itemView.findViewById(R.id.person_date) as TextView
            personConfirmed = itemView.findViewById(R.id.person_confirmed) as ImageView
        }
    }

}