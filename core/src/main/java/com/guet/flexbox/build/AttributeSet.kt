package com.guet.flexbox.build

import android.content.Context
import android.util.AttributeSet
import com.guet.flexbox.el.PropsELContext

class AttributeSet(
        val declarations: Map<String, Any>,
        private val raw: Map<String, String>,
        private val data: PropsELContext
) {
    fun createAndroidAttributeSet(c: Context): AttributeSet {
        return AndroidAttributeSet(c, raw, data)
    }
}