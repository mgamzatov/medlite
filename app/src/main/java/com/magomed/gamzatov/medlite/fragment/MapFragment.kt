package com.magomed.gamzatov.medlite.fragment

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.github.salomonbrys.kotson.typeToken
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.magomed.gamzatov.medlite.R
import com.magomed.gamzatov.medlite.adapter.RVListAdapter
import com.magomed.gamzatov.medlite.model.Login
import com.magomed.gamzatov.medlite.model.Message
import com.magomed.gamzatov.medlite.model.Nurse
import com.magomed.gamzatov.medlite.model.AddVisit
import com.magomed.gamzatov.medlite.network.AddVisitRequest
import com.magomed.gamzatov.medlite.network.LoginRequest
import com.magomed.gamzatov.medlite.network.ServiceGenerator
import com.magomed.gamzatov.medlite.network.VolleySingleton
import org.jetbrains.anko.support.v4.longToast
import retrofit2.Call
import retrofit2.Callback


class MapFragment : Fragment() {

    companion object {
        fun newInstance(): Fragment {
            val frag = MapFragment()
            val args = Bundle()
            frag.arguments = args
            return frag
        }
    }

    var mMapView: MapView? = null
    private var googleMap: GoogleMap? = null
    val MY_LOCATION_REQUEST_CODE = 111
    private val gson = Gson()



    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater?.inflate(R.layout.fragment_map, container, false)

        mMapView = rootView?.findViewById(R.id.mapView) as MapView
        mMapView?.onCreate(savedInstanceState)
        mMapView?.onResume() // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(activity.applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mMapView?.getMapAsync { mMap ->
            googleMap = mMap

            // For showing a move to my location button
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                googleMap?.isMyLocationEnabled = true
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    longToast("need location permission")
                } else {
                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_LOCATION_REQUEST_CODE)
                }
            }

            // For dropping a marker at a point on the Map
            val piter = LatLng(59.937, 30.347)

            val url = ServiceGenerator.API_BASE_URL + ServiceGenerator.API_PREFIX_URL + "getMedics"
            val requestQueue = VolleySingleton.getsInstance()?.getRequestQueue()
            val stringRequest = StringRequest(Request.Method.GET, url, Response.Listener<String> { response ->
                setMarkers(response)
                // For zooming automatically to the location of the marker
                val cameraPosition = CameraPosition.Builder().target(piter).zoom(11.5F).build()
                googleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

            }, Response.ErrorListener { error -> longToast("Error " + error) })
            requestQueue?.add(stringRequest)

            googleMap?.setOnMarkerClickListener {

                val ft = fragmentManager.beginTransaction()
                val prev = fragmentManager.findFragmentByTag("dialog")
                if (prev != null) {
                    ft.remove(prev)
                }
                ft.addToBackStack(null)
                val newFragment = ProfileDialog.newInstance(it.snippet.toLong())
                newFragment.show(ft, "dialog")

                true
            }
        }

        return rootView as View
    }

    private fun setMarkers(response: String?) {
        val nurses = gson.fromJson<List<Nurse>>(response, typeToken<List<Nurse>>())
        nurses.forEach {
            googleMap?.addMarker(MarkerOptions().position(LatLng(it.latitude?.toDouble() as Double, it.longitude?.toDouble() as Double)).title(it.name+ " (${it.phone})" ).snippet(it.id.toString()))
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.size == 1 && permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                googleMap?.isMyLocationEnabled = true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mMapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView?.onLowMemory()
    }
}