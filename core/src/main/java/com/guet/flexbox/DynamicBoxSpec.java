package com.guet.flexbox;


import com.facebook.litho.ClickEvent;
import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.StateValue;
import com.facebook.litho.VisibleEvent;
import com.facebook.litho.annotations.FromEvent;
import com.facebook.litho.annotations.LayoutSpec;
import com.facebook.litho.annotations.OnCreateInitialState;
import com.facebook.litho.annotations.OnCreateLayout;
import com.facebook.litho.annotations.OnEvent;
import com.facebook.litho.annotations.Param;
import com.facebook.litho.annotations.Prop;
import com.facebook.litho.annotations.State;
import com.facebook.litho.widget.TextChangedEvent;
import com.guet.flexbox.build.PagerContext;
import com.guet.flexbox.el.LambdaExpression;

import java.util.List;

@LayoutSpec
final class DynamicBoxSpec {

    @OnCreateInitialState
    static void onCreateInitialState(
            ComponentContext c,
            @Prop(optional = true) Object data,
            @Prop(optional = true) EventListener eventListener,
            StateValue<PagerContext> pagerContext
    ) {
        pagerContext.set(new PagerContext(data, eventListener));
    }

    @OnCreateLayout
    static Component onCreateLayout(
            ComponentContext c,
            @State PagerContext pagerContext,
            @Prop NodeInfo layout
    ) {
        List<Component> components = pagerContext.inflate(c, layout);
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
            @Param LambdaExpression click,
            @Param LambdaExpression report
    ) {
        click.invoke();
        if (report != null) {
            report.invoke();
        }
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
