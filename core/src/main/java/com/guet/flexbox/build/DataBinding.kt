package com.guet.flexbox.build

import android.graphics.Color
import android.util.ArrayMap
import com.guet.flexbox.context.ScopeContext
import com.guet.flexbox.eventsystem.EventTarget
import com.guet.flexbox.eventsystem.ExternalEventReceiver
import com.guet.flexbox.eventsystem.ReceiveEventToExpr
import com.guet.flexbox.eventsystem.event.TemplateEvent
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlEngine

internal class DataBinding(
        private val parent: DataBinding? = null,
        private val binders: Map<String, DataBinder<*>>
) {

    private fun find(name: String): DataBinder<*>? {
        val v = binders[name]
        if (v != null) {
            return v
        }
        if (parent != null) {
            return parent.find(name)
        }
        return null
    }

    fun bind(
            engine: JexlEngine,
            dataContext: JexlContext,
            eventDispatcher: EventTarget,
            map: Map<String, String>
    ): Map<String, Any> {
        if (map.isEmpty()) {
            return emptyMap()
        }
        val output = ArrayMap<String, Any>(map.size)
        map.forEach {
            val binder = find(it.key)
            if (it.value.isNotEmpty()) {
                val o = binder?.cast(
                        engine,
                        dataContext,
                        eventDispatcher,
                        it.value
                )
                if (o != null) {
                    output[it.key] = o
                }
            }
        }
        return output
    }

    companion object {

        private val colorScope: Map<String, String> by lazy {
            @Suppress("UNCHECKED_CAST")
            (Color::class.java.getDeclaredField("sColorNameMap")
                    .apply { isAccessible = true }
                    .get(null) as Map<String, Int>).map {
                it.key to "#" + String.format("%08x", it.value)
            }.toMap()
        }

        val empty = DataBinding(null, emptyMap())

        inline fun create(
                parent: Definition? = null,
                crossinline action: Builder.() -> Unit
        ): Lazy<DataBinding> {
            return lazy {
                Builder().apply(action).build(parent?.dataBinding)
            }
        }
    }

    class Builder {

        private val value = ArrayMap<String, DataBinder<*>>()

        fun text(
                name: String,
                scope: (Map<String, String>) = emptyMap(),
                fallback: String = ""
        ) {
            value[name] = object : DataBinder<String> {
                override fun cast(
                        engine: JexlEngine,
                        dataContext: JexlContext,
                        eventDispatcher: EventTarget,
                        raw: String
                ): String? {
                    return if (raw.isExpr) {
                        try {
                            val expr = engine.createExpression(raw.innerExpr)
                            val o = expr.evaluate(ScopeContext(scope, dataContext))
                            o?.toString() ?: fallback
                        } catch (e: Throwable) {
                            raw
                        }
                    } else {
                        raw
                    }
                }
            }
        }

        fun bool(
                name: String,
                scope: Map<String, Boolean> = emptyMap(),
                fallback: Boolean = false
        ) {
            value[name] = object : DataBinder<Boolean> {
                override fun cast(
                        engine: JexlEngine,
                        dataContext: JexlContext,
                        eventDispatcher: EventTarget,
                        raw: String
                ): Boolean? {
                    return if (raw.isExpr) {
                        val expr = engine.createExpression(raw.innerExpr)
                        val o = expr.evaluate(ScopeContext(scope, dataContext))
                        o as? Boolean ?: fallback
                    } else {
                        raw.toBoolean()
                    }
                }
            }
        }

        fun value(
                name: String,
                scope: Map<String, Float> = emptyMap(),
                fallback: Float = 0f
        ) {
            value[name] = object : DataBinder<Float> {
                override fun cast(
                        engine: JexlEngine,
                        dataContext: JexlContext,
                        eventDispatcher: EventTarget,
                        raw: String
                ): Float? {
                    return if (raw.isExpr) {
                        val expr = engine.createExpression(raw.innerExpr)
                        val o = expr.evaluate(ScopeContext(scope, dataContext))
                        (o as? Number)?.toFloat() ?: fallback
                    } else {
                        raw.toFloatOrNull() ?: fallback
                    }
                }
            }
        }

        fun color(
                name: String,
                scope: Map<String, Int> = emptyMap(),
                fallback: Int = Color.TRANSPARENT
        ) {
            value[name] = object : DataBinder<Int> {
                override fun cast(
                        engine: JexlEngine,
                        dataContext: JexlContext,
                        eventDispatcher: EventTarget,
                        raw: String
                ): Int? {
                    return if (raw.isExpr) {
                        val expr = engine.createExpression(raw.innerExpr)
                        val o = expr.evaluate(ScopeContext(colorScope,
                                ScopeContext(scope, dataContext)
                        ))
                        try {
                            Color.parseColor((o as? String) ?: "")
                        } catch (e: Throwable) {
                            fallback
                        }
                    } else {
                        try {
                            Color.parseColor(raw)
                        } catch (e: Throwable) {
                            fallback
                        }
                    }
                }
            }
        }


        fun <T : Any> typed(
                name: String,
                obj: DataBinder<T>
        ) {
            value[name] = obj
        }

        inline fun <reified V : Enum<V>> enum(
                name: String,
                scope: Map<String, V>,
                fallback: V = enumValues<V>().first()
        ) {
            typed(name, object : DataBinder<V> {
                override fun cast(
                        engine: JexlEngine,
                        dataContext: JexlContext,
                        eventDispatcher: EventTarget,
                        raw: String
                ): V? {
                    return if (raw.isExpr) {
                        val expr = engine.createExpression(raw.innerExpr)
                        val o = expr.evaluate(ScopeContext(scope, dataContext))
                        o as? V ?: fallback
                    } else {
                        scope[raw] ?: fallback
                    }
                }
            })
        }

        fun event(
                name: String,
                factory: TemplateEvent.Factory
        ) {
            value[name] = object : DataBinder<ExternalEventReceiver> {
                override fun cast(
                        engine: JexlEngine,
                        dataContext: JexlContext,
                        eventDispatcher: EventTarget,
                        raw: String
                ): ExternalEventReceiver? {
                    return if (raw.isExpr) {
                        return ReceiveEventToExpr(
                                factory,
                                dataContext,
                                eventDispatcher,
                                engine.createScript(raw.innerExpr)
                        )
                    } else {
                        null
                    }
                }
            }
        }

        internal fun build(
                parent: DataBinding? = null
        ): DataBinding {
            return DataBinding(parent, value)
        }
    }
}