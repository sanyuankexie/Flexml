package com.guet.flexbox.el

import android.content.res.Resources
import android.net.Uri

object Functions {

    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.RUNTIME)
    private annotation class Namespace(val value: String)

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

    private val functions = Functions::class.java
            .declaredClasses
            .filter {
                it.isAnnotationPresent(Namespace::class.java)
            }
            .map {
                it.getAnnotation(Namespace::class.java)!!.value to
                        it.getDeclaredField("INSTANCE").get(null)
            }.toMap()

    operator fun get(name: String?): Any? {
        return functions[name]
    }

    @Namespace("gradient")
    object Gradient {
        @JvmName("linear")
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
    }

    @Namespace("res")
    object Resource {
        @JvmName("drawable")
        fun drawable(name: String): String {
            return buildUri("res", "drawable", listOf("name" to name))
        }
    }

    @Namespace("dimen")
    object Dimension {

        @JvmName("sp")
        fun sp(value: Number): Float {
            return (px(value) * Resources.getSystem().displayMetrics.scaledDensity + 0.5f)
        }

        @JvmName("dp")
        fun dp(value: Number): Float {
            return (px(value) * Resources.getSystem().displayMetrics.density + 0.5f)
        }

        @JvmName("px")
        fun px(value: Number): Float {
            return value.toFloat() / Resources.getSystem().displayMetrics.widthPixels / 360.0f
        }
    }
}