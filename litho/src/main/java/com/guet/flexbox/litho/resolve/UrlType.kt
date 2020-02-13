package com.guet.flexbox.litho.resolve

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable.Orientation
import android.net.Uri
import android.text.TextUtils

enum class UrlType {
    GRADIENT,
    URL,
    RESOURCE,
    COLOR,
    ERROR;

    companion object {

        private val orientations: Map<String, Orientation> = mapOf(
                "t2b" to Orientation.TOP_BOTTOM,
                "tr2bl" to Orientation.TR_BL,
                "l2r" to Orientation.LEFT_RIGHT,
                "br2tl" to Orientation.BR_TL,
                "b2t" to Orientation.BOTTOM_TOP,
                "r2l" to Orientation.RIGHT_LEFT,
                "tl2br" to Orientation.TL_BR
        )

        internal fun parseUrl(
                c: Context,
                url: CharSequence

        ): Pair<UrlType, Array<Any>> {
            when {
                TextUtils.isEmpty(url) -> {
                    return ERROR to emptyArray()
                }
                url.startsWith("gradient://") -> {
                    val uri = Uri.parse(url.toString())
                    val type =
                            uri.getQueryParameter(
                                    "orientation"
                            )?.run {
                                orientations.getValue(this)
                            }
                    val colors = uri.getQueryParameters("color")?.map {
                        Color.parseColor(it)
                    }?.toIntArray()
                    return if (type != null && colors != null
                            && colors.isNotEmpty()) {
                        GRADIENT to arrayOf(type, colors as Any)
                    } else {
                        ERROR to emptyArray()
                    }
                }
                url.startsWith("res://") -> {
                    val uri = Uri.parse(url.toString())
                    when (uri.host) {
                        "drawable" -> {
                            val name = uri.getQueryParameter("name")
                            if (name != null) {
                                val id = c.resources.getIdentifier(
                                        name,
                                        "drawable",
                                        c.packageName
                                )
                                if (id != 0) {
                                    return RESOURCE to arrayOf(id as Any)
                                }
                            }
                            return ERROR to emptyArray()
                        }
                        else -> {
                            return ERROR to emptyArray()
                        }
                    }
                }
                else -> {
                    return try {
                        val color = Color.parseColor(url.toString())
                        COLOR to arrayOf(color as Any)
                    } catch (e: Exception) {
                        URL to arrayOf(url as Any)
                    }
                }
            }
        }
    }
}