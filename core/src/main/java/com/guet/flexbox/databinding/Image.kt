package com.guet.flexbox.databinding

import android.widget.ImageView

internal object Image : Declaration(Common) {
    override val attributeSet: AttributeSet by create {
        enum("scaleType", mapOf(
                "center" to ImageView.ScaleType.CENTER,
                "fitCenter" to ImageView.ScaleType.FIT_CENTER,
                "fitXY" to ImageView.ScaleType.FIT_XY,
                "fitStart" to ImageView.ScaleType.FIT_START,
                "fitEnd" to ImageView.ScaleType.FIT_END,
                "centerInside" to ImageView.ScaleType.CENTER_INSIDE,
                "centerCrop" to ImageView.ScaleType.CENTER_CROP
        ))
        value("blurRadius")
        value("blurSampling")
    }
}