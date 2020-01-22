package com.guet.flexbox.litho

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.text.Layout.Alignment
import android.text.TextUtils
import android.util.ArrayMap
import android.widget.ImageView
import com.facebook.litho.Component
import com.facebook.litho.LithoHandler
import com.facebook.litho.drawable.ComparableGradientDrawable
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaJustify
import com.facebook.yoga.YogaWrap
import com.guet.flexbox.*
import java.util.*
import com.facebook.litho.widget.VerticalGravity as LithoVerticalGravity

internal typealias AttributeAssignSet<C> = Map<String, Assignment<C, *>>

private val pt = Resources.getSystem().displayMetrics.widthPixels / 360f

fun <T : Number> T.toPx(): Int {
    return (this.toDouble() * pt).toInt()
}

inline fun <T : Component.Builder<*>> create(crossinline action: Registry<T>.() -> Unit): Lazy<AttributeAssignSet<T>> {
    return lazy {
        Registry<T>().apply(action).value
    }
}

internal typealias ChildComponent = Component

class Registry<C : Component.Builder<*>> {

    private val _value = ArrayMap<String, Assignment<C, *>>()

    fun <T> register(
            name: String,
            assignment: Assignment<C, T>
    ) {
        _value[name] = assignment
    }

    val value: AttributeAssignSet<C>
        get() = _value
}

typealias Assignment<C, V> = C.(display: Boolean, other: Map<String, Any>, value: V) -> Unit

private val orientations: Map<String, GradientDrawable.Orientation> = mapOf(
        "t2b" to GradientDrawable.Orientation.TOP_BOTTOM,
        "tr2bl" to GradientDrawable.Orientation.TR_BL,
        "l2r" to GradientDrawable.Orientation.LEFT_RIGHT,
        "br2tl" to GradientDrawable.Orientation.BR_TL,
        "b2t" to GradientDrawable.Orientation.BOTTOM_TOP,
        "r2l" to GradientDrawable.Orientation.RIGHT_LEFT,
        "tl2br" to GradientDrawable.Orientation.TL_BR
)

private fun String.toOrientation(): GradientDrawable.Orientation {
    return orientations.getValue(this)
}

internal fun parseUrl(c: Context, url: CharSequence): Any? {
    when {
        TextUtils.isEmpty(url) -> {
            return null
        }
        url.startsWith("res://") -> {
            val uri = Uri.parse(url.toString())
            when (uri.host) {
                "gradient" -> {
                    val type = uri.getQueryParameter(
                            "orientation"
                    )?.toOrientation()
                    val colors = uri.getQueryParameters("color")?.map {
                        Color.parseColor(it)
                    }?.toIntArray()
                    return if (type != null && colors != null && colors.isNotEmpty()) {
                        ComparableGradientDrawable(type, colors)
                    } else {
                        null
                    }
                }
                "drawable" -> {
                    val name = uri.getQueryParameter("name")
                    if (name != null) {
                        val id = c.resources.getIdentifier(
                                name,
                                "drawable",
                                c.packageName
                        )
                        if (id != 0) {
                            return id
                        }
                    }
                    return null
                }
                else -> {
                    return null
                }
            }
        }
        else -> {
            return url
        }
    }
}

internal object LayoutThreadHandler : LithoHandler {

    override fun post(runnable: Runnable, tag: String?) {
        ConcurrentUtils.threadPool.execute(runnable)
    }

    override fun postAtFront(runnable: Runnable, tag: String?) {
        throw UnsupportedOperationException()
    }

    override fun isTracing(): Boolean = true

    override fun remove(runnable: Runnable) {
        ConcurrentUtils.threadPool.remove(runnable)
    }
}

private val mappings = ArrayMap<Class<*>, Map<*, Any>>()
        .apply {
            register<FlexAlign> {
                for (value in enumValues<FlexAlign>()) {
                    it[value] = YogaAlign.valueOf(value.name)
                }
            }
            register<FlexJustify> {
                for (value in enumValues<FlexJustify>()) {
                    it[value] = YogaJustify.valueOf(value.name)
                }
            }
            register<FlexWrap> {
                for (value in enumValues<FlexWrap>()) {
                    it[value] = YogaWrap.valueOf(value.name)
                }
            }
            register<HorizontalGravity> {
                it[HorizontalGravity.CENTER] = Alignment.ALIGN_CENTER
                it[HorizontalGravity.LEFT] = Alignment.valueOf("ALIGN_LEFT")
                it[HorizontalGravity.RIGHT] = Alignment.valueOf("ALIGN_RIGHT")
            }
            register<ScaleType> {
                for (value in enumValues<ScaleType>()) {
                    it[value] = ImageView.ScaleType.valueOf(value.name)
                }
            }
            register<TextStyle> {
                it[TextStyle.BOLD] = Typeface.BOLD
                it[TextStyle.NORMAL] = Typeface.NORMAL
            }
            register<VerticalGravity> {
                for (value in enumValues<VerticalGravity>()) {
                    it[value] = LithoVerticalGravity.valueOf(value.name)
                }
            }
        }

private inline fun <reified T : Enum<T>>
        ArrayMap<Class<*>, Map<*, Any>>.register(
        action: (EnumMap<T, Any>) -> Unit
) {
    val map = EnumMap<T, Any>(T::class.java)
    action(map)
    this[T::class.java] = map
}

internal inline fun <reified T> Enum<*>.mapValue(): T {
    return mappings.getValue(this.javaClass)[this] as T
}

