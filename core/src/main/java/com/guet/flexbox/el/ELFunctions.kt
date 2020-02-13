package com.guet.flexbox.el

import android.content.res.Resources
import android.net.Uri
import java.lang.reflect.Modifier

internal object ELFunctions {

    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.RUNTIME)
    private annotation class Prefix(val value: String)

    internal val functions = ELFunctions::class.java
            .declaredMethods
            .filter {
                it.modifiers.let { mod ->
                    Modifier.isPublic(mod) && Modifier.isStatic(mod)
                } && it.isAnnotationPresent(Prefix::class.java)
            }.map {
                it.apply {
                    it.isAccessible = true
                }.let { m ->
                    "${m.getAnnotation(Prefix::class.java).value}:${m.name}" to m
                }
            }.toMap()

    @Prefix("utils")
    @JvmName("check")
    @JvmStatic
    fun check(o: Any?): Boolean {
        return when (o) {
            is String -> o.isNotEmpty()
            is Collection<*> -> !o.isEmpty()
            is Number -> o.toInt() != 0
            else -> o != null
        }
    }

    @Prefix("utils")
    @JvmName("flags")
    @JvmStatic
    fun flags(vararg value: Int): Int {
        var flags = 0
        value.forEach {
            flags = flags or it
        }
        return flags
    }

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

    @Prefix("gradient")
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

    @Prefix("res")
    @JvmName("drawable")
    @JvmStatic
    fun drawable(name: String): String {
        return buildUri("res", "drawable", listOf("name" to name))
    }

    @Prefix("dimen")
    @JvmName("px")
    @JvmStatic
    fun px(value: Number): Float {
        return value.toFloat() / Resources.getSystem().displayMetrics.widthPixels / 360.0f
    }

    @Prefix("dimen")
    @JvmName("sp")
    @JvmStatic
    fun sp(value: Number): Float {
        return (px(value) * Resources.getSystem().displayMetrics.scaledDensity + 0.5f)
    }

    @Prefix("dimen")
    @JvmName("dp")
    @JvmStatic
    fun dp(value: Number): Float {
        return (px(value) * Resources.getSystem().displayMetrics.density + 0.5f)
    }


}