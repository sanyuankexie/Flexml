package com.guet.flexbox.litho.widget

import android.content.Context
import android.widget.ImageView.ScaleType
import com.facebook.litho.*
import com.facebook.litho.annotations.*
import com.facebook.litho.utils.MeasureUtils
import com.guet.flexbox.litho.drawable.GlideDrawable

@MountSpec(
        isPureRender = true,
        poolSize = 30,
        canPreallocate = true
)
internal object GlideImageSpec {

    @PropDefault
    val scaleType = ScaleType.FIT_XY
    @PropDefault
    val imageAspectRatio = 1f

    @OnCreateMountContent
    fun onCreateMountContent(c: Context): GlideDrawable {
        return GlideDrawable(c)
    }

    @OnMeasure
    fun onMeasure(
            c: ComponentContext,
            layout: ComponentLayout,
            widthSpec: Int,
            heightSpec: Int,
            size: Size,
            @Prop(optional = true) imageAspectRatio: Float
    ) {
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

    @OnUnbind
    fun onUnbind(c: ComponentContext,
                 image: GlideDrawable
    ) {
        image.unbind()
    }

    @OnBind
    fun onBind(
            c: ComponentContext,
            image: GlideDrawable,
            @FromBoundsDefined width: Int,
            @FromBoundsDefined height: Int
    ) {
        image.bind(width, height)
    }

    @OnMount
    fun onMount(
            c: ComponentContext,
            image: GlideDrawable,
            @Prop(optional = true) model: Any,
            @Prop(optional = true) blurRadius: Float,
            @Prop(optional = true) blurSampling: Float,
            @Prop(optional = true) scaleType: ScaleType,
            @Prop(optional = true) leftTopRadius: Float,
            @Prop(optional = true) rightTopRadius: Float,
            @Prop(optional = true) rightBottomRadius: Float,
            @Prop(optional = true) leftBottomRadius: Float
    ) {
        image.mount(
                model,
                blurRadius,
                blurSampling,
                scaleType,
                leftTopRadius,
                rightTopRadius,
                rightBottomRadius,
                leftBottomRadius
        )
    }

    @OnUnmount
    fun onUnmount(c: ComponentContext,
                  drawable: GlideDrawable) {
        drawable.unmount()
    }

    @ShouldUpdate(onMount = true)
    fun shouldUpdate(
            @Prop(optional = true) blurSampling: Diff<Float>,
            @Prop(optional = true) blurRadius: Diff<Float>,
            @Prop(optional = true) scaleType: Diff<ScaleType>,
            @Prop(optional = true) model: Diff<Any>,
            @Prop(optional = true) leftTopRadius: Diff<Float>,
            @Prop(optional = true) rightTopRadius: Diff<Float>,
            @Prop(optional = true) rightBottomRadius: Diff<Float>,
            @Prop(optional = true) leftBottomRadius: Diff<Float>): Boolean {
        return model.next != model.previous
                || blurSampling.next != blurSampling.previous
                || blurRadius.next != blurRadius.previous
                || scaleType.next != scaleType.previous
                || rightTopRadius.next != rightTopRadius.previous
                || leftTopRadius.next != leftTopRadius.previous
                || rightBottomRadius.next != rightBottomRadius.previous
                || leftBottomRadius.next != leftBottomRadius.previous
    }
}