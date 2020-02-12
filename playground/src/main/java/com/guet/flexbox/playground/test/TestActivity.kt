package com.guet.flexbox.playground.test

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.guet.flexbox.litho.drawable.EnhancedBitmapDrawable
import com.guet.flexbox.playground.R

class TestActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        val imageView = findViewById<ImageView>(R.id.native_image)
        val imageView2 = findViewById<ImageView>(R.id.image)
        imageView.scaleType = ImageView.ScaleType.FIT_XY
        Glide.with(this)
                .asBitmap()
                .load(R.drawable.ic_photo2)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                    ) {
                        val type = ImageView.ScaleType.CENTER_CROP
                        imageView.scaleType = type
                        imageView.setImageBitmap(resource)
                        val drawable = EnhancedBitmapDrawable(resource)
                        imageView2.setImageDrawable(drawable)
                        drawable.scaleType = type
                    }
                })

    }
}


