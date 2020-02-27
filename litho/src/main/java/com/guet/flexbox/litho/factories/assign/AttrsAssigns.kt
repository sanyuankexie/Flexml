package com.guet.flexbox.litho.factories.assign

import android.util.ArrayMap
import com.facebook.litho.Component
import com.facebook.litho.EventHandler
import com.guet.flexbox.eventsystem.EventAdapter
import com.guet.flexbox.litho.LithoEventAdapter
import com.guet.flexbox.litho.toPx

class AttrsAssigns<C : Component.Builder<*>>(
        private val parent: AttrsAssigns<in C>? = null,
        private val map: Map<String, Assignment<*, *>>
) {

    @Suppress("UNCHECKED_CAST")
    private fun find(name: String): Assignment<in C, Any>? {
        val assignment = map[name]
        return if (assignment == null) {
            parent?.find(name)
        } else {
            assignment as? Assignment<in C, Any>
        }
    }

    fun assign(
            c: C,
            display: Boolean,
            attrs: Map<String, Any>
    ) {
        for ((name, raw) in attrs) {
            find(name)?.assign(c, display, attrs, raw)
        }
    }

    internal class Builder<C : Component.Builder<*>> {

        private val value = ArrayMap<String, Assignment<C, *>>()

        fun <T> register(
                name: String,
                assignment: Assignment<C, T>
        ) {
            value[name] = assignment
        }

        inline fun <reified T : Number> pt(name: String, crossinline method: (C, T) -> C) {
            register(name, object : Assignment<C, Float> {
                override fun assign(c: C,
                                    display: Boolean,
                                    other: Map<String, Any>,
                                    value: Float
                ) {
                    method(c, value.toPx().safeCast())
                }
            })
        }

        inline fun <reified T : Number> value(name: String, crossinline method: (C, T) -> C) {
            register(name, object : Assignment<C, Float> {
                override fun assign(c: C, display: Boolean, other: Map<String, Any>, value: Float) {
                    method(c, value.safeCast())
                }
            })
        }

        inline fun <reified T : Enum<*>> enum(name: String, crossinline method: (C, T) -> C) {
            register(name, object : Assignment<C, Enum<*>> {
                override fun assign(c: C, display: Boolean, other: Map<String, Any>, value: Enum<*>) {
                    method(c, if (T::class.java == value.javaClass) {
                        value as T
                    } else {
                        EnumMappings.get(value)
                    })
                }
            })
        }

        inline fun <reified T : EventHandler<*>> event(name: String, crossinline method: (C, T) -> C) {
            register(name, object : Assignment<C, EventAdapter> {
                override fun assign(c: C, display: Boolean, other: Map<String, Any>, value: EventAdapter) {
                    method(c, LithoEventAdapter<Any>(value) as T)
                }
            })
        }

        inline fun bool(name: String, crossinline method: (C, Boolean) -> C) {
            register(name, object : Assignment<C, Boolean> {
                override fun assign(c: C, display: Boolean, other: Map<String, Any>, value: Boolean) {
                    method(c, value)
                }
            })
        }

        inline fun color(name: String, crossinline method: (C, Int) -> C) {
            register(name, object : Assignment<C, Int> {
                override fun assign(c: C, display: Boolean, other: Map<String, Any>, value: Int) {
                    method(c, value)
                }
            })
        }

        fun build(parent: AttrsAssigns<in C>? = null): AttrsAssigns<C> {
            return AttrsAssigns(parent, value)
        }
    }

    companion object {

        private inline fun <reified T : Number> Number.safeCast(): T {
            return when (T::class.javaPrimitiveType) {
                Double::class.javaPrimitiveType -> toDouble() as T
                Float::class.javaPrimitiveType -> toFloat() as T
                Long::class.javaPrimitiveType -> toLong() as T
                Int::class.javaPrimitiveType -> toInt() as T
                Char::class.javaPrimitiveType -> toChar() as T
                Short::class.javaPrimitiveType -> toShort() as T
                Byte::class.javaPrimitiveType -> toByte() as T
                else -> throw AssertionError("by ${T::class.java}")
            }
        }

        internal inline fun <T : Component.Builder<*>> create(
                parent: AttrsAssigns<in T>? = null,
                crossinline action: Builder<T>.() -> Unit
        ): Lazy<AttrsAssigns<T>> {
            return lazy {
                Builder<T>().apply(action).build(parent)
            }
        }

        fun <T : Component.Builder<*>> use(parent: AttrsAssigns<in T>): AttrsAssigns<T> {
            return AttrsAssigns(parent, emptyMap())
        }
    }
}