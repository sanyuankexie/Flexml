package com.guet.flexbox.build

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RestrictTo
import com.guet.flexbox.BuildConfig
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.context.PropContext
import com.guet.flexbox.eventsystem.EventTarget
import org.apache.commons.jexl3.JexlBuilder
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlEngine
import java.util.stream.Collectors

abstract class BuildTool {

    protected abstract val widgets: Map<String, ToWidget>

    protected abstract val kits: List<Kit>

    companion object {
        private val default = object : Config {
            override val engine: JexlEngine = JexlBuilder()
                    .silent(!BuildConfig.DEBUG)
                    .strict(false)
                    .create()
        }

        private val fallback = ToWidget(CommonProps, null)

        fun newContext(
                data: Any?,
                target: EventTarget,
                config: Config = default
        ): PropContext {
            return PropContext(data, target, config.engine)
        }
    }

    @Suppress("UNCHECKED_CAST")
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun <T> buildRoot(
            templateNode: TemplateNode,
            dataContext: PropContext,
            eventDispatcher: EventTarget,
            other: Any?,
            config: Config = default
    ): T {
        return buildRoot(
                templateNode,
                config.engine,
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
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            BuildAllMapperApi21.map(
                    this,
                    templates,
                    engine,
                    dataContext,
                    eventDispatcher,
                    other,
                    upperDisplay
            )
        } else {
            BuildAllMapper0.map(
                    this,
                    templates,
                    engine,
                    dataContext,
                    eventDispatcher,
                    other,
                    upperDisplay
            )
        }
    }

    private interface BuildAllMapper {
        fun map(
                buildTool: BuildTool,
                templates: List<TemplateNode>,
                engine: JexlEngine,
                dataContext: JexlContext,
                eventDispatcher: EventTarget,
                other: Any?,
                upperDisplay: Boolean = true
        ): List<Any>
    }

    private object BuildAllMapper0 : BuildAllMapper {
        override fun map(
                buildTool: BuildTool,
                templates: List<TemplateNode>,
                engine: JexlEngine,
                dataContext: JexlContext,
                eventDispatcher: EventTarget,
                other: Any?,
                upperDisplay: Boolean
        ): List<Any> {
            return templates.asSequence().map { templateNode ->
                val type = templateNode.type
                val toWidget: ToWidget = buildTool.widgets[type] ?: fallback
                toWidget.toWidget(
                        buildTool,
                        templateNode,
                        engine,
                        dataContext,
                        eventDispatcher,
                        other,
                        upperDisplay
                )
            }.flatten().toList()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private object BuildAllMapperApi21 : BuildAllMapper {
        override fun map(
                buildTool: BuildTool,
                templates: List<TemplateNode>,
                engine: JexlEngine,
                dataContext: JexlContext,
                eventDispatcher: EventTarget,
                other: Any?,
                upperDisplay: Boolean
        ): List<Any> {
            return templates.stream().map { templateNode ->
                val type = templateNode.type
                val toWidget: ToWidget = buildTool.widgets[type] ?: fallback
                toWidget.toWidget(
                        buildTool,
                        templateNode,
                        engine,
                        dataContext,
                        eventDispatcher,
                        other,
                        upperDisplay
                )
            }.flatMap {
                it.stream()
            }.collect(Collectors.toList())
        }
    }

    fun init(context: Context) {
        kits.forEach {
            it.init(context)
        }
    }

    interface Config {
        val engine: JexlEngine
    }
}
