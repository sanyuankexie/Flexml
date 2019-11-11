package com.guet.flexbox;


import android.text.TextUtils;
import android.view.View;

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
import com.guet.flexbox.build.BuildContext;

@LayoutSpec
final class DynamicBoxSpec {

    @OnCreateLayout
    static Component onCreateLayout(ComponentContext c,
                                    @Prop NodeInfo layout,
                                    @Prop(optional = true) Object bind) {
        return new BuildContext(c, bind).createLayout(layout);
    }

    @OnEvent(VisibleEvent.class)
    static void onView(ComponentContext c,
                       @Param String json,
                       @Prop(optional = true) EventListener eventListener) {
        if (eventListener != null && !TextUtils.isEmpty(json)) {
            eventListener.onEvent(EventType.REPORT_VIEW, json);
        }
    }

    @OnEvent(ClickEvent.class)
    static void onClick(ComponentContext c,
                        @FromEvent View view,
                        @Param String click,
                        @Param String json,
                        @Prop(optional = true) EventListener eventListener) {
        if (eventListener != null) {
            eventListener.onEvent(EventType.CLICK, click);
            if (!TextUtils.isEmpty(json)) {
                eventListener.onEvent(EventType.REPORT_CLICK, json);
            }
        }
    }
}
