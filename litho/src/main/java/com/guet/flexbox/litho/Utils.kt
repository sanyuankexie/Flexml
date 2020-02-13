package com.guet.flexbox.litho

import android.content.res.Resources
import com.facebook.litho.Component

val pt = Resources.getSystem().displayMetrics.widthPixels / 360f

inline fun <reified T : Number> T.toPx(): Int {
    return (this.toDouble() * pt).toInt()
}

inline fun <reified T : Number> T.toPxFloat(): Float {
    return (this.toDouble() * pt).toFloat()
}

internal typealias ChildComponent = Component


