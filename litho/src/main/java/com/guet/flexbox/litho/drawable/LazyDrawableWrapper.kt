package com.guet.flexbox.litho.drawable

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import com.facebook.litho.drawable.ComparableDrawable
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

class LazyDrawableWrapper(
        private val lazyLoader: () -> Drawable
) : DrawableWrapper<Drawable>(NoOpDrawable()), ComparableDrawable {

    private val isInit = AtomicBoolean(false)

    override fun draw(canvas: Canvas) {
        if (isInit.compareAndSet(false, true)) {
            wrappedDrawable = lazyLoader()
        }
        super.draw(canvas)
    }

    override fun isEquivalentTo(other: ComparableDrawable?): Boolean {
        if (this === other) {
            return true
        } else if (other is LazyDrawableWrapper) {
            if (lazyLoader === other.lazyLoader) {
                return true
            } else if (lazyLoader.javaClass == other.lazyLoader.javaClass) {
                val clazz = lazyLoader.javaClass
                val fields = fieldsMap.getOrPut(clazz) {
                    val fields = clazz.declaredFields
                    fields.forEach {
                        it.isAccessible = true
                    }
                    fields
                }
                val set1 = fields.map {
                    it.get(lazyLoader)
                }
                val set2 = fields.map {
                    it.get(other.lazyLoader)
                }
                return set1.containsAll(set2)
            }
        }
        return false
    }

    private companion object {
        private val fieldsMap = ConcurrentHashMap<Class<*>, Array<Field>>()
    }
}

fun lazyDrawable(lazyLoader: () -> Drawable): Drawable {
    return LazyDrawableWrapper(lazyLoader)
}