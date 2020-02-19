package com.guet.flexbox.playground.widget

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.TextView
import io.iftech.android.library.refresh.RefreshView

class MyRefreshViewImpl(context: Context) : RefreshView {

    private val internalView = TextView(context).apply {
        gravity = Gravity.CENTER
        text = "下拉刷新"
    }
    private var visibleHeight = 0
    private var isLoading = false
        set(value) {
            field = value
            internalView.text = if (value) "正在加载..." else "下拉刷新"
        }
    private var isRestore = false

    override val view: View
        get() = internalView

    override fun canDrag(): Boolean {
        return isLoading.not() && isRestore.not()
    }

    override fun canRefresh(): Boolean {
        return visibleHeight > REFRESH_HEIGHT
    }

    override fun isLoading(): Boolean {
        return isLoading
    }

    override fun isRestore(): Boolean {
        return isRestore
    }

    @SuppressLint("SetTextI18n")
    override fun updateDragging(fraction: Float) {
    }

    override fun startLoading() {
        isLoading = true
    }

    override fun restore() {
        if (isLoading) {
            isLoading = false
            isRestore = true
        }
    }

    override fun reset() {
        isLoading = false
        isRestore = false
    }

    override fun updateVisibleHeight(height: Int) {
        visibleHeight = height
        if (isLoading.not()) {
            internalView.text = if (canRefresh()) "松手刷新" else "下拉加载"
        }
    }

    companion object {
        const val REFRESH_HEIGHT = 300
    }
}