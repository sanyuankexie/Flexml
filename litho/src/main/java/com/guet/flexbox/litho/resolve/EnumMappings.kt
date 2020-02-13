package com.guet.flexbox.litho.resolve

import android.graphics.Typeface
import android.text.Layout
import android.util.ArrayMap
import android.widget.ImageView
import com.facebook.litho.widget.VerticalGravity
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaJustify
import com.facebook.yoga.YogaWrap
import com.guet.flexbox.enums.*
import java.util.*

internal object EnumMappings {

    private val value = ArrayMap<Class<*>, Map<*, Any>>()

    init {
        value.apply {
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
    }

    fun <T> get(enum: Enum<*>): T {
        @Suppress("UNCHECKED_CAST")
        return value.getValue(enum.javaClass)[enum] as T
    }

    private inline fun <reified T : Enum<T>>
            ArrayMap<Class<*>, Map<*, Any>>.registerEnumType(
            action: (EnumMap<T, Any>) -> Unit
    ) {
        val map = EnumMap<T, Any>(T::class.java)
        action(map)
        this[T::class.java] = map
    }
}

fun <T> Enum<*>.mapping(): T {
    return EnumMappings.get(this)
}