package com.guet.flexbox.litho.factories

import android.graphics.Typeface
import android.text.Layout
import android.util.ArrayMap
import android.widget.ImageView
import com.facebook.litho.Component
import com.facebook.litho.widget.VerticalGravity
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaJustify
import com.facebook.yoga.YogaWrap
import com.guet.flexbox.build.AttributeSet
import com.guet.flexbox.enums.*
import java.util.*

private val enumsMapping = ArrayMap<Class<*>, Map<*, Any>>()
        .apply {
            registerEnumType<FlexAlign> {
                for (value in enumValues<FlexAlign>()) {
                    it[value] = YogaAlign.valueOf(value.name)
                }
            }
            registerEnumType<FlexJustify> {
                for (value in enumValues<FlexJustify>()) {
                    it[value] = YogaJustify.valueOf(value.name)
                }
            }
            registerEnumType<FlexWrap> {
                for (value in enumValues<FlexWrap>()) {
                    it[value] = YogaWrap.valueOf(value.name)
                }
            }
            registerEnumType<Horizontal> {
                it[Horizontal.CENTER] = Layout.Alignment.ALIGN_CENTER
                it[Horizontal.LEFT] = Layout.Alignment.valueOf("ALIGN_LEFT")
                it[Horizontal.RIGHT] = Layout.Alignment.valueOf("ALIGN_RIGHT")
            }
            registerEnumType<ScaleType> {
                for (value in enumValues<ScaleType>()) {
                    it[value] = ImageView.ScaleType.valueOf(value.name)
                }
            }
            registerEnumType<TextStyle> {
                it[TextStyle.BOLD] = Typeface.BOLD
                it[TextStyle.NORMAL] = Typeface.NORMAL
            }
            registerEnumType<Vertical> {
                for (value in enumValues<Vertical>()) {
                    it[value] = VerticalGravity.valueOf(value.name)
                }
            }
        }

private inline fun <reified T : Enum<T>>
        ArrayMap<Class<*>, Map<*, Any>>.registerEnumType(
        action: (EnumMap<T, Any>) -> Unit
) {
    val map = EnumMap<T, Any>(T::class.java)
    action(map)
    this[T::class.java] = map
}

internal inline fun <reified T> Enum<*>.mapping(): T {
    return enumsMapping.getValue(this.javaClass)[this] as T
}

internal typealias Assignment<C, V> = C.(display: Boolean, other: Map<String, Any>, value: V) -> Unit

internal typealias AttributeAssignSet<C> = Map<String, Assignment<C, *>>

internal inline fun <T : Component.Builder<*>> create(
        crossinline action: AttrsAssignRegistry<T>.() -> Unit
): Lazy<AttributeAssignSet<T>> {
    return lazy {
        AttrsAssignRegistry<T>().apply(action).value
    }
}

internal fun AttributeSet.getFloatValue(name: String): Float {
    return (this[name] as? Double)?.toFloat() ?: 0f
}