package com.guet.flexbox.build

import androidx.annotation.RestrictTo
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.context.ScopeContext
import com.guet.flexbox.eventsystem.EventTarget
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlEngine
import org.apache.commons.jexl3.MapContext
import java.util.*
import java.lang.reflect.Array as RArray

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
object ForEach : Declaration() {

    override val dataBinding by DataBinding.create {
        text("var")
        typed("items", TextToItems)
    }

    private object TextToItems : TextToAttribute<Any> {
        override fun cast(
                engine: JexlEngine,
                dataContext: JexlContext,
                eventDispatcher: EventTarget,
                raw: String
        ): Any? {
            val trim = raw.trim()
            return if (raw.isExpr) {
                val expr = engine.createExpression(raw.innerExpr)
                val o = expr.evaluate(dataContext)
                if (o != null && (o.javaClass.isArray || o is Collection<*>)) {
                    o
                } else {
                    null
                }
            } else if (trim.startsWith("[") && trim.endsWith("]")) {
                engine.createExpression(trim).evaluate(MapContext(emptyMap())) as Array<*>
            } else {
                null
            }
        }
    }

    override fun onBuildWidget(
            buildTool: BuildTool,
            rawAttrs: Map<String, String>,
            children: List<TemplateNode>,
            factory: RenderNodeFactory<*>?,
            engine: JexlEngine,
            dataContext: JexlContext,
            eventDispatcher: EventTarget,
            other: Any?,
            upperDisplay: Boolean
    ): List<Any> {
        val attrs = bindAttrs(rawAttrs, engine, dataContext, eventDispatcher)
        val name = attrs.getValue("var") as String
        val items = attrs.getValue("items")
        if (items.javaClass.isArray) {
            val length = RArray.getLength(items)
            if (length == 0) {
                return emptyList()
            }
            val list = LinkedList<Any>()
            for (index in 0 until length) {
                val item = RArray.get(items, index)
                val scope = ScopeContext(mapOf(name to item), dataContext)
                val subList = buildTool.buildAll(
                        children,
                        engine,
                        scope,
                        eventDispatcher,
                        other,
                        upperDisplay
                )
                list.addAll(subList)
            }
            return list
        } else {
            val collection = items as Collection<*>
            if (collection.isEmpty()) {
                return emptyList()
            }
            val list = LinkedList<Any>()
            for (item in collection) {
                val scope = ScopeContext(mapOf(name to item), dataContext)
                val subList = buildTool.buildAll(
                        children,
                        engine,
                        scope,
                        eventDispatcher,
                        other,
                        upperDisplay
                )
                list.addAll(subList)
            }
            return list
        }
    }
}