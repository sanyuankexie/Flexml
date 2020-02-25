package com.guet.flexbox.litho.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView.ScaleType
import com.bumptech.glide.Glide
import com.bumptech.glide.request.transition.Transition
import com.facebook.litho.*
import com.facebook.litho.annotations.*
import com.facebook.litho.utils.MeasureUtils
import com.guet.flexbox.litho.drawable.BitmapDrawable
import com.guet.flexbox.litho.drawable.DrawableWrapper
import com.guet.flexbox.litho.drawable.NoOpDrawable
import com.guet.flexbox.litho.drawable.load.CornerRadius
import com.guet.flexbox.litho.drawable.load.DelegateTarget
import com.guet.flexbox.litho.drawable.load.DrawableLoaderModule
import com.guet.flexbox.litho.transforms.FastBlur


@MountSpec(
        isPureRender = true,
        poolSize = 30
)
object DynamicImageSpec {

    @PropDefault
    val scaleType = ScaleType.FIT_XY
    @PropDefault
    val imageAspectRatio = 1f

    @OnCreateInitialState
    fun onCreateInitialState(
            c: ComponentContext,
            target: StateValue<DynamicImageTarget>
    ) {
        target.set(DynamicImageTarget())
    }

    @OnCreateMountContent(mountingType = MountingType.DRAWABLE)
    fun onCreateMountContent(c: Context): DynamicImageDrawable {
        return DynamicImageDrawable(c)
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

    @OnMount
    fun onMount(
            c: ComponentContext,
            image: DynamicImageDrawable,
            @Prop(optional = true) model: Any,
            @Prop(optional = true) blurRadius: Float,
            @Prop(optional = true) blurSampling: Float,
            @Prop(optional = true) scaleType: ScaleType,
            @Prop(optional = true) leftTopRadius: Float,
            @Prop(optional = true) rightTopRadius: Float,
            @Prop(optional = true) rightBottomRadius: Float,
            @Prop(optional = true) leftBottomRadius: Float,
            @FromBoundsDefined width: Int,
            @FromBoundsDefined height: Int,
            @State target: DynamicImageTarget
    ) {
        image.mount(
                target,
                model,
                blurRadius,
                blurSampling,
                scaleType,
                leftTopRadius,
                rightTopRadius,
                rightBottomRadius,
                leftBottomRadius,
                width,
                height
        )
    }

    @OnUnmount
    fun onUnmount(
            c: ComponentContext,
            image: DynamicImageDrawable,
            @State target: DynamicImageTarget
    ) {
        image.unmount(target)
    }

    class DynamicImageTarget : DelegateTarget<BitmapDrawable>() {

        var wrapper: DynamicImageDrawable? = null

        override fun onLoadFailed(errorDrawable: Drawable?) {
            val wrapper = this.wrapper
            if (wrapper != null) {
                wrapper.wrappedDrawable = wrapper.cacheNoOpDrawable
            }
        }

        override fun onLoadCleared(placeholder: Drawable?) {
            val wrapper = this.wrapper
            if (wrapper != null) {
                wrapper.wrappedDrawable = wrapper.cacheNoOpDrawable
            }
        }

        override fun onResourceReady(
                resource: BitmapDrawable,
                transition: Transition<in BitmapDrawable>?
        ) {
            val wrapper = this.wrapper
            if (wrapper != null) {
                wrapper.wrappedDrawable = resource
                wrapper.invalidateSelf()
            }
        }
    }

    class DynamicImageDrawable(private val c: Context) : DrawableWrapper<Drawable>(NoOpDrawable()) {

        val cacheNoOpDrawable = wrappedDrawable

        fun mount(
                target: DynamicImageTarget,
                @Prop(optional = true) model: Any,
                @Prop(optional = true) blurRadius: Float,
                @Prop(optional = true) blurSampling: Float,
                @Prop(optional = true) scaleType: ScaleType,
                @Prop(optional = true) leftTopRadius: Float,
                @Prop(optional = true) rightTopRadius: Float,
                @Prop(optional = true) rightBottomRadius: Float,
                @Prop(optional = true) leftBottomRadius: Float,
                @FromBoundsDefined width: Int,
                @FromBoundsDefined height: Int
        ) {
            target.wrapper = this
            var request = Glide.with(c)
                    .`as`(BitmapDrawable::class.java)
                    .load(model)
                    .set(DrawableLoaderModule.scaleType, scaleType)
                    .set(DrawableLoaderModule.cornerRadius, CornerRadius(
                            leftTopRadius,
                            rightTopRadius,
                            rightBottomRadius,
                            leftBottomRadius
                    ))
            request = if (blurSampling > 1) {
                val w = (width / blurSampling).toInt()
                val h = (height / blurSampling).toInt()
                request.override(w, h)
            } else {
                request.override(width, height)
            }
            if (blurRadius > 0) {
                request = request.transform(FastBlur(blurRadius))
            }
            request.into(target)
        }

        fun unmount(target: DynamicImageTarget) {
            target.wrapper = null
            wrappedDrawable = cacheNoOpDrawable
            Glide.with(c).clear(target)
        }
    }
}