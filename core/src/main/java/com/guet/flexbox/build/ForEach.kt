package com.guet.flexbox.build

import com.guet.flexbox.PageContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.ScopeContext
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlEngine
import org.apache.commons.jexl3.MapContext
import java.lang.reflect.Array as RArray

object ForEach : Declaration() {

    override val dataBinding by DataBinding.create {
        text("var")
        typed("items", TextToItems)
    }

    private object TextToItems : TextToAttribute<Any> {
        override fun cast(
                engine: JexlEngine,
                dataContext: JexlContext,
                pageContext: PageContext,
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
                engine.createExpression(trim).evaluate(MapContext()) as Array<*>
            } else {
                null
            }
        }
    }

    override fun onBuildWidget(
            buildTool: BuildTool,
            attrs: AttributeSet,
            children: List<TemplateNode>,
            factory: RenderNodeFactory<*>?,
            dataContext: JexlContext,
            pageContext: PageContext,
            other: Any?,
            upperVisibility: Boolean
    ): List<Any> {
        val name = attrs.getValue("var") as String
        val items = attrs.getValue("items")
        return if (items.javaClass.isArray) {
            (0 until RArray.getLength(items)).map {
                val item = RArray.get(items, it)
                val scope = ScopeContext(mapOf(name to item), dataContext)
                buildTool.buildAll(
                        children,
                        scope,
                        pageContext,
                        other,
                        upperVisibility
                )
            }.flatten()
        } else {
            val collection = items as Collection<*>
            collection.map {
                val scope = ScopeContext(mapOf(name to it), dataContext)
                buildTool.buildAll(
                        children,
                        scope,
                        pageContext,
                        other,
                        upperVisibility
                )
            }.flatten()
        }
    }
}