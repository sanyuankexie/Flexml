package com.guet.flexbox.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.text.TextUtils
import com.facebook.litho.drawable.ComparableGradientDrawable

private val orientations: Map<String, GradientDrawable.Orientation> = mapOf(
        "t2b" to GradientDrawable.Orientation.TOP_BOTTOM,
        "tr2bl" to GradientDrawable.Orientation.TR_BL,
        "l2r" to GradientDrawable.Orientation.LEFT_RIGHT,
        "br2tl" to GradientDrawable.Orientation.BR_TL,
        "b2t" to GradientDrawable.Orientation.BOTTOM_TOP,
        "r2l" to GradientDrawable.Orientation.RIGHT_LEFT,
        "tl2br" to GradientDrawable.Orientation.TL_BR
)

private fun String.toOrientation(): GradientDrawable.Orientation {
    return orientations.getValue(this)
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