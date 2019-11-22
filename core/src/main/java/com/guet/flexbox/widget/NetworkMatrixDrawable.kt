package com.guet.flexbox.widget

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView.ScaleType
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.facebook.litho.DrawableMatrix
import com.facebook.litho.Touchable
import com.guet.flexbox.build.toOrientation

internal class NetworkMatrixDrawable(
        private val c: Context,
        target: (Target<Drawable>) = DelegateTarget()
) : BorderDrawable<MatrixDrawable>(MatrixDrawable()), Touchable, Target<Drawable> by target {

    private var width: Int = 0
    private var height: Int = 0
    private var scaleType = ScaleType.FIT_CENTER

    fun mount(
            url: CharSequence,
            width: Int,
            height: Int,
            radius: Int,
            borderWidth: Int,
            borderColor: Int,
            blurRadius: Float,
            blurSampling: Float,
            scaleType: ScaleType
    ) {
        this.width = width
        this.height = height
        this.radius = radius
        this.borderWidth = borderWidth
        this.borderColor = borderColor
        this.scaleType = scaleType
        when {
            TextUtils.isEmpty(url) -> {
                setToEmpty()
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
                        if (type != null && colors != null && colors.isNotEmpty()) {
                            notifyChanged(scaleType, GradientDrawable(type, colors))
                        } else {
                            setToEmpty()
                        }
                    }
                    "load" -> {
                        val name = uri.getQueryParameter("name")
                        if (name != null) {
                            val id = c.resources.getIdentifier(
                                    name,
                                    "drawable",
                                    c.packageName
                            )
                            if (id != 0) {
                                loadModel(id, blurRadius, blurSampling)
                            } else {
                                setToEmpty()
                            }
                        }
                    }
                    else -> {
                        setToEmpty()
                    }
                }
            }
            else -> {
                loadModel(url, blurRadius, blurSampling)
            }
        }
    }

    private fun setToEmpty() {
        wrappedDrawable.mount(NoOpDrawable(), null, 0, 0)
    }

    private fun loadModel(
            model: Any,
            blurRadius: Float,
            blurSampling: Float
    ) {
        Glide.with(c).load(model).apply {
            if (blurRadius > 0) {
                transform(BlurTransformation(
                        blurRadius,
                        blurSampling
                ))
            }
        }.into(this)
    }

    override fun getSize(cb: SizeReadyCallback) {
        cb.onSizeReady(width, height)
    }

    override fun onLoadCleared(placeholder: Drawable?) {
        if (placeholder != null) {
            onResourceReady(placeholder, null)
        } else {
            wrappedDrawable.unmount()
        }
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        wrappedDrawable.unmount()
    }

    override fun onResourceReady(
            resource: Drawable,
            transition: Transition<in Drawable>?
    ) {
        notifyChanged(scaleType, resource)
    }

    fun unmount() {
        wrappedDrawable.unmount()
        Glide.with(c).clear(this)
    }

    @TargetApi(LOLLIPOP)
    override fun onTouchEvent(event: MotionEvent, host: View): Boolean {
        return wrappedDrawable.onTouchEvent(event, host)
    }

    override fun shouldHandleTouchEvent(event: MotionEvent): Boolean {
        return wrappedDrawable.shouldHandleTouchEvent(event)
    }

    private fun notifyChanged(
            scaleType: ScaleType,
            resource: Drawable
    ) {
        val drawableWidth: Int
        val drawableHeight: Int
        val matrix: DrawableMatrix?
        if (ScaleType.FIT_XY == scaleType
                || resource.intrinsicWidth <= 0
                || resource.intrinsicHeight <= 0) {
            matrix = null
            drawableWidth = width
            drawableHeight = height
        } else {
            matrix = DrawableMatrix.create(
                    resource,
                    scaleType,
                    width,
                    height
            )
            drawableWidth = resource.intrinsicWidth
            drawableHeight = resource.intrinsicHeight
        }
        wrappedDrawable.mount(
                DelegateTarget.transition(wrappedDrawable.mountedDrawable, resource),
                matrix,
                drawableWidth,
                drawableHeight
        )
    }
}