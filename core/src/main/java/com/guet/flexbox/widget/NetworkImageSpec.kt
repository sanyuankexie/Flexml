package com.guet.flexbox.widget

import android.content.Context
import android.text.TextUtils
import android.widget.ImageView.ScaleType
import com.facebook.litho.*
import com.facebook.litho.annotations.*
import com.facebook.litho.utils.MeasureUtils

@MountSpec(isPureRender = true, poolSize = 30)
internal object NetworkImageSpec {

    @PropDefault
    val scaleType = ScaleType.FIT_CENTER
    @PropDefault
    val imageAspectRatio = 1f

    @OnCreateMountContent
    fun onCreateMountContent(c: Context): NetworkMatrixDrawable {
        return NetworkMatrixDrawable(c)
    }

    @OnMeasure
    fun onMeasure(c: ComponentContext,
                  layout: ComponentLayout,
                  widthSpec: Int,
                  heightSpec: Int,
                  size: Size,
                  @Prop(optional = true) imageAspectRatio: Float) {
        MeasureUtils.measureWithAspectRatio(
                widthSpec,
                heightSpec,
                imageAspectRatio,
                size
        )
    }

    @OnBoundsDefined
    fun onBoundsDefined(
            c: ComponentContext,
            layout: ComponentLayout,
            layoutWidth: Output<Int>,
            layoutHeight: Output<Int>,
            horizontalPadding: Output<Int>,
            verticalPadding: Output<Int>) {
        horizontalPadding.set(layout.paddingLeft + layout.paddingRight)
        verticalPadding.set(layout.paddingTop + layout.paddingBottom)
        layoutWidth.set(layout.width)
        layoutHeight.set(layout.height)
    }

    @OnMount
    fun onMount(c: ComponentContext,
                drawable: NetworkMatrixDrawable,
                @Prop url: CharSequence,
                @Prop(optional = true) borderRadius: Int,
                @Prop(optional = true) borderWidth: Int,
                @Prop(optional = true) borderColor: Int,
                @Prop(optional = true) blurRadius: Int,
                @Prop(optional = true) blurSampling: Int,
                @Prop(optional = true) scaleType: ScaleType,
                @FromBoundsDefined layoutWidth: Int,
                @FromBoundsDefined layoutHeight: Int,
                @FromBoundsDefined horizontalPadding: Int,
                @FromBoundsDefined verticalPadding: Int) {
        drawable.mount(
                url,
                layoutWidth,
                layoutHeight,
                horizontalPadding,
                verticalPadding,
                borderRadius,
                borderWidth,
                borderColor,
                blurRadius,
                blurSampling,
                scaleType
        )
    }

    @OnUnmount
    fun onUnmount(c: ComponentContext,
                  drawable: NetworkMatrixDrawable) {
        drawable.unmount()
    }

    @ShouldUpdate(onMount = true)
    fun shouldUpdate(
            @Prop(optional = true) blurSampling: Diff<Int>,
            @Prop(optional = true) blurRadius: Diff<Int>,
            @Prop(optional = true) scaleType: Diff<ScaleType>,
            @Prop(optional = true) borderRadius: Diff<Int>,
            @Prop(optional = true) borderWidth: Diff<Int>,
            @Prop(optional = true) borderColor: Diff<Int>,
            @Prop url: Diff<CharSequence>): Boolean {
        return !TextUtils.equals(url.next, url.previous)
                || scaleType.next != scaleType.previous
                || borderRadius.next != borderRadius.previous
                || borderWidth.next != borderWidth.previous
                || borderColor.next != borderColor.previous
                || blurRadius.next != blurRadius.previous
                || blurSampling.next != blurSampling.previous
    }
}