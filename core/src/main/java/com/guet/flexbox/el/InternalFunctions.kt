package com.guet.flexbox.el

import android.content.res.Resources
import android.net.Uri
import androidx.annotation.RestrictTo

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
object InternalFunctions {

    private fun buildUri(
            scheme: String,
            type: String,
            map: (List<Pair<String, String>>) = emptyList()
    ): String {
        return Uri.Builder()
                .scheme(scheme)
                .authority(type)
                .apply {
                    map.forEach {
                        appendQueryParameter(it.first, it.second)
                    }
                }
                .build()
                .toString()
    }

    @JvmName("linear")
    @JvmStatic
    fun linear(orientation: String, vararg colors: String): String {
        return buildUri("gradient", "linear",
                mutableListOf("orientation" to orientation)
                        .apply {
                            addAll(colors.map {
                                "color" to it
                            })
                        }
        )
    }

    @JvmName("drawable")
    @JvmStatic
    fun drawable(name: String): String {
        return buildUri("res", "drawable", listOf("name" to name))
    }

    @JvmName("px")
    @JvmStatic
    fun px(value: Number): Float {
        return value.toFloat() / Resources.getSystem().displayMetrics.widthPixels / 360.0f
    }

    @JvmName("sp")
    @JvmStatic
    fun sp(value: Number): Float {
        return (px(value) * Resources.getSystem().displayMetrics.scaledDensity + 0.5f)
    }

    @JvmName("dp")
    @JvmStatic
    fun dp(value: Number): Float {
        return (px(value) * Resources.getSystem().displayMetrics.density + 0.5f)
    }

}