package com.guet.flexbox.build

import android.os.Build
import androidx.annotation.RequiresApi
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.context.ScopeContext
import com.guet.flexbox.eventsystem.EventTarget
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlEngine
import org.apache.commons.jexl3.MapContext
import java.util.stream.Collectors
import java.util.stream.IntStream
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
                engine.createExpression(trim).evaluate(MapContext()) as Array<*>
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
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ForEachMapperApi24.map(
                    buildTool,
                    name,
                    items,
                    children,
                    engine,
                    dataContext,
                    eventDispatcher,
                    other,
                    upperDisplay
            )
        } else {
            ForEachMapper0.map(
                    buildTool,
                    name,
                    items,
                    children,
                    engine,
                    dataContext,
                    eventDispatcher,
                    other,
                    upperDisplay
            )
        }
    }

    private interface ForEachMapper {
        fun map(
                buildTool: BuildTool,
                name: String,
                items: Any,
                children: List<TemplateNode>,
                engine: JexlEngine,
                dataContext: JexlContext,
                eventDispatcher: EventTarget,
                other: Any?,
                upperVisibility: Boolean
        ): List<Any>
    }

    private object ForEachMapper0 : ForEachMapper {
        override fun map(
                buildTool: BuildTool,
                name: String,
                items: Any,
                children: List<TemplateNode>,
                engine: JexlEngine,
                dataContext: JexlContext,
                eventDispatcher: EventTarget,
                other: Any?,
                upperVisibility: Boolean
        ): List<Any> {
            return if (items.javaClass.isArray) {
                (0 until RArray.getLength(items)).asSequence().map {
                    val item = RArray.get(items, it)
                    val scope = ScopeContext(mapOf(name to item), dataContext)
                    buildTool.buildAll(
                            children,
                            engine,
                            scope,
                            eventDispatcher,
                            other,
                            upperVisibility
                    )
                }.flatten().toList()
            } else {
                val collection = items as Collection<*>
                collection.asSequence().map {
                    val scope = ScopeContext(mapOf(name to it), dataContext)
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
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private object ForEachMapperApi24 : ForEachMapper {
        override fun map(
                buildTool: BuildTool,
                name: String,
                items: Any,
                children: List<TemplateNode>,
                engine: JexlEngine,
                dataContext: JexlContext,
                eventDispatcher: EventTarget,
                other: Any?,
                upperVisibility: Boolean
        ): List<Any> {
            return if (items.javaClass.isArray) {
                IntStream.range(0, RArray.getLength(items) - 1).mapToObj {
                    val item = RArray.get(items, it)
                    val scope = ScopeContext(mapOf(name to item), dataContext)
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
            } else {
                val collection = items as Collection<*>
                collection.stream().map {
                    val scope = ScopeContext(mapOf(name to it), dataContext)
                    buildTool.buildAll(
                            children,
                            engine,
                            scope,
                            eventDispatcher,
                            other,
                            upperVisibility
                    )
                }.collect(Collectors.toList())
            }
        }
    }
}