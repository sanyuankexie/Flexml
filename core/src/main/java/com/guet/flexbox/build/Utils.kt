package com.guet.flexbox.build

import android.view.View
import com.guet.flexbox.HostContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.build.attrsinfo.AttributeInfo
import com.guet.flexbox.build.attrsinfo.AttrsInfoRegistry
import com.guet.flexbox.el.ELContext
import com.guet.flexbox.el.LambdaExpression

internal inline val CharSequence.isExpr: Boolean
    get() = length > 3 && startsWith("\${") && endsWith('}')

internal typealias AttributeInfoSet = Map<String, AttributeInfo<*>>

internal inline fun create(crossinline action: AttrsInfoRegistry.() -> Unit): Lazy<AttributeInfoSet> {
    return lazy {
        AttrsInfoRegistry().apply(action).value
    }
}

typealias AttributeSet = Map<String, Any>

internal typealias Converter<T> = (HostContext, ELContext, String) -> T?

typealias OutputFactory = (
        visibility: Boolean,
        attrs: AttributeSet,
        children: List<Child>,
        other: Any
) -> Any

typealias Child = Any

typealias ToWidget = Pair<Declaration, OutputFactory?>

internal fun ToWidget.toWidget(
        bindings: BuildTool,
        template: TemplateNode,
        pageContext: HostContext,
        data: ELContext,
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

internal fun LambdaExpression.execute(
        elContext: ELContext,
        vararg values: Any?
) {
    @Suppress("UNCHECKED_CAST")
    this.invoke(elContext, values)?.run {
        this as? Set<(ELContext) -> Unit>
    }?.firstOrNull()?.invoke(elContext)
}

typealias EventHandler = (View?, Array<out Any?>?) -> Unit