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
import com.magomed.gamzatov.medlite.model.Nurse
import com.magomed.gamzatov.medlite.network.VolleySingleton


class RVListAdapter : RecyclerView.Adapter<RVListAdapter.PersonViewHolder> {

    private val imageLoader: ImageLoader?
    val nurses: List<Nurse>

    constructor(nurses: List<Nurse>) {
        this.nurses = nurses
        val volleySingleton = VolleySingleton.getsInstance()
        imageLoader = volleySingleton?.getImageLoader()
    }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PersonViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.item_list, parent, false)
        return PersonViewHolder(v)
    }

    override fun getItemCount(): Int {
        return nurses.size
    }

    override fun onBindViewHolder(holder: PersonViewHolder?, position: Int) {
        holder?.personName?.text = nurses[position].name
        holder?.personAge?.text = nurses[position].phone
        holder?.personCharge?.text = nurses[position].callCharge?.toString()?.plus(" ₽")
        val url = nurses[position].photoUrl

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
        internal var personCharge: TextView
        internal var personPhoto: ImageView

        init {
            cv = itemView.findViewById(R.id.cv) as CardView
            personName = itemView.findViewById(R.id.person_name) as TextView
            personAge = itemView.findViewById(R.id.person_age) as TextView
            personCharge = itemView.findViewById(R.id.person_charge) as TextView
            personPhoto = itemView.findViewById(R.id.person_photo) as ImageView
        }
    }

}