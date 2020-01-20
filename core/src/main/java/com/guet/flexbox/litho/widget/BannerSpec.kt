package com.guet.flexbox.litho.widget

import android.content.Context
import android.graphics.Color
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.annotations.*
import com.guet.flexbox.Orientation
import com.guet.flexbox.litho.toPx


@MountSpec(isPureRender = true, hasChildLithoViews = true)
object BannerSpec {

    @PropDefault
    val timeSpan = 3000L

    @get:JvmName(name = "getIsCircular")
    @PropDefault
    val isCircular: Boolean = true

    @PropDefault
    val indicatorHeightPx: Int = 5.toPx()

    @PropDefault
    val orientation = Orientation.HORIZONTAL

    @PropDefault
    val indicatorSelectedColor: Int = Color.WHITE

    @PropDefault
    val indicatorUnselectedColor: Int = Color.GRAY

    @PropDefault
    val indicatorEnable: Boolean = true

    @OnCreateMountContent
    fun onCreateMountContent(c: Context): LithoBannerView {
        return LithoBannerView(c)
    }

    @OnMount
    fun onMount(
            c: ComponentContext,
            view: LithoBannerView,
            @Prop(optional = true) orientation: Orientation,
            @Prop(optional = true) isCircular: Boolean,
            @Prop(optional = true) indicatorHeightPx: Int,
            @Prop(optional = true) indicatorSelectedColor: Int,
            @Prop(optional = true) indicatorUnselectedColor: Int,
            @Prop(optional = true) indicatorEnable: Boolean,
            @Prop(optional = true, varArg = "child") children: List<Component>?
    ) {
        view.mount(
                c,
                orientation,
                isCircular,
                indicatorHeightPx,
                indicatorSelectedColor,
                indicatorUnselectedColor,
                indicatorEnable,
                children
        )
    }

    @OnUnmount
    fun onUnmount(
            c: ComponentContext,
            view: LithoBannerView
    ) {
        view.unmount()
    }

    @OnBind
    fun onBind(
            c: ComponentContext,
            view: LithoBannerView,
            @Prop(optional = true) timeSpan: Long
    ) {
        view.bind(timeSpan)
    }

    @OnUnbind
    fun onUnbind(
            c: ComponentContext,
            view: LithoBannerView
    ) {
        view.unbind()
    }

}

