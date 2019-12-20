package com.guet.flexbox


import android.util.Log
import androidx.annotation.RestrictTo
import com.facebook.litho.ClickEvent
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.VisibleEvent
import com.facebook.litho.annotations.*
import com.facebook.litho.widget.TextChangedEvent
import com.guet.flexbox.build.*
import com.guet.flexbox.content.RenderNode
import com.guet.flexbox.el.LambdaExpression

@RestrictTo(RestrictTo.Scope.LIBRARY)
@LayoutSpec
internal object RendererSpec {

    private val TAG = "PageHostView"

    private fun inflate(c: ComponentContext, renderNode: RenderNode): Component? {
        return widgets[renderNode.type]?.create(
                c,
                renderNode,
                renderNode.children.mapNotNull {
                    inflate(c, it)
                }
        )
    }

    private val widgets = mapOf(
            "Flex" to Flex,
            "Empty" to Empty,
            "Image" to Image,
            "Native" to Native,
            "Scroller" to Scroller,
            "Stack" to Stack,
            "Text" to Text,
            "TextInput" to TextInput
    )

    @OnCreateLayout
    fun onCreateLayout(
            c: ComponentContext,
            @Prop content: RenderNode
    ): Component? {
        Log.i(TAG, "onCreateLayout: " + Thread.currentThread().name)
        return inflate(c, content)
    }


    @OnEvent(TextChangedEvent::class)
    fun onTextChanged(
            c: ComponentContext,
            @FromEvent text: String,
            @Param lambda: LambdaExpression
    ) {
        lambda.invoke(text)
    }

    @OnEvent(VisibleEvent::class)
    fun onView(
            c: ComponentContext,
            @Param lambda: LambdaExpression
    ) {
        lambda.invoke()
    }

    @OnEvent(ClickEvent::class)
    fun onClick(
            c: ComponentContext,
            @Param lambda: LambdaExpression
    ) {
        lambda.invoke()
    }
}
