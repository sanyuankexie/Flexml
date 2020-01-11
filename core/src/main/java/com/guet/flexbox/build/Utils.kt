package com.guet.flexbox.build

import android.graphics.Color
import com.guet.flexbox.PageContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.PropsELContext

internal inline val CharSequence.isExpr: Boolean
    get() = length > 3 && startsWith("\${") && endsWith('}')

typealias AttributeSet = Map<String, AttributeInfo<*>>

internal inline fun create(crossinline action: Registry.() -> Unit): Lazy<AttributeSet> {
    return lazy {
        Registry().apply(action).value
    }
}

internal class Registry {
    private val _value = HashMap<String, AttributeInfo<*>>()

    internal fun text(
            name: String,
            scope: (Map<String, String>) = emptyMap(),
            fallback: String = ""
    ) {
        _value[name] = TextAttribute(scope, fallback)
    }

    internal fun bool(
            name: String,
            scope: Map<String, Boolean> = emptyMap(),
            fallback: Boolean = false
    ) {
        _value[name] = BoolAttribute(scope, fallback)
    }

    internal fun value(
            name: String,
            scope: Map<String, Double> = emptyMap(),
            fallback: Double = 0.0
    ) {
        _value[name] = ValueAttribute(scope, fallback)
    }

    internal fun color(
            name: String,
            scope: Map<String, Int> = emptyMap(),
            fallback: Int = Color.TRANSPARENT
    ) {
        _value[name] = ColorAttribute(scope, fallback)
    }

    internal inline fun <reified V : Enum<V>> enum(
            name: String,
            scope: Map<String, V>,
            fallback: V = enumValues<V>().first()
    ) {
        _value[name] = EnumAttribute(scope, fallback)
    }

    internal inline fun <T : Any> typed(
            name: String,
            scope: (Map<String, T>) = emptyMap(),
            fallback: T? = null,
            crossinline action: (PageContext, PropsELContext, String) -> T?
    ) {
        _value[name] = object : AttributeInfo<T>(scope, fallback) {
            override fun cast(pageContext: PageContext, props: PropsELContext, raw: String): T? {
                return action(pageContext, props, raw)
            }
        }
    }

    val value: AttributeSet
        get() = _value
}

internal typealias Factory = (
        type: String,
        visibility: Boolean,
        attrs: Map<String, Any>,
        children: List<Any>,
        other: Any
) -> Any

typealias ToWidget = Pair<Declaration, Factory?>

internal fun ToWidget.toWidget(
        bindings: BuildUtils,
        template: TemplateNode,
        pageContext: PageContext,
        data: PropsELContext,
        upperVisibility: Boolean,
        other: Any
): List<Any> {
    return first.transform(
            bindings,
            template,
            second,
            pageContext,
            data,
            upperVisibility,
            other
    )
}

typealias EventHandler<T> = (T) -> Unit