package com.guet.flexbox.build

import android.os.Build
import androidx.annotation.RequiresApi
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.context.ScopeContext
import com.guet.flexbox.eventsystem.EventTarget
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlEngine
import java.util.stream.Collectors
import java.util.stream.IntStream

object For : Declaration() {

    override val dataBinding by DataBinding
            .create {
                text("var")
                value("from")
                value("to")
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
        val from = (attrs.getValue("from") as Float).toInt()
        val to = (attrs.getValue("to") as Float).toInt()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ForMapperApi24.map(
                    buildTool,
                    name,
                    from,
                    to,
                    children,
                    engine,
                    dataContext,
                    eventDispatcher,
                    other,
                    upperDisplay
            )
        } else {
            ForMapper0.map(
                    buildTool,
                    name,
                    from,
                    to,
                    children,
                    engine,
                    dataContext,
                    eventDispatcher,
                    other,
                    upperDisplay
            )
        }
    }

    private interface ForMapper {
        fun map(
                buildTool: BuildTool,
                name: String,
                form: Int,
                to: Int,
                children: List<TemplateNode>,
                engine: JexlEngine,
                dataContext: JexlContext,
                eventDispatcher: EventTarget,
                other: Any?,
                upperVisibility: Boolean
        ): List<Any>
    }

    private object ForMapper0 : ForMapper {
        override fun map(
                buildTool: BuildTool,
                name: String,
                form: Int,
                to: Int,
                children: List<TemplateNode>,
                engine: JexlEngine,
                dataContext: JexlContext,
                eventDispatcher: EventTarget,
                other: Any?,
                upperVisibility: Boolean
        ): List<Any> {
            return (form..to).asSequence().map { index ->
                val scope = ScopeContext(mapOf(name to index), dataContext)
                buildTool.buildAll(
                        children,
                        engine,
                        scope,
                        eventDispatcher,
                        other,
                        upperVisibility
                )
            }.flatten().toList()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private object ForMapperApi24 : ForMapper {
        override fun map(
                buildTool: BuildTool,
                name: String,
                form: Int,
                to: Int,
                children: List<TemplateNode>,
                engine: JexlEngine,
                dataContext: JexlContext,
                eventDispatcher: EventTarget,
                other: Any?,
                upperVisibility: Boolean
        ): List<Any> {
            return IntStream.range(form, to + 1).mapToObj { index ->
                val scope = ScopeContext(mapOf(name to index), dataContext)
                buildTool.buildAll(
                        children,
                        engine,
                        scope,
                        eventDispatcher,
                        other,
                        upperVisibility
                )
            }.flatMap {
                it.stream()
            }.collect(Collectors.toList())
        }
    }

}