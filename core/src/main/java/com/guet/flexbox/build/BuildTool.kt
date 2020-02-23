package com.guet.flexbox.build

import android.content.Context
import androidx.annotation.RestrictTo
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.ELContext
import com.guet.flexbox.el.PropsELContext
import com.guet.flexbox.transaction.PageContext
import com.guet.flexbox.transaction.dispatch.ActionBridge

abstract class BuildTool {

    protected abstract val widgets: Map<String, ToWidget>

    protected abstract val kits: List<Kit>

    private companion object {
        private val default: ToWidget = CommonProps to null
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun buildRoot(
            templateNode: TemplateNode,
            data: Any?,
            bridge: ActionBridge,
            other: Any
    ): Child {
        val pageContextImpl = bridge.newPageContext()
        val elContext = PropsELContext(data, pageContextImpl.newWrapper())
        return buildAll(
                listOf(templateNode),
                pageContextImpl,
                elContext,
                true,
                other
        ).single()
    }

    internal fun buildAll(
            templates: List<TemplateNode>,
            pageContext: PageContext,
            data: ELContext,
            upperVisibility: Boolean,
            other: Any
    ): List<Child> {
        if (templates.isEmpty()) {
            return emptyList()
        }
        return templates.map { templateNode ->
            val type = templateNode.type
            val toWidget: ToWidget = widgets[type] ?: default
            toWidget.toWidget(
                    this@BuildTool,
                    templateNode,
                    pageContext,
                    data,
                    upperVisibility,
                    other
            )
        }.flatten()
    }

    fun init(context: Context) {
        kits.forEach {
            it.init(context)
        }
    }
}
