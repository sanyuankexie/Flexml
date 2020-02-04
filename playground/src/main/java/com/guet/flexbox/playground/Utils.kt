package com.guet.flexbox.playground

import android.content.res.Resources
import kotlin.math.ceil

fun getStatusBarHeight(): Int {
    return ceil((25f * Resources.getSystem().displayMetrics.density).toDouble()).toInt()
}