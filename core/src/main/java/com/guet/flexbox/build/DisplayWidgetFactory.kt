package com.guet.flexbox.build

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import com.bumptech.glide.request.target.Target
import com.facebook.litho.Component
import com.guet.flexbox.widget.BorderDrawable
import com.guet.flexbox.widget.NetworkDrawable
import com.guet.flexbox.widget.NoOpDrawable

internal abstract class DisplayWidgetFactory<T : Component.Builder<*>> : WidgetFactory<T>() {

    private fun T.applyBackground(
            c: BuildContext,
            attrs: Map<String, String>) {
        val borderRadius = c.tryGetValue(attrs["borderRadius"], 0).toPx()
        val borderWidth = c.tryGetValue(attrs["borderWidth"], 0).toPx()
        val borderColor = c.tryGetColor(attrs["borderColor"], Color.TRANSPARENT)
        var model: Drawable? = null
        val backgroundValue = attrs["background"]
        if (backgroundValue != null) {
            try {
                model = ColorDrawable(c.getColor(backgroundValue))
            } catch (e: Exception) {
                val backgroundRaw = c.scope(orientations) {
                    c.scope(colorNameMap) {
                        c.tryGetValue<Any>(backgroundValue, Unit)
                    }
                }
                if (backgroundRaw is Drawable) {
                    model = backgroundRaw
                } else if (backgroundRaw is CharSequence && backgroundRaw.isNotEmpty()) {
                    var width = c.tryGetValue(attrs["width"], Target.SIZE_ORIGINAL)
                    if (width <= 0) {
                        width = Target.SIZE_ORIGINAL
                    }
                    var height = c.tryGetValue(attrs["height"], Target.SIZE_ORIGINAL)
                    if (height <= 0) {
                        height = Target.SIZE_ORIGINAL
                    }
                    model = NetworkDrawable(
                            width.toPx(),
                            height.toPx(),
                            c.componentContext.androidContext,
                            backgroundRaw
                    )
                }
            }
        }
        if (model == null) {
            model = NoOpDrawable()
        }
        @Suppress("DEPRECATION")
        this.background(BorderDrawable(
                model,
                borderRadius,
                borderWidth,
                borderColor
        ))
    }

    override fun loadStyles(
            owner: T,
            c: BuildContext,
            attrs: Map<String, String>?,
            visibility: Int) {
        if (!attrs.isNullOrEmpty() && visibility != View.INVISIBLE) {
            owner.applyBackground(c, attrs)
        }
        super.loadStyles(owner, c, attrs, visibility)
    }
}