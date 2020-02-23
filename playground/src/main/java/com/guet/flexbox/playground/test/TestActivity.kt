package com.guet.flexbox.playground.test

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.MemoryFile
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.didichuxing.doraemonkit.DoraemonKit
import com.guet.flexbox.playground.R

class TestActivity : AppCompatActivity() {

    val memoryFiles = ArrayList<MemoryFile>()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DoraemonKit.install(application)
        val path = Glide.get(this)
                .registry.getLoadPath(
                Int::class.java,
                Drawable::class.java,
                Drawable::class.java
        )


        val drawable = Glide.with(this)
                .load(R.drawable.ic_background_transition)
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        return true
                    }

                    override fun onResourceReady(resource: Drawable, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        Log.d("asdasdasd", resource.toString())
                        return true
                    }
                })
                .submit()

    }
}


