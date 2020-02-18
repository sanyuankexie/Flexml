package com.guet.flexbox.playground.test

import android.os.Build
import android.os.Bundle
import android.os.MemoryFile
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.didichuxing.doraemonkit.DoraemonKit
import com.facebook.litho.ComponentContext
import com.facebook.litho.LithoView
import com.facebook.litho.Row

class TestActivity : AppCompatActivity() {

    val memoryFiles = ArrayList<MemoryFile>()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DoraemonKit.install(application)
        val c = ComponentContext(this)
        setContentView(LithoView.create(c, Row.create(c)

                .build()))
    }
}


