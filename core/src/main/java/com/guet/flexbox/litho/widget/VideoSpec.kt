package com.guet.flexbox.litho.widget

import android.content.Context
import com.facebook.litho.ComponentContext
import com.facebook.litho.annotations.MountSpec
import com.facebook.litho.annotations.OnCreateMountContent
import com.facebook.litho.annotations.OnMount
import com.yqritc.scalablevideoview.ScalableVideoView

@MountSpec(isPureRender = true)
object VideoSpec {

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
}