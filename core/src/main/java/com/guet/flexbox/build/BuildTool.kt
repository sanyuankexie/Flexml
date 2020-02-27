package com.guet.flexbox.build

import android.content.Context
import androidx.annotation.RestrictTo
import com.guet.flexbox.BuildConfig
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.context.PropContext
import com.guet.flexbox.eventsystem.EventTarget
import org.apache.commons.jexl3.JexlBuilder
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlEngine
import java.util.*

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
abstract class BuildTool {

    private companion object {
        private val fallback = ToWidget(CommonProps, null)
    }

    protected abstract val widgets: Map<String, ToWidget>

    protected abstract val kits: List<Kit>

    protected open val engine: JexlEngine by lazy {
        JexlBuilder().silent(!BuildConfig.DEBUG)
                .strict(false)
                .create()
    }
    
    fun newContext(
            data: Any?,
            target: EventTarget
    ): PropContext {
        return PropContext(data, target, engine)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> buildRoot(
            templateNode: TemplateNode,
            dataContext: PropContext,
            eventDispatcher: EventTarget,
            other: Any?
    ): T {
        return buildRoot(
                templateNode,
                engine,
                dataContext,
                eventDispatcher,
                other
        ) as T
    }

    private fun buildRoot(
            templateNode: TemplateNode,
            engine: JexlEngine,
            dataContext: JexlContext,
            eventDispatcher: EventTarget,
            other: Any?
    ): Any {
        return buildAll(
                listOf(templateNode),
                engine,
                dataContext,
                eventDispatcher,
                other
        ).single()
    }

    internal fun buildAll(
            templates: List<TemplateNode>,
            engine: JexlEngine,
            dataContext: JexlContext,
            eventDispatcher: EventTarget,
            other: Any?,
            upperDisplay: Boolean = true
    ): List<Any> {
        if (templates.isEmpty()) {
            return emptyList()
        }
        val list = LinkedList<Any>()
        for (templateNode in templates) {
            val type = templateNode.type
            val toWidget: ToWidget = widgets[type] ?: fallback
            val subList = toWidget.toWidget(
                    this,
                    templateNode,
                    engine,
                    dataContext,
                    eventDispatcher,
                    other,
                    upperDisplay
            )
            list.addAll(subList)
        }
        return list
    }

    fun install(context: Context) {
        kits.forEach {
            it.init(context)
        }
    }
}
