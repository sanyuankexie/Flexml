package com.guet.flexbox.litho.drawable

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.ArrayMap
import com.facebook.litho.drawable.ComparableDrawable
import java.lang.reflect.Field
import java.util.concurrent.atomic.AtomicBoolean

internal abstract class LazyDrawableLoader
    : DrawableWrapper(), ComparableDrawable {

    private val isInit = AtomicBoolean(false)

    override fun draw(canvas: Canvas) {
        if (isInit.compareAndSet(false, true)) {
            wrappedDrawable = loadDrawable()
        }
        super.draw(canvas)
    }

    protected abstract fun loadDrawable(): Drawable

    override fun isEquivalentTo(other: ComparableDrawable?): Boolean {
        if (this === other) {
            return true
        } else if (other?.javaClass == javaClass) {
            val set1 = getFieldObjects(this)
            val set2 = getFieldObjects(other)
            return set1.contentDeepEquals(set2)
        }
        return false
    }

    companion object {

        private val cache = ArrayMap<Class<*>, Array<Field>>()

        private fun getFieldObjects(o: Any): Array<Any?> {
            val clazz = o.javaClass
            return synchronized(cache) {
                cache.getOrPut(clazz) {
                    val fields = clazz.declaredFields
                    fields.forEach {
                        it.isAccessible = true
                    }
                    fields
                }
            }.map {
                it.get(o)
            }.toTypedArray()
        }

        inline fun from(crossinline onLoad: () -> Drawable): Drawable {
            return object : LazyDrawableLoader() {
                override fun loadDrawable(): Drawable = onLoad()
            }
        }
    }
}

