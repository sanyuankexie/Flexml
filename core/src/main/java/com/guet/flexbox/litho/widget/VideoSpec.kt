package com.guet.flexbox.litho.widget

import android.content.Context
import com.facebook.litho.ComponentContext
import com.facebook.litho.ComponentLayout
import com.facebook.litho.Size
import com.facebook.litho.annotations.*
import com.facebook.litho.utils.MeasureUtils
import com.yqritc.scalablevideoview.ScalableVideoView

@MountSpec(isPureRender = true)
object VideoSpec {

    @PropDefault
    val videoAspectRatio = 1f

    @OnCreateMountContent
    fun onCreateMountContent(c: Context): ScalableVideoView {
        return ScalableVideoView(c)
    }

    @OnMount
    fun onMount(
            c: ComponentContext,
            view: ScalableVideoView
    ) {

    }

    @OnMeasure
    fun onMeasure(c: ComponentContext,
                  layout: ComponentLayout,
                  widthSpec: Int,
                  heightSpec: Int,
                  size: Size,
                  @Prop(optional = true) videoAspectRatio: Float) {
        MeasureUtils.measureWithAspectRatio(
                widthSpec,
                heightSpec,
                videoAspectRatio,
                size
        )
    }
}
