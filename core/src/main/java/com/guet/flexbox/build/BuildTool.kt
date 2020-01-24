package com.guet.flexbox.build

import com.guet.flexbox.ConcurrentUtils
import com.guet.flexbox.HostContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.ELContext
import com.guet.flexbox.el.ScopeELContext
import java.util.concurrent.Callable
import java.util.concurrent.ForkJoinTask

abstract class BuildTool {

    protected abstract val widgets: Map<String, ToWidget>

    companion object {
        private val default: ToWidget = Common to null
    }

    fun build(
            templateNode: TemplateNode,
            hostContext: HostContext,
            data: ELContext,
            c: Any
    ): Child {
        return ConcurrentUtils.forkJoinPool
                .invoke(ForkJoinTask.adapt(
                        BuildTask(
                                templateNode,
                                hostContext,
                                data,
                                true,
                                c
                        ))).single()
    }

    internal fun createBuildTasks(
            templates: List<TemplateNode>,
            hostContext: HostContext,
            data: ELContext,
            upperVisibility: Boolean,
            other: Any,
            scope: Map<String, Any>? = null
    ): List<Callable<List<Child>>> {
        return if (templates.isEmpty()) {
            emptyList()
        } else {
            templates.map {
                BuildTask(
                        it,
                        hostContext,
                        data,
                        upperVisibility,
                        other
                ).apply {
                    if (scope != null) {
                        enterLambdaScope(scope)
                    }
                }
            }
        }
    }

    internal fun invokeAllTasks(
            tasks: List<Callable<List<Child>>>
    ): List<Child> {
        if (tasks.isEmpty()) {
            return emptyList()
        }
        val futures = ConcurrentUtils
                .forkJoinPool
                .invokeAll(tasks)
        return futures.map {
            it.get()
        }.flatten()
    }

    internal fun buildAll(
            templates: List<TemplateNode>,
            hostContext: HostContext,
            data: ELContext,
            upperVisibility: Boolean,
            other: Any
    ): List<Child> {
        if (templates.isEmpty()) {
            return emptyList()
        }
        return invokeAllTasks(
                createBuildTasks(
                        templates,
                        hostContext,
                        data,
                        upperVisibility,
                        other
                )
        )
    }

    private inner class BuildTask(
            private val templateNode: TemplateNode,
            private val hostContext: HostContext,
            target: ELContext,
            private val upperVisibility: Boolean,
            private val other: Any
    ) : ScopeELContext(target), Callable<List<Child>> {
        override fun call(): List<Child> {
            val type = templateNode.type
            val toWidget: ToWidget = widgets[type] ?: default
            return toWidget.toWidget(
                    this@BuildTool,
                    templateNode,
                    hostContext,
                    this,
                    upperVisibility,
                    other
            )
        }
    }
}
