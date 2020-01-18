package com.guet.flexbox.build

import android.graphics.Color
import android.util.ArrayMap
import android.view.View
import com.guet.flexbox.HostingContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.LambdaExpression
import com.guet.flexbox.el.PropsELContext

internal inline val CharSequence.isExpr: Boolean
    get() = length > 3 && startsWith("\${") && endsWith('}')

internal typealias AttributeInfoSet = Map<String, AttributeInfo<*>>

internal inline fun create(crossinline action: Registry.() -> Unit): Lazy<AttributeInfoSet> {
    return lazy {
        Registry().apply(action).value
    }
}

typealias AttributeSet = Map<String, Any>

internal class Registry {
    private val _value = ArrayMap<String, AttributeInfo<*>>()

    fun text(
            name: String,
            scope: (Map<String, String>) = emptyMap(),
            fallback: String = ""
    ) {
        _value[name] = TextAttributeInfo(scope, fallback)
    }

    fun bool(
            name: String,
            scope: Map<String, Boolean> = emptyMap(),
            fallback: Boolean = false
    ) {
        _value[name] = BoolAttributeInfo(scope, fallback)
    }

    fun value(
            name: String,
            scope: Map<String, Double> = emptyMap(),
            fallback: Double = 0.0
    ) {
        _value[name] = ValueAttributeInfo(scope, fallback)
    }

    fun color(
            name: String,
            scope: Map<String, Int> = emptyMap(),
            fallback: Int = Color.TRANSPARENT
    ) {
        _value[name] = ColorAttributeInfo(scope, fallback)
    }

    inline fun <reified V : Enum<V>> enum(
            name: String,
            scope: Map<String, V>,
            fallback: V = enumValues<V>().first()
    ) {
        _value[name] = EnumAttributeInfo(scope, fallback)
    }

    inline fun <T : Any> typed(
            name: String,
            scope: (Map<String, T>) = emptyMap(),
            fallback: T? = null,
            crossinline action: (HostingContext, PropsELContext, String) -> T?
    ) {
        _value[name] = object : AttributeInfo<T>(scope, fallback) {
            override fun cast(pageContext: HostingContext, props: PropsELContext, raw: String): T? {
                return action(pageContext, props, raw)
            }
        }
    }

    val value: AttributeInfoSet
        get() = _value
}

typealias Factory = (
        visibility: Boolean,
        attrs: AttributeSet,
        children: List<Child>,
        other: Any
) -> Any

typealias Child = Any

typealias ToWidget = Pair<Declaration, Factory?>

internal fun ToWidget.toWidget(
        bindings: BuildUtils,
        template: TemplateNode,
        pageContext: HostingContext,
        data: PropsELContext,
        upperVisibility: Boolean,
        other: Any
): List<Child> {
    return first.transform(
            bindings,
            template.attrs ?: emptyMap(),
            template.children ?: emptyList(),
            second,
            pageContext,
            data,
            upperVisibility,
            other
    )
}

internal fun LambdaExpression.exec(
        elContext: PropsELContext,
        vararg values: Any?
) {
    @Suppress("UNCHECKED_CAST")
    this.invoke(elContext, values)?.run {
        this as? Set<(PropsELContext) -> Unit>
    }?.firstOrNull()?.invoke(elContext)
}

typealias EventHandler = (View, Array<out Any?>) -> Unit