package com.guet.flexbox.build

import android.widget.ImageView.ScaleType.*
import com.guet.flexbox.widget.NetworkImage

internal object ImageFactory : WidgetFactory<NetworkImage.Builder>() {

    init {
        text("url") {
            url(it)
        }
        bound("scaleType", FIT_CENTER,
                mapOf(
                        "center" to CENTER,
                        "fitCenter" to FIT_CENTER,
                        "fitXY" to FIT_XY,
                        "fitStart" to FIT_START,
                        "fitEnd" to FIT_END,
                        "centerInside" to CENTER_INSIDE,
                        "centerCrop" to CENTER_CROP
                )
        ) {
            scaleType(it)
        }
        color("borderColor") {
            borderColor(it)
        }
        value("borderRadius") {
            borderRadius(it.toPx())
        }
        value("borderWidth") {
            borderWidth(it.toPx())
        }
        value("blurRadius") {
            blurRadius(it.toInt())
        }
        value("blurSampling", 1.0) {
            blurSampling(it.toInt())
        }
    }

    override fun create(
            c: BuildContext,
            attrs: Map<String, String>): NetworkImage.Builder {
        return NetworkImage.create(c.componentContext).apply {
            applyDefault(c, attrs)
        }
    }
}
