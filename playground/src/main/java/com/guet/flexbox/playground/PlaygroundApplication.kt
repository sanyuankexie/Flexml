package com.guet.flexbox.playground

import androidx.multidex.MultiDexApplication
import com.didichuxing.doraemonkit.DoraemonKit
import com.facebook.soloader.SoLoader
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger

class PlaygroundApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        DoraemonKit.install(this)
        Logger.addLogAdapter(AndroidLogAdapter())
        SoLoader.init(this, false)
    }
}