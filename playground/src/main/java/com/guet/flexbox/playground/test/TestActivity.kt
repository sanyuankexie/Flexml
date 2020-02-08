package com.guet.flexbox.playground.test

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.guet.flexbox.litho.transforms.ScaleTypes
import com.guet.flexbox.playground.R

class TestActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        imageView = findViewById(R.id.image)


        Handler().postDelayed({
            Glide.with(this)
                    .load("https://tva1.sinaimg.cn/large/87c01ec7gy1frqbkngr3dj21hc0u0u0x.jpg")
                    .transform(
                            ScaleTypes(
                                    ScaleType.FIT_XY
                            ),
//                            FastBlur(
//                                    10f,
//                                    3f
//                            ),
                            GranularRoundedCorners(
                                    100f,
                                    100f,
                                    100f,
                                    100f
                            )
                    )
                    .into(object :SimpleTarget<Drawable>(
                            imageView.width,
                            imageView.height
                    ){
                        override fun onResourceReady(
                                resource: Drawable,
                                transition: Transition<in Drawable>?) {
                            imageView.scaleType = ScaleType.FIT_XY
                            imageView.setImageDrawable(resource)
                        }
                    })
        },2000)
    }

}
