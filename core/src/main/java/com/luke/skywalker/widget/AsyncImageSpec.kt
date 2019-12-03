package com.luke.skywalker.widget

import android.content.Context
import android.text.TextUtils
import android.widget.ImageView.ScaleType
import com.facebook.litho.*
import com.facebook.litho.annotations.*
import com.facebook.litho.utils.MeasureUtils

@MountSpec(isPureRender = true, poolSize = 30)
internal object AsyncImageSpec {

    @PropDefault
    val scaleType = ScaleType.FIT_CENTER
    @PropDefault
    val imageAspectRatio = 1f

    @OnCreateMountContent
    fun onCreateMountContent(c: Context): AsyncMatrixDrawable {
        return AsyncMatrixDrawable(c)
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
            width: Output<Int>,
            height: Output<Int>) {
        width.set(layout.width - (layout.paddingLeft + layout.paddingRight))
        height.set(layout.height - (layout.paddingTop + layout.paddingBottom))
    }

    @OnMount
    fun onMount(c: ComponentContext,
                drawable: AsyncMatrixDrawable,
                @Prop url: CharSequence,
                @Prop(optional = true) borderRadius: Int,
                @Prop(optional = true) borderWidth: Int,
                @Prop(optional = true) borderColor: Int,
                @Prop(optional = true) blurRadius: Float,
                @Prop(optional = true) blurSampling: Float,
                @Prop(optional = true) scaleType: ScaleType,
                @FromBoundsDefined width: Int,
                @FromBoundsDefined height: Int) {
        drawable.mount(
                url,
                width,
                height,
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
                  drawable: AsyncMatrixDrawable) {
        drawable.unmount()
    }

    @ShouldUpdate(onMount = true)
    fun shouldUpdate(
            @Prop(optional = true) blurSampling: Diff<Float>,
            @Prop(optional = true) blurRadius: Diff<Float>,
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