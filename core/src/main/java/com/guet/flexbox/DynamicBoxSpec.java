package com.guet.flexbox;


import com.facebook.litho.ClickEvent;
import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.VisibleEvent;
import com.facebook.litho.annotations.CachedValue;
import com.facebook.litho.annotations.LayoutSpec;
import com.facebook.litho.annotations.OnCalculateCachedValue;
import com.facebook.litho.annotations.OnCreateLayout;
import com.facebook.litho.annotations.OnEvent;
import com.facebook.litho.annotations.Param;
import com.facebook.litho.annotations.Prop;
import com.guet.flexbox.el.PropsELContext;

import java.util.List;

@LayoutSpec
final class DynamicBoxSpec {

    @OnCalculateCachedValue(name = "propsELContext")
    static PropsELContext onCreateELContext(
            @Prop(optional = true) Object data
    ) {
        return new PropsELContext(data);
    }

    @OnCreateLayout
    static Component onCreateLayout(
            ComponentContext c,
            @CachedValue PropsELContext propsELContext,
            @Prop NodeInfo layout
    ) {
        List<Component> components = propsELContext.inflate(c, layout);
        return components.isEmpty() ? null : components.get(0);
    }

    @OnEvent(VisibleEvent.class)
    static void onView(
            ComponentContext c,
            @Prop(optional = true) EventListener eventListener,
            @Param String report
    ) {
        if (eventListener != null) {
            eventListener.handleEvent("onView", new Object[]{report});
        }
    }

    @OnEvent(ClickEvent.class)
    static void onClick(
            ComponentContext c,
            @Prop(optional = true) EventListener eventListener,
            @Param String click,
            @Param String report
    ) {
        if (eventListener != null) {
            eventListener.handleEvent("onClick", new Object[]{click, report});
        }
    }
}
