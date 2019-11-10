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
        var backgroundDrawable: Drawable? = null
        val background = attrs["background"]
        if (background != null) {
            try {
                backgroundDrawable = ColorDrawable(c.getColor(background))
            } catch (e: Exception) {
                val backgroundELResult = c.scope(orientations) {
                    c.scope(colorNameMap) {
                        c.tryGetValue<Any>(background, Unit)
                    }
                }
                if (backgroundELResult is Drawable) {
                    backgroundDrawable = backgroundELResult
                } else if (backgroundELResult is CharSequence && backgroundELResult.isNotEmpty()) {
                    var width = c.tryGetValue(attrs["width"], Target.SIZE_ORIGINAL)
                    if (width <= 0) {
                        width = Target.SIZE_ORIGINAL
                    }
                    var height = c.tryGetValue(attrs["height"], Target.SIZE_ORIGINAL)
                    if (height <= 0) {
                        height = Target.SIZE_ORIGINAL
                    }
                    backgroundDrawable = NetworkDrawable(
                            width.toPx(),
                            height.toPx(),
                            c.componentContext.androidContext,
                            backgroundELResult
                    )
                }
            }
        }
        if (backgroundDrawable == null) {
            backgroundDrawable = NoOpDrawable()
        }
        @Suppress("DEPRECATION")
        this.background(BorderDrawable(
                backgroundDrawable,
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