package com.guet.flexbox.playground.test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.facebook.litho.ComponentContext
import com.guet.flexbox.playground.R
import com.guet.flexbox.playground.widget.TransformRootLayout

class TestActivity : AppCompatActivity() {

    var m: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val c = ComponentContext(this)
        setContentView(R.layout.activity_test)
        findViewById<TransformRootLayout>(R.id.host).apply {
            setOnClickListener {
                m = if (!m) {
                    move()
                    true
                } else {
                    reset()
                    false
                }
            }
        }
    }
}
