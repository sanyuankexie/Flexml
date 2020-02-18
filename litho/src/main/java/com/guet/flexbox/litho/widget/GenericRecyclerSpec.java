/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.guet.flexbox.litho.widget;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ItemAnimator;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.SnapHelper;

import com.facebook.litho.ComponentContext;
import com.facebook.litho.ComponentLayout;
import com.facebook.litho.Diff;
import com.facebook.litho.Size;
import com.facebook.litho.StateValue;
import com.facebook.litho.annotations.MountSpec;
import com.facebook.litho.annotations.OnBind;
import com.facebook.litho.annotations.OnBoundsDefined;
import com.facebook.litho.annotations.OnCreateInitialState;
import com.facebook.litho.annotations.OnCreateMountContent;
import com.facebook.litho.annotations.OnEvent;
import com.facebook.litho.annotations.OnMeasure;
import com.facebook.litho.annotations.OnMount;
import com.facebook.litho.annotations.OnUnbind;
import com.facebook.litho.annotations.OnUnmount;
import com.facebook.litho.annotations.OnUpdateState;
import com.facebook.litho.annotations.Param;
import com.facebook.litho.annotations.Prop;
import com.facebook.litho.annotations.PropDefault;
import com.facebook.litho.annotations.ResType;
import com.facebook.litho.annotations.ShouldAlwaysRemeasure;
import com.facebook.litho.annotations.ShouldUpdate;
import com.facebook.litho.annotations.State;
import com.facebook.litho.widget.Binder;
import com.facebook.litho.widget.LithoRecylerView;
import com.facebook.litho.widget.ReMeasureEvent;

import java.util.List;
import java.util.Objects;

@MountSpec(
        hasChildLithoViews = true,
        isPureRender = true
)
class GenericRecyclerSpec {

    @PropDefault
    static final int scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY;
    @PropDefault
    static final boolean hasFixedSize = true;
    @PropDefault
    static final boolean nestedScrollingEnabled = true;
    @PropDefault
    static final ItemAnimator itemAnimator = new NoUpdateItemAnimator();
    @PropDefault
    static final int overScrollMode = View.OVER_SCROLL_ALWAYS;
    @PropDefault
    static final boolean clipToPadding = true;
    @PropDefault
    static final boolean clipChildren = true;
    @PropDefault
    static final int leftPadding = 0;
    @PropDefault
    static final int rightPadding = 0;
    @PropDefault
    static final int topPadding = 0;
    @PropDefault
    static final int bottomPadding = 0;

    @OnMeasure
    static void onMeasure(
            ComponentContext c,
            ComponentLayout layout,
            int widthSpec,
            int heightSpec,
            Size measureOutput,
            @Prop Binder<RecyclerView> binder) {
        binder.measure(
                measureOutput,
                widthSpec,
                heightSpec,
                (binder.canMeasure() || binder.isWrapContent())
                        ? GenericRecycler.onRemeasure(c) : null
        );
    }

    @OnBoundsDefined
    static void onBoundsDefined(
            ComponentContext context,
            ComponentLayout layout,
            @Prop Binder<RecyclerView> binder) {
        binder.setSize(layout.getWidth(), layout.getHeight());
    }

    @OnCreateMountContent
    static LithoRecylerView onCreateMountContent(Context c) {
        return new LithoRecylerView(c);
    }

