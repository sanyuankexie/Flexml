package com.guet.flexbox.playground

import android.app.Application
import com.didichuxing.doraemonkit.DoraemonKit
import com.facebook.soloader.SoLoader

class PlaygroundApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DoraemonKit.install(this)
        SoLoader.init(this, false)
    }
}