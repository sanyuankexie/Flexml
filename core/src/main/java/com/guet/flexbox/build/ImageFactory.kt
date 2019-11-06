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
        enumAttr("scaleType", FIT_CENTER,
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
        numberAttr("borderRadius") { _, it ->
            borderRadius(it.toPx())
        }
        numberAttr("borderWidth") { _, it ->
            borderWidth(it.toPx())
        }
        numberAttr("blurRadius") { _, it ->
            blurRadius(it.toInt())
        }
        numberAttr("blurSampling", 1.0) { _, it ->
            blurSampling(it.toInt())
        }
    }

    override fun onCreate(
            c: BuildContext,
            attrs: Map<String, String>?,
            visibility: Int
    ): NetworkImage.Builder {
        return NetworkImage.create(c.componentContext)
    }
}
