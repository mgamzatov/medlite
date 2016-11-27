package com.magomed.gamzatov.medlite.network

import android.graphics.Bitmap
import android.support.v4.util.LruCache
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley
import com.magomed.gamzatov.medlite.MyApplication


class VolleySingleton {

    companion object {
        private var sInstance: VolleySingleton? = null
        fun getsInstance(): VolleySingleton? {
            if (sInstance == null) {
                sInstance = VolleySingleton()
            }
            return sInstance
        }
    }

    private var imageLoader: ImageLoader

    private var mRequestQueue: RequestQueue

    private constructor() {
        mRequestQueue = Volley.newRequestQueue(MyApplication.getAppContext())
        imageLoader = ImageLoader(mRequestQueue, object : ImageLoader.ImageCache {
            private val cache = LruCache<String, Bitmap>((Runtime.getRuntime().maxMemory() / 1024).toInt() / 8)
            override fun getBitmap(url: String): Bitmap? {
                return cache.get(url)
            }

            override fun putBitmap(url: String, bitmap: Bitmap) {
                cache.put(url, bitmap)
            }
        })

    }

    fun getRequestQueue(): RequestQueue {
        return mRequestQueue
    }

    fun getImageLoader(): ImageLoader {
        return imageLoader
    }
}