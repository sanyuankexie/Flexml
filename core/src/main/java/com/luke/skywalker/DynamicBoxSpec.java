package com.luke.skywalker;


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
import com.luke.skywalker.el.LambdaExpression;
import com.luke.skywalker.el.PropsELContext;

import java.util.Collections;

@LayoutSpec
final class DynamicBoxSpec {

    @OnCalculateCachedValue(name = "propsELContext")
    static PropsELContext onCreateELContext(@Prop(optional = true) Object data) {
        return new PropsELContext(data);
    }

    @OnCalculateCachedValue(name = "sender")
    static EventSender onCreateSender(@Prop(optional = true) EventListener eventListener) {
        return new EventSender(eventListener);
    }

    @OnCreateLayout
    static Component onCreateLayout(
            ComponentContext c,
            @CachedValue PropsELContext propsELContext,
            @Prop NodeInfo layout
    ) {
        return propsELContext.inflate(c, layout);
    }


    @OnEvent(TextChangedEvent.class)
    static void onTextChanged(
            ComponentContext c,
            @FromEvent String text,
            @CachedValue PropsELContext propsELContext,
            @CachedValue EventSender sender,
            @Param LambdaExpression lambda
    ) {
        try {
            propsELContext.enterLambdaScope(
                    Collections.singletonMap(
                            "sender", sender
                    )
            );
            lambda.invoke(propsELContext, text);
        } finally {
            propsELContext.exitLambdaScope();
        }
    }

    @OnEvent(VisibleEvent.class)
    static void onView(
            ComponentContext c,
            @CachedValue PropsELContext propsELContext,
            @CachedValue EventSender sender,
            @Param LambdaExpression lambda
    ) {
        try {
            propsELContext.enterLambdaScope(
                    Collections.singletonMap(
                            "sender", sender
                    )
            );
            lambda.invoke(propsELContext);
        } finally {
            propsELContext.exitLambdaScope();
        }
    }

    @OnEvent(ClickEvent.class)
    static void onClick(
            ComponentContext c,
            @CachedValue PropsELContext propsELContext,
            @CachedValue EventSender sender,
            @Param LambdaExpression lambda
    ) {
        try {
            propsELContext.enterLambdaScope(
                    Collections.singletonMap(
                            "sender", sender
                    )
            );
            lambda.invoke(propsELContext);
        } finally {
            propsELContext.exitLambdaScope();
        }
    }
}
