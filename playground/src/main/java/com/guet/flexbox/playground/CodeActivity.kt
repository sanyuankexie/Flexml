package com.guet.flexbox.playground

import android.graphics.Outline
import android.os.Bundle
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import com.didichuxing.doraemonkit.util.UIUtils
import com.google.android.material.appbar.AppBarLayout
import com.guet.flexbox.AppExecutors
import com.guet.flexbox.litho.HostingView
import com.guet.flexbox.playground.model.AppLoader
import thereisnospon.codeview.CodeView
import thereisnospon.codeview.CodeViewTheme
import kotlin.math.abs

class CodeActivity : AppCompatActivity() {

    private lateinit var scroll: NestedScrollView
    private lateinit var codeView: CodeView
    private lateinit var lithoView: HostingView
    private lateinit var appbar: AppBarLayout
    private lateinit var host: CoordinatorLayout
    private lateinit var title: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code)
        appbar = findViewById(R.id.appbar)
        scroll = findViewById(R.id.scroll)
        host = findViewById(R.id.host)
        title = findViewById(R.id.title)
        lithoView = findViewById(R.id.dynamic)
        val back = findViewById<View>(R.id.go_back)
        back.setOnClickListener {
            finishAfterTransition()
        }
        val lithoHost = findViewById<View>(R.id.lithoHost)
        appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _: AppBarLayout, verticalOffset: Int ->
            lithoHost.alpha = 1f - (abs(verticalOffset.toFloat()) / lithoHost.height.toFloat())
        })
        title.text = this.intent.getStringExtra("url")
        codeView = findViewById(R.id.code)
        codeView.setTheme(CodeViewTheme.ANDROIDSTUDIO).fillColor()
        val outline = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height,
                        UIUtils.dp2px(this@CodeActivity, 15f).toFloat())
            }
        }
        scroll.outlineProvider = outline
        scroll.clipToOutline = true
        loadData()
    }

    private fun loadData() {
        val url = this.intent.getStringExtra("url")
        AppExecutors.threadPool.execute {
            val page = AppLoader.loadPage(application, url)
            val code = AppLoader.findSourceCode(url)
            runOnUiThread {
                lithoView.unmountAllItems()
                lithoView.setContentAsync(page)
                codeView.showCode(code)
            }
        }
    }
}
