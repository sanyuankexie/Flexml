package com.guet.flexbox;


import android.text.TextUtils;

import com.facebook.litho.ClickEvent;
import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.VisibleEvent;
import com.facebook.litho.annotations.FromEvent;
import com.facebook.litho.annotations.LayoutSpec;
import com.facebook.litho.annotations.OnCreateLayout;
import com.facebook.litho.annotations.OnEvent;
import com.facebook.litho.annotations.Param;
import com.facebook.litho.annotations.Prop;
import com.facebook.litho.widget.TextChangedEvent;
import com.guet.flexbox.build.WidgetFactory;

@LayoutSpec
final class DynamicBoxSpec {

    @OnCreateLayout
    static Component onCreateLayout(
            ComponentContext c,
            @Prop(optional = true) Object bind,
            @Prop NodeInfo layout
    ) {
        return WidgetFactory.createLayout(c, bind, layout);
    }

    @OnEvent(VisibleEvent.class)
    static void onView(
            ComponentContext c,
            @Param String json,
            @Prop(optional = true) EventListener eventListener
    ) {
        if (eventListener != null && !TextUtils.isEmpty(json)) {
            eventListener.onEvent("report_view", json);
        }
    }

    @OnEvent(ClickEvent.class)
    static void onClick(
            ComponentContext c,
            @Param String click,
            @Param String json,
            @Prop(optional = true) EventListener eventListener
    ) {
        if (eventListener != null) {
            eventListener.onEvent("click", click);
            if (!TextUtils.isEmpty(json)) {
                eventListener.onEvent("report_click", json);
            }
        }
    }

    @OnEvent(TextChangedEvent.class)
    static void onTextChanged(
            ComponentContext c,
            @FromEvent String text,
            @Param String key,
            @Prop(optional = true) EventListener eventListener
    ) {
        if (eventListener != null) {
            eventListener.onEvent(key, text);
        }
    }
}
