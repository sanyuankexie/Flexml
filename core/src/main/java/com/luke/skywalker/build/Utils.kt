@file:JvmName("-Utils")

package com.luke.skywalker.build

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.GradientDrawable.Orientation
import android.net.Uri
import android.text.TextUtils
import com.facebook.litho.drawable.ComparableGradientDrawable
import com.luke.skywalker.el.PropsELContext

internal typealias Mapping<T> = T.(PropsELContext, Map<String, String>, Boolean, String) -> Unit

internal typealias Apply<T, V> = T.(Map<String, String>, Boolean, V) -> Unit

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

private fun String.toOrientation(): Orientation {
    return orientations.getValue(this)
}

internal inline fun <reified T : Number> T.toPx(): Int {
    return (this.toFloat() * Resources.getSystem().displayMetrics.widthPixels / 360f).toInt()
}

internal fun parseUrl(c: Context, url: CharSequence): Any? {
    when {
        TextUtils.isEmpty(url) -> {
            return null
        }
        url.startsWith("res://") -> {
            val uri = Uri.parse(url.toString())
            when (uri.host) {
                "gradient" -> {
                    val type = uri.getQueryParameter(
                            "orientation"
                    )?.toOrientation()
                    val colors = uri.getQueryParameters("color")?.map {
                        Color.parseColor(it)
                    }?.toIntArray()
                    return if (type != null && colors != null && colors.isNotEmpty()) {
                        ComparableGradientDrawable(type, colors)
                    } else {
                        null
                    }
                }
                "load" -> {
                    val name = uri.getQueryParameter("name")
                    if (name != null) {
                        val id = c.resources.getIdentifier(
                                name,
                                "drawable",
                                c.packageName
                        )
                        if (id != 0) {
                            return id
                        }
                    }
                    return null
                }
                else -> {
                    return null
                }
            }
        }
        else -> {
            return url
        }
    }
}
