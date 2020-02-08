package com.guet.flexbox.litho.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView.ScaleType
import com.facebook.litho.*
import com.facebook.litho.annotations.*
import com.facebook.litho.utils.MeasureUtils
import com.guet.flexbox.litho.drawable.MatrixGlideDrawable

@MountSpec(isPureRender = true, poolSize = 30)
internal object GlideImageSpec {

    @PropDefault
    val scaleType = ScaleType.FIT_CENTER
    @PropDefault
    val imageAspectRatio = 1f

    @OnCreateMountContent
    fun onCreateMountContent(c: Context): MatrixGlideDrawable {
        return MatrixGlideDrawable(c)
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

    @OnBind
    fun onBind(
            c: ComponentContext,
            image: MatrixGlideDrawable,
            @FromBoundsDefined width: Int,
            @FromBoundsDefined height: Int
    ) {
        image.bind(width, height)
    }

    @OnMount
    fun onMount(c: ComponentContext,
                image: MatrixGlideDrawable,
                @Prop(optional = true) resId: Int,
                @Prop(optional = true) url: String?,
                @Prop(optional = true) blurRadius: Float,
                @Prop(optional = true) blurSampling: Float,
                @Prop(optional = true) scaleType: ScaleType,
                @Prop(optional = true) leftTop: Float,
                @Prop(optional = true) rightTop: Float,
                @Prop(optional = true) rightBottom: Float,
                @Prop(optional = true) leftBottom: Float,
                @FromBoundsDefined width: Int,
                @FromBoundsDefined height: Int
    ) {
        val model: Any? = if (resId == 0) {
            if (url.isNullOrEmpty()) {
                null
            } else {
                url
            }
        } else {
            resId
        }
        if (model != null) {
            image.mount(
                    model,
                    width,
                    height,
                    blurRadius,
                    blurSampling,
                    scaleType,
                    leftTop,
                    rightTop,
                    rightBottom,
                    leftBottom
            )
        }
    }

    @OnUnmount
    fun onUnmount(c: ComponentContext,
                  drawable: MatrixGlideDrawable) {
        drawable.unmount()
    }

    @ShouldUpdate(onMount = true)
    fun shouldUpdate(
            @Prop(optional = true) resId: Diff<Int>,
            @Prop(optional = true) blurSampling: Diff<Float>,
            @Prop(optional = true) blurRadius: Diff<Float>,
            @Prop(optional = true) scaleType: Diff<ScaleType>,
            @Prop(optional = true) url: Diff<String?>,
            @Prop(optional = true) drawable: Diff<Drawable?>,
            @Prop(optional = true) leftTop: Diff<Float>,
            @Prop(optional = true) rightTop: Diff<Float>,
            @Prop(optional = true) rightBottom: Diff<Float>,
            @Prop(optional = true) leftBottom: Diff<Float>): Boolean {
        return url.next != url.previous
                || scaleType.next != scaleType.previous
                || blurRadius.next != blurRadius.previous
                || blurSampling.next != blurSampling.previous
                || drawable.next != drawable.previous
                || resId.next != resId.previous
                || rightTop.next != rightTop.previous
                || leftTop.next != leftTop.previous
                || rightBottom.next != rightBottom.previous
                || leftBottom.next != leftBottom.previous
    }
}