package com.guet.flexbox.playground

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.didichuxing.doraemonkit.util.UIUtils
import com.facebook.litho.LithoView
import com.facebook.litho.Row
import com.facebook.litho.widget.HorizontalScroll
import com.facebook.litho.widget.Text
import com.facebook.litho.widget.VerticalScroll
import com.facebook.yoga.YogaEdge
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.guet.flexbox.playground.model.AppLoader
import com.guet.flexbox.playground.widget.CornerOutlineProvider
import com.guet.flexbox.playground.widget.TransformRootLayout

class MainActivity : AppCompatActivity() {

    private val fragments by lazy {
        arrayOf(
                HomepageFragment(),
                IntroductionFragment(),
                AboutFragment()
        )
    }

    private lateinit var bottomHost: View

    private lateinit var codeView: LithoView

    private lateinit var root: TransformRootLayout

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    private fun move() {
        root.move()
    }

    private fun reset() {
        root.reset()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        root = findViewById(R.id.transform_root)
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
        initCodePanel()
    }

    private fun initCodePanel() {
        codeView = findViewById(R.id.code)
        codeView.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                outlineProvider = CornerOutlineProvider(
                        UIUtils.dp2px(this@MainActivity, 15f)
                )
                clipToOutline = true
            }
        }
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById<View>(R.id.design_bottom_sheet))
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    reset()
                    codeView.release()
                    bottomHost.visibility = View.GONE
                }
            }
        })
        bottomHost = findViewById(R.id.bottom_sheet_host)
    }

    fun showCodePanel(url: String) {
        move()
        bottomHost.visibility = View.VISIBLE
        val code = AppLoader.findSourceCode(url)
        val c = codeView.componentContext
        codeView.setComponentAsync(Row.create(c)
                .backgroundColor(resources.getColor(R.color.code_background))
                .child(VerticalScroll.create(c)
                        .nestedScrollingEnabled(true)
                        .paddingDip(YogaEdge.VERTICAL, 10f)
                        .childComponent(HorizontalScroll.create(c)
                                .paddingDip(YogaEdge.HORIZONTAL, 10f)
                                .contentProps(Text.create(c)
                                        .textColor(resources.getColor(R.color.whitesmoke))
                                        .text(code))
                        )
                ).build())
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }
}