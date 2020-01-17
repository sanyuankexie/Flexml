package com.guet.flexbox.playground.test

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.facebook.litho.ComponentContext
import com.facebook.litho.LithoView
import com.facebook.litho.Row
import com.guet.flexbox.litho.widget.Banner

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val c = ComponentContext(this)
        setContentView(LithoView.create(c,Banner.create(c)
                .isCircular(true)
                .child(Row.create(c)
                        .backgroundColor(Color.RED)
                        .widthDip(300f)
                        .widthDip(300f)
                        .build())
                .child(Row.create(c)
                        .backgroundColor(Color.GRAY)
                        .widthDip(300f)
                        .widthDip(300f)
                        .build())
                .child(Row.create(c)
                        .backgroundColor(Color.WHITE)
                        .widthDip(300f)
                        .widthDip(300f)
                        .build())
                .build()).apply {
            layoutParams = ViewGroup.LayoutParams(-1,-1)
        })

    }
}
