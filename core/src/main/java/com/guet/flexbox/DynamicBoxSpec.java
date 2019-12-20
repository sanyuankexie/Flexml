package com.guet.flexbox;


import android.util.Log;

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
import com.guet.flexbox.build.Inflater;
import com.guet.flexbox.content.RenderContent;
import com.guet.flexbox.el.LambdaExpression;

@LayoutSpec
final class DynamicBoxSpec {

    private static final String TAG = "DynamicBox";

    @OnCreateLayout
    static Component onCreateLayout(
            ComponentContext c,
            @Prop RenderContent content
    ) {
        Log.i(TAG, "onCreateLayout: " + Thread.currentThread().getName());
        return Inflater.INSTANCE.inflate(c, content);
    }


    @OnEvent(TextChangedEvent.class)
    static void onTextChanged(
            ComponentContext c,
            @FromEvent String text,
            @Param LambdaExpression lambda
    ) {
        lambda.invoke(text);
    }

    @OnEvent(VisibleEvent.class)
    static void onView(
            ComponentContext c,
            @Param LambdaExpression lambda
    ) {
        lambda.invoke();
    }

    @OnEvent(ClickEvent.class)
    static void onClick(
            ComponentContext c,
            @Param LambdaExpression lambda
    ) {
        lambda.invoke();
    }
}
