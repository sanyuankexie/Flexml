package com.guet.flexbox.litho.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView.ScaleType
import com.facebook.litho.*
import com.facebook.litho.annotations.*
import com.facebook.litho.utils.MeasureUtils

@MountSpec(isPureRender = true, poolSize = 30)
internal object GlideImageSpec {

    @PropDefault
    val scaleType = ScaleType.FIT_CENTER
    @PropDefault
    val imageAspectRatio = 1f

    @OnCreateMountContent
    fun onCreateMountContent(c: Context): GlideDrawable {
        return GlideDrawable(c)
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
                image: GlideDrawable,
                @Prop(optional = true) resId: Int,
                @Prop(optional = true) drawable: Drawable?,
                @Prop(optional = true) url: String?,
                @Prop(optional = true) blurRadius: Float,
                @Prop(optional = true) blurSampling: Float,
                @Prop(optional = true) scaleType: ScaleType,
                @FromBoundsDefined width: Int,
                @FromBoundsDefined height: Int) {
        val model: Any? = when {
            resId != 0 -> {
                resId
            }
            drawable != null -> {
                drawable
            }
            !url.isNullOrBlank() -> {
                url
            }
            else -> null
        }
        if (model != null) {
            image.mount(
                    model,
                    width,
                    height,
                    floatArrayOf(100f, 100f, 100f, 100f),
                    blurRadius,
                    blurSampling,
                    scaleType
            )
        }
    }

    @OnUnmount
    fun onUnmount(c: ComponentContext,
                  drawable: GlideDrawable) {
        drawable.unmount()
    }

    @ShouldUpdate(onMount = true)
    fun shouldUpdate(
            @Prop(optional = true) resId: Diff<Int>,
            @Prop(optional = true) blurSampling: Diff<Float>,
            @Prop(optional = true) blurRadius: Diff<Float>,
            @Prop(optional = true) scaleType: Diff<ScaleType>,
            @Prop(optional = true) url: Diff<String?>,
            @Prop(optional = true) drawable: Diff<Drawable?>): Boolean {
        return url.next != url.previous
                || scaleType.next != scaleType.previous
                || blurRadius.next != blurRadius.previous
                || blurSampling.next != blurSampling.previous
                || drawable.next != drawable.previous
                || resId.next != resId.previous
    }
}