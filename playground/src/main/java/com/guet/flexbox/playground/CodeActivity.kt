package com.guet.flexbox.playground

import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.facebook.litho.LithoView
import com.facebook.litho.Row
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaJustify
import com.google.gson.Gson
import com.guet.flexbox.DynamicBox
import com.guet.flexbox.compiler.Compiler
import com.guet.flexbox.data.LayoutNode
import com.guet.flexbox.databinding.DataBindingUtils
import io.github.kbiakov.codeview.CodeView
import io.github.kbiakov.codeview.adapters.Options
import io.github.kbiakov.codeview.highlight.ColorTheme
import java.util.*

class CodeActivity : AppCompatActivity() {

    private lateinit var codeView: CodeView
    private lateinit var lithoView: LithoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code)
        codeView = findViewById(R.id.code)
        codeView.apply {
            setOptions(Options.Default.get(this@CodeActivity)
                    .withLanguage("xml")
                    .withTheme(ColorTheme.DEFAULT))
        }
        lithoView = findViewById(R.id.dynamic)
        loadData()
    }

    private fun loadData() {
        val url = this.intent.getStringExtra("url")
        AsyncTask.THREAD_POOL_EXECUTOR.execute {
            val gson = Gson()
            val input = resources.assets.open(url)
            val code = input.reader().readText()
            val data = Collections.singletonMap("url", url)
            val s = Compiler.compile(input)
            val contentRaw = gson.fromJson(s, LayoutNode::class.java)
            val content = DataBindingUtils.bind(this, contentRaw, data)
            runOnUiThread {
                val c = lithoView.componentContext
                lithoView.setComponentAsync(Row.create(c)
                        .alignItems(YogaAlign.CENTER)
                        .justifyContent(YogaJustify.CENTER)
                        .flexGrow(1f)
                        .child(DynamicBox.create(c)
                                .content(content)
                        ).build())
                codeView.setCode(code)
            }
        }
    }
}
