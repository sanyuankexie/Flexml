package com.guet.flexbox.build

import android.util.ArrayMap
import androidx.annotation.RestrictTo
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.context.ScopeContext
import com.guet.flexbox.enums.Visibility
import com.guet.flexbox.eventsystem.EventTarget
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlEngine

@RestrictTo(RestrictTo.Scope.LIBRARY)
abstract class Widget : Definition() {

    private companion object {
        private val visibility: Map<String, Visibility>

        init {
            val visibility = ArrayMap<String, Visibility>()
            visibility["gone"] = Visibility.GONE
            visibility["invisible"] = Visibility.INVISIBLE
            visibility["visible"] = Visibility.VISIBLE
            this.visibility = visibility
        }
    }

    override fun onBuildWidget(
            buildTool: BuildTool,
            rawProps: Map<String, String>,
            children: List<TemplateNode>,
            factory: RenderNodeFactory<*>?,
            engine: JexlEngine,
            dataContext: JexlContext,
            eventDispatcher: EventTarget,
            other: Any?,
            upperDisplay: Boolean
    ): List<Any> {
        if (factory == null) {
            return emptyList()
        }
        val selfVisibility = parseSelfVisibility(
                rawProps, engine, dataContext, upperDisplay
        )
        if (selfVisibility == Visibility.GONE) {
            return emptyList()
        }
        val display = selfVisibility != Visibility.INVISIBLE
        val components = if (children.isEmpty()) {
            emptyList()
        } else {
            buildTool.buildAll(
                    children,
                    engine,
                    dataContext,
                    eventDispatcher,
                    other,
                    display
            )
        }
        val attrs = bindProps(
                rawProps,
                engine,
                dataContext,
                eventDispatcher
        )
        @Suppress("UNCHECKED_CAST")
        return listOf(factory.create(
                display,
                attrs,
                components as List<Nothing>,
                other
        ))
    }

    private fun parseSelfVisibility(
            rawAttrs: Map<String, String>,
            engine: JexlEngine,
            dataContext: JexlContext,
            upperDisplay: Boolean
    ): Visibility {
        val raw = rawAttrs["visibility"]
        if (raw.isNullOrEmpty()) {
            return Visibility.VISIBLE
        } else {
            val local = if (raw.isExpr) {
                engine.createExpression(raw.innerExpr)
                        .evaluate(ScopeContext(visibility, dataContext))
                        as? Visibility
                        ?: Visibility.GONE
            } else {
                visibility[raw] ?: Visibility.GONE
            }
            return if (local == Visibility.VISIBLE && upperDisplay) {
                Visibility.VISIBLE
            } else {
                local
            }
        }
    }
}
