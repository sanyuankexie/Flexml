package com.guet.flexbox.litho.drawable

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.facebook.litho.drawable.ComparableDrawable
import java.util.concurrent.atomic.AtomicBoolean

class LazyImageDrawable(
        private val context: Context,
        private val model: Any,
        private val radiusArray: FloatArray = emptyArray
) : DrawableWrapper<Drawable>(NoOpDrawable()),
        Target<Drawable> by DelegateTarget(),
        ComparableDrawable {

    private companion object {
        private val emptyArray = FloatArray(0)
    }

    private val isInit = AtomicBoolean(false)

    override fun isEquivalentTo(other: ComparableDrawable?): Boolean {
        return other is LazyImageDrawable
                && model == other.model
                && radiusArray.contentEquals(other.radiusArray)
    }

    override fun draw(canvas: Canvas) {
        if (isInit.compareAndSet(false, true)) {
            var request = Glide.with(context)
                    .load(model)
                    .override(bounds.width(), bounds.height())
            if (radiusArray.size == 4) {
                request = request.transform(
                        GranularRoundedCorners(
                                radiusArray[0],
                                radiusArray[1],
                                radiusArray[2],
                                radiusArray[3]
                        )
                )
            } else if (radiusArray.size == 1) {
                request = request.transform(
                        RoundedCorners(radiusArray[0].toInt())
                )
            }
            request.into(this)
        } else {
            super.draw(canvas)
        }
    }

    override fun onResourceReady(
            resource: Drawable,
            transition: Transition<in Drawable>?) {
        resource.bounds = bounds
        wrappedDrawable = resource
        invalidateSelf()
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        isInit.set(false)
        wrappedDrawable = NoOpDrawable()
        invalidateSelf()
    }
}