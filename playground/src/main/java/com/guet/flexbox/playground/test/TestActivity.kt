package com.guet.flexbox.playground.test

import android.os.Build
import android.os.Bundle
import android.os.MemoryFile
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.didichuxing.doraemonkit.DoraemonKit
import com.guet.flexbox.playground.R

class TestActivity : AppCompatActivity() {

    val memoryFiles = ArrayList<MemoryFile>()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DoraemonKit.install(application)

        setContentView(R.layout.activity_test)
        val imageView = findViewById<ImageView>(R.id.native_image)
    }
}


