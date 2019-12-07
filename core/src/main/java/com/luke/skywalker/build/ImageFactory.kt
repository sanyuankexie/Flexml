package com.luke.skywalker.build

import android.view.View
import android.widget.ImageView
import com.facebook.litho.ComponentContext
import com.luke.skywalker.el.PropsELContext
import com.luke.skywalker.widget.AsyncImage

internal object ImageFactory : WidgetFactory<AsyncImage.Builder>(
        {
            enumAttr("scaleType",
                    mapOf(
                            "center" to ImageView.ScaleType.CENTER,
                            "fitCenter" to ImageView.ScaleType.FIT_CENTER,
                            "fitXY" to ImageView.ScaleType.FIT_XY,
                            "fitStart" to ImageView.ScaleType.FIT_START,
                            "fitEnd" to ImageView.ScaleType.FIT_END,
                            "centerInside" to ImageView.ScaleType.CENTER_INSIDE,
                            "centerCrop" to ImageView.ScaleType.CENTER_CROP
                    ),
                    ImageView.ScaleType.FIT_XY
            ) { _, _, it ->
                scaleType(it)
            }
            colorAttr("borderColor") { _, _, it ->
                borderColor(it)
            }
            numberAttr<Double>("borderRadius") { _, _, it ->
                borderRadius(it.toPx())
            }
            numberAttr<Double>("borderWidth") { _, _, it ->
                borderWidth(it.toPx())
            }
            numberAttr<Float>("blurRadius") { _, _, it ->
                blurRadius(it)
            }
            numberAttr("blurSampling", 1f) { _, _, it ->
                blurSampling(it)
            }
        }
) {

    override fun onCreateWidget(
            c: ComponentContext,
            data: PropsELContext,
            attrs: Map<String, String>?,
            visibility: Int
    ): AsyncImage.Builder {
        return AsyncImage.create(c)
    }

    override fun onLoadStyles(
            owner: AsyncImage.Builder,
            c: ComponentContext,
            data: PropsELContext,
            attrs: Map<String, String>?,
            visibility: Int
    ) {
        super.onLoadStyles(owner, c, data, attrs, visibility)
        owner.url(if (visibility == View.GONE) {
            ""
        } else {
            data.tryGetValue(attrs?.get("url"), "")
        })
    }
}
