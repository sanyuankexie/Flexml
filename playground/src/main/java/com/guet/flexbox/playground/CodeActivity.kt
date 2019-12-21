package com.guet.flexbox.playground

import android.graphics.Outline
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import com.didichuxing.doraemonkit.util.UIUtils
import com.google.gson.Gson
import com.guet.flexbox.PageHostView
import com.guet.flexbox.compiler.Compiler
import com.guet.flexbox.content.DynamicNode
import com.guet.flexbox.databinding.DataBindingUtils
import thereisnospon.codeview.CodeView
import thereisnospon.codeview.CodeViewTheme
import java.util.*

class CodeActivity : AppCompatActivity() {

    private lateinit var scroll: NestedScrollView
    private lateinit var codeView: CodeView
    private lateinit var lithoView: PageHostView
    private lateinit var host: CoordinatorLayout
    private lateinit var title: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code)
        scroll = findViewById(R.id.scroll)
        host = findViewById(R.id.host)
        title = findViewById(R.id.title)
        title.text = this.intent.getStringExtra("url")
        codeView = findViewById(R.id.code)
        codeView.setTheme(CodeViewTheme.ANDROIDSTUDIO).fillColor()
        lithoView = findViewById(R.id.dynamic)
        scroll.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height,
                        UIUtils.dp2px(this@CodeActivity, 15f).toFloat())
            }
        }
        scroll.clipToOutline = true
        loadData()
    }

    private fun loadData() {
        val url = this.intent.getStringExtra("url")
        AsyncTask.THREAD_POOL_EXECUTOR.execute {
            val gson = Gson()
            val input = resources.assets.open(url)
            val code = input.reader().readText()
            input.close()
            val data = if (url == resources.getString(R.string.function_path)) {
                mapOf(
                        "icons" to resources.getStringArray(R.array.function_icons),
                        "url" to url
                )
            } else {
                Collections.singletonMap("url", url)
            }
            val s = Compiler.compile(code)
            val contentRaw = gson.fromJson(s, DynamicNode::class.java)
            val content = DataBindingUtils.bind(this, contentRaw, data)
            runOnUiThread {
                lithoView.setContentAsync(content)
                codeView.showCode(code)
            }
        }
    }
}
