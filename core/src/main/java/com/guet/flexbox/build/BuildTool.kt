package com.guet.flexbox.build

import android.content.Context
import androidx.annotation.RestrictTo
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.eventsystem.EventDispatcher
import com.guet.flexbox.eventsystem.EventTarget
import org.apache.commons.jexl3.JexlBuilder
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlEngine

abstract class BuildTool {

    protected abstract val widgets: Map<String, ToWidget>

    protected abstract val kits: List<Kit>

    private companion object {
        private val default = ToWidget(CommonProps, null)
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun buildRoot(
            templateNode: TemplateNode,
            dataContext: JexlContext,
            eventDispatcher: EventTarget,
            other: Any?
    ): Any {
        return buildAll(
                listOf(templateNode),
                dataContext,
                eventDispatcher,
                other
        ).single()
    }

    open val engine: JexlEngine by lazy {
        JexlBuilder().create()
    }

    internal fun buildAll(
            templates: List<TemplateNode>,
            dataContext: JexlContext,
            eventDispatcher: EventTarget,
            other: Any?,
            upperVisibility: Boolean = true
    ): List<Any> {
        if (templates.isEmpty()) {
            return emptyList()
        }
        return templates.map { templateNode ->
            val type = templateNode.type
            val toWidget: ToWidget = widgets[type] ?: default
            toWidget.toWidget(
                    this@BuildTool,
                    templateNode,
                    dataContext,
                    eventDispatcher,
                    other,
                    upperVisibility
            )
        }.flatten()
    }

    fun init(context: Context) {
        kits.forEach {
            it.init(context)
        }
    }
}
