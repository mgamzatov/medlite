package com.magomed.gamzatov.medlite

import android.app.Application
import android.content.Context

class MyApplication : Application() {

    companion object {
        private var sInstance: MyApplication? = null

        fun getsInstance(): MyApplication? {
            return sInstance
        }

        fun getAppContext(): Context? {
            return sInstance?.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        sInstance = this
    }
}