package com.guet.flexbox;


import com.facebook.litho.ClickEvent;
import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.VisibleEvent;
import com.facebook.litho.annotations.CachedValue;
import com.facebook.litho.annotations.FromEvent;
import com.facebook.litho.annotations.LayoutSpec;
import com.facebook.litho.annotations.OnCalculateCachedValue;
import com.facebook.litho.annotations.OnCreateLayout;
import com.facebook.litho.annotations.OnEvent;
import com.facebook.litho.annotations.Param;
import com.facebook.litho.annotations.Prop;
import com.facebook.litho.widget.TextChangedEvent;
import com.guet.flexbox.el.LambdaExpression;
import com.guet.flexbox.el.PropsELContext;

import java.util.List;

@LayoutSpec
final class DynamicBoxSpec {

    @OnCalculateCachedValue(name = "propsELContext")
    static PropsELContext onCreateELContext(
            @Prop(optional = true) Object data,
            @Prop(optional = true) EventListener eventListener
    ) {
        return new PropsELContext(data, eventListener);
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
            @Param LambdaExpression report
    ) {
        report.invoke();
    }

    @OnEvent(ClickEvent.class)
    static void onClick(
            ComponentContext c,
            @Param LambdaExpression click
    ) {
        click.invoke();
    }

    @OnEvent(TextChangedEvent.class)
    static void onTextChanged(
            ComponentContext c,
            @FromEvent String text,
            @Param LambdaExpression lambda
    ) {
        lambda.invoke(text);
    }
}
