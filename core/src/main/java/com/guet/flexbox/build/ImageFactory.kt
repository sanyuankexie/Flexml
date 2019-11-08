package com.guet.flexbox.build

import android.widget.ImageView.ScaleType.*
import com.guet.flexbox.widget.NetworkImage

internal object ImageFactory : WidgetFactory<NetworkImage.Builder>() {

    init {
        textAttr("url") { display, it ->
            if (display) {
                url(it)
            } else {
                url("")
            }
        }
        enumAttr("scaleType",
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
        colorAttr("borderColor") { _, it ->
            borderColor(it)
        }
        numberAttr<Int>("borderRadius") { _, it ->
            borderRadius(it.toPx())
        }
        numberAttr<Int>("borderWidth") { _, it ->
            borderWidth(it.toPx())
        }
        numberAttr<Int>("blurRadius") { _, it ->
            blurRadius(it)
        }
        numberAttr("blurSampling", 1) { _, it ->
            blurSampling(it)
        }
    }

    override fun onCreateWidget(
            c: BuildContext,
            attrs: Map<String, String>?,
            visibility: Int
    ): NetworkImage.Builder {
        return NetworkImage.create(c.componentContext)
    }
}
