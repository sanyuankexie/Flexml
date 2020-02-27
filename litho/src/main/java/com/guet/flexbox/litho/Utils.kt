package com.guet.flexbox.litho

import android.content.res.Resources
import com.facebook.litho.Component
import com.guet.flexbox.build.PropSet

val pt = Resources.getSystem().displayMetrics.widthPixels / 360f

inline fun <reified T : Number> T.toPx(): Int {
    return (this.toFloat() * pt).toInt()
}

inline fun <reified T : Number> T.toPxFloat(): Float {
    return (this.toFloat() * pt)
}

internal typealias Widget = Component

internal fun PropSet.getFloatValue(name: String): Float {
    return (this[name] as? Float) ?: 0f
}



