package com.guet.flexbox.litho

import android.content.res.Resources
import com.facebook.litho.Component

private val pt = Resources.getSystem().displayMetrics.widthPixels / 360f

fun <T : Number> T.toPx(): Int {
    return (this.toDouble() * pt).toInt()
}

internal typealias ChildComponent = Component


