package com.guet.flexbox.build

import android.widget.ImageView.ScaleType.*
import com.facebook.litho.widget.Image
import org.dom4j.Attribute

internal object ImageFactory : Factory<Image.Builder>() {

    init {
        text("source") {

        }
        bound("scaleType", FIT_CENTER,
                mapOf(
                        "center" to CENTER,
                        "fitCenter" to FIT_CENTER,
                        "fitXY" to FIT_XY,
                        "fitStart" to FIT_START,
                        "fitEnd" to FIT_END,
                        "centerInside" to CENTER_INSIDE
                )
        ) {
            scaleType(it)
        }
    }

    override fun create(
            c: BuildContext,
            attrs: List<Attribute>): Image.Builder {
        return Image.create(c.componentContext).apply {
            applyDefault(c, attrs)
        }
    }
}
