package com.guet.flexbox.playground.test

import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.guet.flexbox.litho.transforms.ImageScale
import com.guet.flexbox.playground.R
import com.guet.flexbox.playground.widget.CornerOutlineProvider

class TestActivity : AppCompatActivity() {

    private lateinit var nativeImage: ImageView
    private lateinit var imageView: ImageView

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        nativeImage = findViewById(R.id.native_image)
        imageView = findViewById(R.id.image)
        imageView.scaleType = ScaleType.FIT_XY
        imageView.alpha = 0.5f
        nativeImage.outlineProvider = CornerOutlineProvider(400)
        nativeImage.clipToOutline = true
        Glide.with(this)
                .load(R.drawable.ic_photo2)
                .transform(
                        ImageScale(
                                ScaleType.FIT_XY
                        ),
//                            FastBlur(
//                                    10f,
//                                    3f
//                            ),
                        RoundedCorners(400)
                )
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView)
    }

}
