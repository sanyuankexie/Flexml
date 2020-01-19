package com.guet.flexbox.litho.widget

import android.content.Context
import android.graphics.Color
import androidx.viewpager2.widget.ViewPager2
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.annotations.*
import com.guet.flexbox.Orientation
import com.guet.flexbox.litho.toPx


@MountSpec(isPureRender = true, hasChildLithoViews = true)
object BannerSpec {

    @PropDefault
    val timeSpan = 500L

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
        if (!children.isNullOrEmpty()) {
            view.viewPager.adapter = BannerAdapter(
                    isCircular,
                    children
            )
            if (isCircular) {
                view.viewPager.currentItem = children.size * 100
            }
        }
        if (orientation == Orientation.HORIZONTAL) {
            view.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        } else {
            view.viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
        }
        view.indicatorEnable = indicatorEnable
        view.indicatorHeightPx = indicatorHeightPx
        view.indicatorSelectedColor = indicatorSelectedColor
        view.indicatorUnselectedColor = indicatorUnselectedColor
    }

    @OnUnmount
    fun onUnmount(
            c: ComponentContext,
            view: LithoBannerView
    ) {
        view.viewPager.adapter = null
    }

    @OnBind
    fun onBind(
            c: ComponentContext,
            host: LithoBannerView,
            @Prop(optional = true) timeSpan: Long,
            @Prop(optional = true, varArg = "child") children: List<Component>?
    ) {
        host.bind(timeSpan)
    }

    @OnUnbind
    fun onUnbind(
            c: ComponentContext,
            host: LithoBannerView
    ) {
        host.unbind()
    }

}

