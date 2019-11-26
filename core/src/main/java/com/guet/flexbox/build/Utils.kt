@file:JvmName("-Utils")

package com.guet.flexbox.build

import android.content.res.Resources
import android.graphics.drawable.GradientDrawable.Orientation

private val orientations: Map<String, Orientation> = mapOf(
        "t2b" to Orientation.TOP_BOTTOM,
        "tr2bl" to Orientation.TR_BL,
        "l2r" to Orientation.LEFT_RIGHT,
        "br2tl" to Orientation.BR_TL,
        "b2t" to Orientation.BOTTOM_TOP,
        "r2l" to Orientation.RIGHT_LEFT,
        "tl2br" to Orientation.TL_BR
)

internal inline val CharSequence.isExpr: Boolean
    get() = length > 3 && startsWith("\${") && endsWith('}')

internal fun String.toOrientation(): Orientation {
    return orientations.getValue(this)
}

internal inline fun <reified T : Number> T.toPx(): Int {
    return (this.toFloat() * Resources.getSystem().displayMetrics.widthPixels / 360f).toInt()
}