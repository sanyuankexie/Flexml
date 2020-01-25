package com.guet.flexbox.playground.test

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.facebook.litho.ComponentContext
import com.facebook.litho.LithoView
import com.facebook.litho.Row
import com.guet.flexbox.litho.widget.Banner
import com.guet.flexbox.playground.R

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val c = ComponentContext(this)
        setContentView(R.layout.activity_test)
        val lithoView: LithoView = findViewById(R.id.litho)
        lithoView.setComponentAsync(Banner.create(c)
                .isCircular(true)
                .child(Row.create(c)
                        .backgroundColor(Color.RED)
                        .widthDip(300f)
                        .widthDip(300f)
                        .build())
                .child(Row.create(c)
                        .backgroundColor(Color.GREEN)
                        .widthDip(300f)
                        .widthDip(300f)
                        .build())
                .child(Row.create(c)
                        .backgroundColor(Color.BLUE)
                        .widthDip(300f)
                        .widthDip(300f)
                        .build())
                .build())
    }
}
