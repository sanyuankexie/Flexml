package com.guet.flexbox.playground.test

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.didichuxing.doraemonkit.util.UIUtils
import com.guet.flexbox.playground.R

class TestActivity : AppCompatActivity() {

    private lateinit var rv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        rv = findViewById(R.id.host)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = object : BaseQuickAdapter<Any, BaseViewHolder>(R.layout.feed_item) {
            override fun convert(helper: BaseViewHolder, item: Any?) {

            }
        }.apply {
            addHeaderView(View(this@TestActivity).apply {
                layoutParams = RecyclerView.LayoutParams(0, 0).apply {
                    width = ViewGroup.LayoutParams.MATCH_PARENT
                    height = UIUtils.dp2px(this@TestActivity, 1000f)
                }
                setBackgroundColor(Color.RED)
            })
        }
    }
}
