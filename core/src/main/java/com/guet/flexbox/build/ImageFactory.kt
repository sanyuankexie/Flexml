package com.guet.flexbox.build

import android.widget.ImageView.ScaleType.*
import com.guet.flexbox.widget.NetworkImage

internal object ImageFactory : WidgetFactory<NetworkImage.Builder>() {

    init {
        text("url") { display, it ->
            if (display) {
                url(it)
            } else {
                url("")
            }
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
        ) { _, it ->
            scaleType(it)
        }
        color("borderColor") { _, it ->
            borderColor(it)
        }
        value("borderRadius") { _, it ->
            borderRadius(it.toPx())
        }
        value("borderWidth") { _, it ->
            borderWidth(it.toPx())
        }
        value("blurRadius") { _, it ->
            blurRadius(it.toInt())
        }
        value("blurSampling", 1.0) { _, it ->
            blurSampling(it.toInt())
        }
    }

    override fun create(
            c: BuildContext,
            attrs: Map<String, String>
    ): NetworkImage.Builder {
        return NetworkImage.create(c.componentContext)
    }
}
