package com.yan.loadmorerecyclertest

import android.app.Application
import android.content.Context

import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher


/**
 * Created by yan on 2017/6/27.
 */

class App : Application() {
    //在自己的Application中添加如下代码

    override fun onCreate() {
        super.onCreate()
        refWatcher = LeakCanary.install(this)
    }
    private var refWatcher: RefWatcher? = null

    fun getRefWatcher(baseContext: Context?): RefWatcher? {
        return refWatcher;
    }


}
