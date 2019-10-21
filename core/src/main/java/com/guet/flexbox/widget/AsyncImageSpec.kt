package com.guet.flexbox.widget

import android.content.Context
import android.text.TextUtils
import android.widget.ImageView.ScaleType
import com.facebook.litho.*
import com.facebook.litho.annotations.*
import com.facebook.litho.utils.MeasureUtils

@MountSpec(isPureRender = true, poolSize = 10)
internal object AsyncImageSpec {

    @PropDefault
    val scaleType = ScaleType.FIT_XY
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
                drawable: AsyncMatrixDrawable,
                @Prop url: CharSequence,
                @Prop(optional = true) scaleType: ScaleType,
                @Prop(optional = true) borderRadius: Float,
                @Prop(optional = true) borderColor: Int,
                @Prop(optional = true) borderWidth: Float,
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
            @Prop(optional = true) scaleType: Diff<ScaleType>,
            @Prop url: Diff<CharSequence>,
            @Prop(optional = true) borderRadius: Diff<Float>,
            @Prop(optional = true) borderColor: Diff<Int>,
            @Prop(optional = true) borderWidth: Diff<Float>): Boolean {
        return (scaleType.previous != scaleType.next
                || !TextUtils.equals(url.previous, url.next)
                || borderRadius.previous != borderRadius.next
                || borderColor.previous != borderColor.next
                || borderWidth.previous != borderWidth.next)
    }
}