    @OnMount
    static void onMount(
            ComponentContext c,
            LithoRecylerView recyclerView,
            @Prop Binder<RecyclerView> binder,
            @Prop(optional = true) boolean hasFixedSize,
            @Prop(optional = true) boolean clipToPadding,
            @Prop(optional = true) int leftPadding,
            @Prop(optional = true) int rightPadding,
            @Prop(optional = true) int topPadding,
            @Prop(optional = true) int bottomPadding,
            @Prop(optional = true) boolean clipChildren,
            @Prop(optional = true) boolean nestedScrollingEnabled,
            @Prop(optional = true) int scrollBarStyle,
            @Prop(optional = true) RecyclerView.ItemDecoration itemDecoration,
            @Prop(optional = true) boolean horizontalFadingEdgeEnabled,
            @Prop(optional = true) boolean verticalFadingEdgeEnabled,
            @Prop(optional = true, resType = ResType.DIMEN_SIZE) int fadingEdgeLength,
            @Prop(optional = true) int overScrollMode,
            @Prop(optional = true, isCommonProp = true) CharSequence contentDescription,
            @Prop(optional = true) ItemAnimator itemAnimator) {
        recyclerView.setContentDescription(contentDescription);
        recyclerView.setHasFixedSize(hasFixedSize);
        recyclerView.setClipToPadding(clipToPadding);
        recyclerView.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
        recyclerView.setClipChildren(clipChildren);
        recyclerView.setNestedScrollingEnabled(nestedScrollingEnabled);
        recyclerView.setScrollBarStyle(scrollBarStyle);
        recyclerView.setHorizontalFadingEdgeEnabled(horizontalFadingEdgeEnabled);
        recyclerView.setVerticalFadingEdgeEnabled(verticalFadingEdgeEnabled);
        recyclerView.setFadingEdgeLength(fadingEdgeLength);
        recyclerView.setOverScrollMode(overScrollMode);

        if (itemDecoration != null) {
            recyclerView.addItemDecoration(itemDecoration);
        }

        recyclerView.setItemAnimator(
                itemAnimator != GenericRecyclerSpec.itemAnimator
                        ? itemAnimator
                        : new NoUpdateItemAnimator()
        );

        binder.mount(recyclerView);
    }

    @OnBind
    static void onBind(
            ComponentContext c,
            LithoRecylerView recyclerView,
            @Prop Binder<RecyclerView> binder,
            @Prop(optional = true, varArg = "onScrollListener") List<OnScrollListener> onScrollListeners,
            @Prop(optional = true) SnapHelper snapHelper,
            @Prop(optional = true) LithoRecylerView.TouchInterceptor touchInterceptor) {

        if (onScrollListeners != null) {
            for (OnScrollListener onScrollListener : onScrollListeners) {
                recyclerView.addOnScrollListener(onScrollListener);
            }
        }

        if (touchInterceptor != null) {
            recyclerView.setTouchInterceptor(touchInterceptor);
        }

        // We cannot detach the snap helper in unbind, so it may be possible for it to get
        // attached twice which causes SnapHelper to raise an exception.
        if (snapHelper != null && recyclerView.getOnFlingListener() == null) {
            snapHelper.attachToRecyclerView(recyclerView);
        }

        binder.bind(recyclerView);
    }

    @OnUnbind
    static void onUnbind(
            ComponentContext context,
            LithoRecylerView recyclerView,
            @Prop Binder<RecyclerView> binder,
            @Prop(optional = true, varArg = "onScrollListener")
                    List<OnScrollListener> onScrollListeners) {

        binder.unbind(recyclerView);

        if (onScrollListeners != null) {
            for (OnScrollListener onScrollListener : onScrollListeners) {
                recyclerView.removeOnScrollListener(onScrollListener);
            }
        }
        recyclerView.setTouchInterceptor(null);

    }

    @OnUnmount
    static void onUnmount(
            ComponentContext context,
            LithoRecylerView recyclerView,
            @Prop Binder<RecyclerView> binder,
            @Prop(optional = true) RecyclerView.ItemDecoration itemDecoration,
            @Prop(optional = true) SnapHelper snapHelper
    ) {
        if (itemDecoration != null) {
            recyclerView.removeItemDecoration(itemDecoration);
        }

        binder.unmount(recyclerView);

        if (snapHelper != null) {
            snapHelper.attachToRecyclerView(null);
        }
    }

