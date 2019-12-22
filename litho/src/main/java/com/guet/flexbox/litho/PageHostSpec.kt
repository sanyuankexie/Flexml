package com.guet.flexbox.litho


import android.util.Log
import androidx.annotation.RestrictTo
import com.facebook.litho.ClickEvent
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.VisibleEvent
import com.facebook.litho.annotations.*
import com.facebook.litho.widget.TextChangedEvent
import com.guet.flexbox.content.RenderNode
import com.guet.flexbox.el.ELContext
import com.guet.flexbox.el.LambdaExpression
import com.guet.flexbox.litho.build.*

@RestrictTo(RestrictTo.Scope.LIBRARY)
@LayoutSpec
internal object PageHostSpec {

    private const val TAG = "PageHost"

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
            @Prop content: RenderNode,
            @Prop(optional = true) tag: String?
    ): Component? {
        Log.i(TAG, "onCreateLayout: thread=" + Thread.currentThread().name + " tag=" + tag)
        return inflate(c, content)
    }


    @OnEvent(TextChangedEvent::class)
    fun onTextChanged(
            c: ComponentContext,
            @FromEvent text: String,
            @Prop elContext: ELContext,
            @Param lambda: LambdaExpression
    ) {
        lambda.invoke(elContext, text)
    }

    @OnEvent(VisibleEvent::class)
    fun onView(
            c: ComponentContext,
            @Prop elContext: ELContext,
            @Param lambda: LambdaExpression
    ) {
        lambda.invoke(elContext)
    }

    @OnEvent(ClickEvent::class)
    fun onClick(
            c: ComponentContext,
            @Prop elContext: ELContext,
            @Param lambda: LambdaExpression
    ) {
        lambda.invoke(elContext)
    }
}
