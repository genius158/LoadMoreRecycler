package com.yan.loadmorerecyclertest

import android.app.Application

import com.squareup.leakcanary.LeakCanary

/**
 * Created by yan on 2017/6/27.
 */

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
        // Normal app init code...
    }
}
