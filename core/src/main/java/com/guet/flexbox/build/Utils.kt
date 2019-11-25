@file:JvmName("-Utils")

package com.guet.flexbox.build

import android.content.res.Resources
import android.graphics.drawable.GradientDrawable.Orientation
import android.view.View
import androidx.annotation.ColorInt
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.BuildConfig
import com.guet.flexbox.NodeInfo
import com.guet.flexbox.el.ELException

internal typealias Mapping<T> = T.(BuildContext, Map<String, String>, Boolean, String) -> Unit

internal typealias Mappings<T> = HashMap<String, Mapping<T>>

internal typealias Apply<T, V> = T.(Map<String, String>, Boolean, V) -> Unit

private val transforms = mapOf(
        "Image" to ImageFactory,
        "Flex" to FlexFactory,
        "Text" to TextFactory,
        "Stack" to StackFactory,
        "Native" to NativeFactory,
        "Scroller" to ScrollerFactory,
        "Empty" to EmptyFactory,
        "TextInput" to TextInputFactory,
        "for" to ForBehavior,
        "foreach" to ForEachBehavior,
        "if" to IfBehavior
)

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

internal inline fun <reified T : Any> BuildContext.tryGetValue(expr: String?, fallback: T): T {
    if (expr == null) {
        return fallback
    }
    return try {
        getValue(expr)
    } catch (e: ELException) {
        if (BuildConfig.DEBUG) {
            e.printStackTrace()
        }
        fallback
    }
}

internal inline fun <T> BuildContext.scope(scope: Map<String, Any>, action: () -> T): T {
    enterScope(scope)
    try {
        return action()
    } finally {
        exitScope()
    }
}

internal inline fun <reified T : Enum<T>> BuildContext.tryGetEnum(
        expr: String?,
        scope: Map<String, T>,
        fallback: T = enumValues<T>().first()): T {
    return when {
        expr == null -> fallback
        expr.isExpr -> scope(scope) {
            tryGetValue(expr, fallback)
        }
        else -> scope[expr] ?: fallback
    }
}

internal inline fun <reified T : Any> BuildContext.requestValue(
        name: String,
        attrs: Map<String, String>
): T {
    return getValue(attrs[name] ?: error("request attr '$name'"))
}

@ColorInt
internal fun BuildContext.tryGetColor(expr: String?, @ColorInt fallback: Int): Int {
    if (expr == null) {
        return fallback
    }
    return try {
        getColor(expr)
    } catch (e: ELException) {
        fallback
    }
}

internal inline fun <reified N : Number> Number.safeCast(): N {
    return safeCast(N::class.javaObjectType) as N
}

private fun Number.safeCast(type: Class<*>): Any {
    return when (type) {
        Byte::class.javaObjectType -> this.toByte()
        Char::class.javaObjectType -> this.toChar()
        Int::class.javaObjectType -> this.toInt()
        Short::class.javaObjectType -> this.toShort()
        Long::class.javaObjectType -> this.toLong()
        Float::class.javaObjectType -> this.toFloat()
        Double::class.javaObjectType -> this.toDouble()
        else -> error("no match number type ${type.name}")
    }
}

internal fun ComponentContext.createFromElement(
        buildContext: BuildContext,
        element: NodeInfo,
        upperVisibility: Int = View.VISIBLE
): List<Component> {
    return transforms[element.type]?.transform(
            this,
            buildContext,
            element,
            upperVisibility
    ) ?: emptyList()
}