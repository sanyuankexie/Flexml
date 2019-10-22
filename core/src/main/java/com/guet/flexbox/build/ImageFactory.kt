package com.guet.flexbox.build

import android.widget.ImageView.ScaleType.*
import com.guet.flexbox.widget.AsyncImage
import org.dom4j.Attribute

internal object ImageFactory : WidgetFactory<AsyncImage.Builder>() {

    init {
        text("source") {
            url(it)
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
        value("borderRadius") {
            borderRadius(it.toPx().toFloat())
        }
        value("borderWidth") {
            borderWidth(it.toPx().toFloat())
        }
        color("borderColor") {
            borderColor(it)
        }
    }

    override fun create(
            c: BuildContext,
            attrs: List<Attribute>): AsyncImage.Builder {
        return AsyncImage.create(c.componentContext).apply {
            applyDefault(c, attrs)
        }
    }
}
