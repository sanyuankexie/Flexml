package com.guet.flexbox.playground

import android.os.Bundle
import android.view.View
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
        arrayOf<View>(
                findViewById(R.id.main),
                findViewById(R.id.introduction),
                findViewById(R.id.about)
        ).mapIndexed { index, view ->
            view.setOnClickListener {
                supportFragmentManager.beginTransaction()
                        .apply {
                            fragments.forEachIndexed { index1, fragment ->
                                if (index != index1) {
                                    hide(fragment)
                                }
                            }
                        }.show(fragments[index])
                        .commit()
            }
        }
    }
}