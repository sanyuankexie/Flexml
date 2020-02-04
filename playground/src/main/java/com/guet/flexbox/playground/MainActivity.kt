package com.guet.flexbox.playground

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.didichuxing.doraemonkit.util.UIUtils
import com.guet.flexbox.playground.widget.TransformRootLayout

class MainActivity : AppCompatActivity() {

    private val fragments by lazy {
        arrayOf(
                HomepageFragment(),
                IntroductionFragment(),
                AboutFragment()
        )
    }

    private val root by lazy {
        TransformRootLayout(this)
    }

    private lateinit var superHost: CardView

    fun move() {
        root.move()
        superHost.radius = UIUtils.dp2px(this, 15f).toFloat()
    }

    fun reset() {
        root.reset()
        superHost.radius = 0f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decor = window.decorView as ViewGroup
        val list = (0 until decor.childCount).map {
            decor.getChildAt(it)
        }
        decor.removeAllViews()
        list.forEach {
            root.addView(it)
        }
        decor.addView(root)
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
        superHost = findViewById(R.id.super_host)
    }
}