    @ShouldUpdate(onMount = true)
    static boolean shouldUpdate(
            @Prop Diff<Binder<RecyclerView>> binder,
            @Prop(optional = true) Diff<Boolean> hasFixedSize,
            @Prop(optional = true) Diff<Boolean> clipToPadding,
            @Prop(optional = true) Diff<Integer> leftPadding,
            @Prop(optional = true) Diff<Integer> rightPadding,
            @Prop(optional = true) Diff<Integer> topPadding,
            @Prop(optional = true) Diff<Integer> bottomPadding,
            @Prop(optional = true) Diff<Boolean> clipChildren,
            @Prop(optional = true) Diff<Integer> scrollBarStyle,
            @Prop(optional = true) Diff<RecyclerView.ItemDecoration> itemDecoration,
            @Prop(optional = true) Diff<Boolean> horizontalFadingEdgeEnabled,
            @Prop(optional = true) Diff<Boolean> verticalFadingEdgeEnabled,
            @Prop(optional = true, resType = ResType.DIMEN_SIZE) Diff<Integer> fadingEdgeLength,
            @Prop(optional = true) Diff<ItemAnimator> itemAnimator,
            @State Diff<Integer> measureVersion) {
        if (!Objects.equals(measureVersion.getPrevious(), measureVersion.getNext())) {
            return true;
        }
        if (binder.getPrevious() != binder.getNext()) {
            return true;
        }
        if (!Objects.equals(hasFixedSize.getNext(), hasFixedSize.getNext())) {
            return true;
        }

        if (!Objects.equals(clipToPadding.getPrevious(), clipToPadding.getNext())) {
            return true;
        }

        if (!Objects.equals(leftPadding.getPrevious(), leftPadding.getNext())) {
            return true;
        }

        if (!Objects.equals(rightPadding.getPrevious(), rightPadding.getNext())) {
            return true;
        }

        if (!Objects.equals(topPadding.getPrevious(), topPadding.getNext())) {
            return true;
        }

        if (!Objects.equals(bottomPadding.getPrevious(), bottomPadding.getNext())) {
            return true;
        }

        if (!Objects.equals(clipChildren.getPrevious(), clipChildren.getNext())) {
            return true;
        }

        if (!Objects.equals(scrollBarStyle.getPrevious(), scrollBarStyle.getNext())) {
            return true;
        }

        if (!Objects.equals(horizontalFadingEdgeEnabled.getPrevious(), horizontalFadingEdgeEnabled.getNext())) {
            return true;
        }

        if (!Objects.equals(verticalFadingEdgeEnabled.getPrevious(), verticalFadingEdgeEnabled.getNext())) {
            return true;
        }

        if (!Objects.equals(fadingEdgeLength.getNext(), fadingEdgeLength.getPrevious())) {
            return true;
        }

        final ItemAnimator previousItemAnimator = itemAnimator.getPrevious();
        final ItemAnimator nextItemAnimator = itemAnimator.getNext();

        if (previousItemAnimator == null
                ? nextItemAnimator != null
                : !previousItemAnimator.getClass()
                .equals(nextItemAnimator != null
                        ? nextItemAnimator.getClass() : null
                )) {
            return true;
        }

        final RecyclerView.ItemDecoration previous = itemDecoration.getPrevious();
        final RecyclerView.ItemDecoration next = itemDecoration.getNext();
        return !Objects.equals(previous, next);
    }

    @OnEvent(ReMeasureEvent.class)
    static void onRemeasure(ComponentContext c, @State int measureVersion) {
        GenericRecycler.onUpdateMeasureAsync(c, measureVersion + 1);
    }

    @OnCreateInitialState
    static void onCreateInitialState(
            ComponentContext c,
            StateValue<Integer> measureVersion
    ) {
        measureVersion.set(0);
    }

    @OnUpdateState
    static void onUpdateMeasure(@Param int measureVer, StateValue<Integer> measureVersion) {
        // We don't really need to update a state here. This state update is only really used to force
        // a re-layout on the tree containing this Recycler.
        measureVersion.set(measureVer);
    }

    @ShouldAlwaysRemeasure
    static boolean shouldAlwaysRemeasure(
            @Prop Binder<RecyclerView> binder
    ) {
        return binder.isWrapContent();
    }

    public static class NoUpdateItemAnimator extends DefaultItemAnimator {
        NoUpdateItemAnimator() {
            super();
            setSupportsChangeAnimations(false);
        }
    }
}
