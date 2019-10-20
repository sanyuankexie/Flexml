package com.guet.flexbox.widget;

import android.content.Context;
import android.text.TextUtils;

import com.facebook.litho.ComponentContext;
import com.facebook.litho.ComponentLayout;
import com.facebook.litho.Diff;
import com.facebook.litho.Output;
import com.facebook.litho.Size;
import com.facebook.litho.annotations.FromBoundsDefined;
import com.facebook.litho.annotations.MountSpec;
import com.facebook.litho.annotations.OnBoundsDefined;
import com.facebook.litho.annotations.OnCreateMountContent;
import com.facebook.litho.annotations.OnMeasure;
import com.facebook.litho.annotations.OnMount;
import com.facebook.litho.annotations.Prop;
import com.facebook.litho.annotations.PropDefault;
import com.facebook.litho.annotations.ShouldUpdate;
import com.facebook.litho.utils.MeasureUtils;

import static android.widget.ImageView.ScaleType;

@MountSpec(isPureRender = true, poolSize = 10)
final class AsyncImageSpec {

    @PropDefault
    static final ScaleType scaleType = ScaleType.FIT_XY;
    @PropDefault
    static final float imageAspectRatio = 1f;

    @OnCreateMountContent
    static AsyncMatrixDrawable onCreateMountContent(Context c) {
        return new AsyncMatrixDrawable(c);
    }

    @OnMeasure
    static void onMeasure(ComponentContext c,
                          ComponentLayout layout,
                          int widthSpec,
                          int heightSpec,
                          Size size,
                          @Prop(optional = true) float imageAspectRatio) {
        MeasureUtils.measureWithAspectRatio(
                widthSpec,
                heightSpec,
                imageAspectRatio,
                size
        );
    }

    @OnBoundsDefined
    static void onBoundsDefined(
            ComponentContext c,
            ComponentLayout layout,
            Output<Integer> layoutWidth,
            Output<Integer> layoutHeight,
            Output<Integer> horizontalPadding,
            Output<Integer> verticalPadding) {
        horizontalPadding.set(layout.getPaddingLeft() + layout.getPaddingRight());
        verticalPadding.set(layout.getPaddingTop() + layout.getPaddingBottom());
        layoutWidth.set(layout.getWidth());
        layoutHeight.set(layout.getHeight());
    }

    @OnMount
    static void onMount(ComponentContext c,
                        AsyncMatrixDrawable drawable,
                        @Prop CharSequence url,
                        @Prop(optional = true) ScaleType scaleType,
                        @Prop(optional = true) float radius,
                        @FromBoundsDefined int layoutWidth,
                        @FromBoundsDefined int layoutHeight,
                        @FromBoundsDefined int horizontalPadding,
                        @FromBoundsDefined int verticalPadding) {
        drawable.mount(
                url,
                layoutWidth,
                layoutHeight,
                horizontalPadding,
                verticalPadding,
                radius,
                scaleType
        );
    }

    @ShouldUpdate(onMount = true)
    static boolean shouldUpdate(
            @Prop(optional = true) Diff<ScaleType> scaleType,
            @Prop Diff<CharSequence> url) {
        return scaleType.getNext() != scaleType.getPrevious()
                || !TextUtils.equals(url.getPrevious(), url.getNext());
    }
}
