package com.guet.flexbox.widget

import android.graphics.*
import android.graphics.drawable.Drawable

internal open class RoundedDrawable<T : Drawable>(
        drawable: T,
        var radius: Int = 0
) : Drawable(), Drawable.Callback {

    private val paint = Paint().apply { isAntiAlias = true }
    private val mode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    private val rectF = RectF()

    protected var inner: T = drawable.apply { callback = this@RoundedDrawable }
        set(value) {
            field.callback = null
            field = value
            field.callback = this
        }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        inner.setBounds(0, 0, bounds.width(), bounds.height())
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)
        inner.setBounds(0, 0, bounds.width(), bounds.height())
    }

    override fun draw(canvas: Canvas) {
        SharedCanvas.draw(inner) {
            rectF.set(bounds)
            val sc = canvas.saveLayer(rectF, null)
            paint.xfermode = null
            canvas.drawRoundRect(rectF, radius.toFloat(), radius.toFloat(), paint)
            paint.xfermode = mode
            canvas.drawBitmap(it, null, rectF, paint)
            canvas.restoreToCount(sc)
        }
    }

    override fun setChangingConfigurations(configs: Int) {
        inner.changingConfigurations = configs
    }

    override fun getChangingConfigurations(): Int {
        return inner.changingConfigurations
    }

    override fun setDither(dither: Boolean) {
        @Suppress("DEPRECATION")
        inner.setDither(dither)
    }

    override fun setFilterBitmap(filter: Boolean) {
        inner.isFilterBitmap = filter
    }

    override fun setAlpha(alpha: Int) {
        inner.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        inner.colorFilter = cf
    }

    override fun isStateful(): Boolean {
        return inner.isStateful
    }

    override fun setState(stateSet: IntArray): Boolean {
        return inner.setState(stateSet)
    }

    override fun getState(): IntArray? {
        return inner.state ?: UNSET
    }

    override fun getCurrent(): Drawable? {
        return inner.current
    }

    override fun setVisible(visible: Boolean, restart: Boolean): Boolean {
        return inner.setVisible(visible, restart)
    }

    override fun getOpacity(): Int {
        return inner.opacity
    }

    override fun getTransparentRegion(): Region? {
        return inner.transparentRegion
    }

    override fun getIntrinsicWidth(): Int {
        return inner.intrinsicWidth
    }

    override fun getIntrinsicHeight(): Int {
        return inner.intrinsicHeight
    }

    override fun getMinimumWidth(): Int {
        return inner.minimumWidth
    }

    override fun getMinimumHeight(): Int {
        return inner.minimumHeight
    }

    override fun getPadding(padding: Rect): Boolean {
        return inner.getPadding(padding)
    }

    override fun onLevelChange(level: Int): Boolean {
        return inner.setLevel(level)
    }

    override fun invalidateDrawable(who: Drawable) {
        invalidateSelf()
    }

    override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
        scheduleSelf(what, `when`)
    }

    override fun unscheduleDrawable(who: Drawable, what: Runnable) {
        unscheduleSelf(what)
    }

    private companion object {
        private val UNSET = IntArray(0)
    }
}