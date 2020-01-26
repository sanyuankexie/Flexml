package com.guet.flexbox.playground

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val fragments = arrayOf(
            MainFragment(),
            IntroductionFragment(),
            AboutFragment()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().apply {
            fragments.forEach {
                add(R.id.container, it)
                hide(it)
            }
        }.show(fragments[0]).commit()
    }
}