package com.guet.flexbox.litho.widget

import android.content.Context
import android.graphics.drawable.Drawable
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
            width: Output<Int>,
            height: Output<Int>) {
        width.set(layout.width - (layout.paddingLeft + layout.paddingRight))
        height.set(layout.height - (layout.paddingTop + layout.paddingBottom))
    }

    @OnMount
    fun onMount(c: ComponentContext,
                image: NetworkMatrixDrawable,
                @Prop(optional = true) resId: Int,
                @Prop(optional = true) drawable: Drawable?,
                @Prop(optional = true) url: CharSequence?,
                @Prop(optional = true) blurRadius: Float,
                @Prop(optional = true) blurSampling: Float,
                @Prop(optional = true) scaleType: ScaleType,
                @FromBoundsDefined width: Int,
                @FromBoundsDefined height: Int) {
        when {
            resId != 0 -> {
                image.mount(
                        resId,
                        width,
                        height,
                        blurRadius,
                        blurSampling,
                        scaleType
                )
            }
            drawable != null -> {
                image.mount(
                        drawable,
                        width,
                        height,
                        blurRadius,
                        blurSampling,
                        scaleType
                )
            }
            else -> {
                image.mount(
                        url ?: "",
                        width,
                        height,
                        blurRadius,
                        blurSampling,
                        scaleType
                )
            }
        }
    }

    @OnUnmount
    fun onUnmount(c: ComponentContext,
                  drawable: NetworkMatrixDrawable) {
        drawable.unmount()
    }

    @ShouldUpdate(onMount = true)
    fun shouldUpdate(
            @Prop(optional = true) resId: Diff<Int>,
            @Prop(optional = true) blurSampling: Diff<Float>,
            @Prop(optional = true) blurRadius: Diff<Float>,
            @Prop(optional = true) scaleType: Diff<ScaleType>,
            @Prop(optional = true) url: Diff<CharSequence?>,
            @Prop(optional = true) drawable: Diff<Drawable?>): Boolean {
        return !TextUtils.equals(url.next, url.previous)
                || scaleType.next != scaleType.previous
                || blurRadius.next != blurRadius.previous
                || blurSampling.next != blurSampling.previous
                || drawable.next != drawable.previous
                || resId.next != resId.previous
    }
}