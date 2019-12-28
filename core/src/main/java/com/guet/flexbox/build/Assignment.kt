package com.guet.flexbox.build

import android.content.res.Resources
import com.facebook.litho.Component

internal typealias AttributeSet<C> = Map<String, Assignment<C, *>>

private val metrics = Resources.getSystem().displayMetrics

internal inline fun <reified T : Number> T.toPx(): Int {
    return (this.toFloat() * metrics.widthPixels / 360f).toInt()
}

internal inline fun <T : Component.Builder<*>> create(crossinline action: HashMap<String, Assignment<T, *>>.() -> Unit): Lazy<AttributeSet<T>> {
    return lazy {
        HashMap<String, Assignment<T, *>>().apply(action)
    }
}

internal abstract class Assignment<C : Component.Builder<*>, V : Any> {

    abstract fun C.assign(display: Boolean, other: Map<String, Any>, value: V)

    operator fun invoke(c: C, display: Boolean, other: Map<String, Any>, value: V) {
        c.assign(display, other, value)
    }
}