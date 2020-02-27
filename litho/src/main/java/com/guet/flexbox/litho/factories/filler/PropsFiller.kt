package com.guet.flexbox.litho.factories.filler

import android.graphics.Typeface
import android.util.ArrayMap
import com.facebook.litho.Component
import com.facebook.litho.EventHandler
import com.facebook.yoga.YogaEdge
import com.guet.flexbox.enums.TextStyle
import com.guet.flexbox.eventsystem.ExternalEventReceiver
import com.guet.flexbox.litho.LithoEventHandler
import com.guet.flexbox.litho.factories.ToComponent
import com.guet.flexbox.litho.toPx
import java.util.*

internal class PropsFiller<C : Component.Builder<*>>(
        private val parent: PropsFiller<in C>? = null,
        private val map: Map<String, PropFiller<*, *>>
) {

    @Suppress("UNCHECKED_CAST")
    private fun find(name: String): PropFiller<in C, Any>? {
        val assignment = map[name]
        return if (assignment == null) {
            parent?.find(name)
        } else {
            assignment as? PropFiller<in C, Any>
        }
    }

    fun fill(
            c: C,
            display: Boolean,
            attrs: Map<String, Any>
    ) {
        for ((name, raw) in attrs) {
            find(name)?.fill(c, display, attrs, raw)
        }
    }

    internal class Builder<C : Component.Builder<*>> {

        private val value = ArrayMap<String, PropFiller<C, *>>()

        fun <T> register(
                name: String,
                propFiller: PropFiller<C, T>
        ) {
            value[name] = propFiller
        }

        @JvmName("pt\$float")
        inline fun pt(name: String, crossinline method: (C, Float) -> C) {
            register(name, object : PropFiller<C, Float> {
                override fun fill(c: C,
                                  display: Boolean,
                                  other: Map<String, Any>,
                                  value: Float
                ) {
                    method(c, value.toPx().toFloat())
                }
            })
        }


        inline fun pt(name: String, crossinline method: (C, Int) -> C) {
            register(name, object : PropFiller<C, Float> {
                override fun fill(c: C,
                                  display: Boolean,
                                  other: Map<String, Any>,
                                  value: Float
                ) {
                    method(c, value.toPx())
                }
            })
        }

        inline fun textStyle(name: String, crossinline method: (C, Typeface) -> C) {
            register(name, object : PropFiller<C, TextStyle> {
                override fun fill(
                        c: C,
                        display: Boolean,
                        other: Map<String, Any>,
                        value: TextStyle
                ) {
                    method(c, Typeface.defaultFromStyle(EnumMappings.get(value)))
                }
            })
        }

        inline fun edges(
                prefix: String,
                crossinline method: (C, YogaEdge, Int) -> C
        ) {
            register(prefix, object : PropFiller<C, Float> {
                override fun fill(
                        c: C,
                        display: Boolean,
                        other: Map<String, Any>,
                        value: Float
                ) {
                    method(c, YogaEdge.ALL, value.toPx())
                }
            })
            for (suffix in arrayOf("Left", "Right", "Top", "Bottom")) {
                val name = prefix + suffix
                val edge = YogaEdge.valueOf(suffix.toUpperCase(Locale.getDefault()))
                register(name, object : PropFiller<C, Float> {
                    override fun fill(
                            c: C,
                            display: Boolean,
                            other: Map<String, Any>,
                            value: Float
                    ) {
                        method(c, edge, value.toPx())
                    }
                })
            }
        }

        @JvmName("value\$float")
        inline fun value(name: String, crossinline method: (C, Float) -> C) {
            register(name, object : PropFiller<C, Float> {
                override fun fill(c: C, display: Boolean, other: Map<String, Any>, value: Float) {
                    method(c, value)
                }
            })
        }

        @JvmName("value\$int")
        inline fun value(name: String, crossinline method: (C, Int) -> C) {
            register(name, object : PropFiller<C, Float> {
                override fun fill(c: C, display: Boolean, other: Map<String, Any>, value: Float) {
                    method(c, value.toInt())
                }
            })
        }

        @JvmName("value\$long")
        inline fun value(name: String, crossinline method: (C, Long) -> C) {
            register(name, object : PropFiller<C, Float> {
                override fun fill(c: C, display: Boolean, other: Map<String, Any>, value: Float) {
                    method(c, value.toLong())
                }
            })
        }

        @JvmName("value\$short")
        inline fun <reified T : Number> value(name: String, crossinline method: (C, Short) -> C) {
            register(name, object : PropFiller<C, Float> {
                override fun fill(c: C, display: Boolean, other: Map<String, Any>, value: Float) {
                    method(c, value.toShort())
                }
            })
        }

        @JvmName("value\$double")
        inline fun <reified T : Number> value(name: String, crossinline method: (C, Double) -> C) {
            register(name, object : PropFiller<C, Float> {
                override fun fill(c: C, display: Boolean, other: Map<String, Any>, value: Float) {
                    method(c, value.toDouble())
                }
            })
        }

        inline fun <reified T : Enum<*>> enum(name: String, crossinline method: (C, T) -> C) {
            register(name, object : PropFiller<C, Enum<*>> {
                override fun fill(c: C, display: Boolean, other: Map<String, Any>, value: Enum<*>) {
                    method(c, if (T::class.java == value.javaClass) {
                        value as T
                    } else {
                        EnumMappings.get(value)
                    })
                }
            })
        }

        inline fun <reified T : EventHandler<*>> event(name: String, crossinline method: (C, T) -> C) {
            register(name, object : PropFiller<C, ExternalEventReceiver> {
                override fun fill(c: C, display: Boolean, other: Map<String, Any>, value: ExternalEventReceiver) {
                    method(c, LithoEventHandler<Any>(value) as T)
                }
            })
        }

        inline fun bool(name: String, crossinline method: (C, Boolean) -> C) {
            register(name, object : PropFiller<C, Boolean> {
                override fun fill(c: C, display: Boolean, other: Map<String, Any>, value: Boolean) {
                    method(c, value)
                }
            })
        }

        inline fun text(name: String, crossinline method: (C, String) -> C) {
            register(name, object : PropFiller<C, String> {
                override fun fill(c: C, display: Boolean, other: Map<String, Any>, value: String) {
                    method(c, value)
                }
            })
        }

        inline fun color(name: String, crossinline method: (C, Int) -> C) {
            register(name, object : PropFiller<C, Int> {
                override fun fill(c: C, display: Boolean, other: Map<String, Any>, value: Int) {
                    method(c, value)
                }
            })
        }

        fun build(parent: PropsFiller<in C>? = null): PropsFiller<C> {
            return PropsFiller(parent, value)
        }
    }

    companion object {

        internal inline fun <T : Component.Builder<*>> create(
                parent: ToComponent<in T>? = null,
                crossinline action: Builder<T>.() -> Unit
        ): Lazy<PropsFiller<T>> {
            return lazy {
                Builder<T>().apply(action).build(parent?.propsFiller)
            }
        }

        fun <T : Component.Builder<*>> use(parent: ToComponent<in T>): PropsFiller<T> {
            return PropsFiller(parent.propsFiller, emptyMap())
        }
    }
}