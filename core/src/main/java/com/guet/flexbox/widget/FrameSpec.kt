package com.guet.flexbox.widget

import com.facebook.litho.*
import com.facebook.litho.annotations.LayoutSpec
import com.facebook.litho.annotations.OnCreateLayoutWithSizeSpec
import com.facebook.litho.annotations.Prop
import com.facebook.yoga.YogaEdge
import com.facebook.yoga.YogaPositionType
import kotlin.math.max

@LayoutSpec
object FrameSpec {
    @OnCreateLayoutWithSizeSpec
    fun onCreateLayoutWithSizeSpec(
            c: ComponentContext,
            widthSpec: Int,
            heightSpec: Int,
            @Prop(optional = true, varArg = "child") children: List<Component>?
    ): Component {
        var maxWidth = 0
        var maxHeight = 0
        val size = Size()
        (children ?: emptyList()).forEach {
            it.measure(
                    c,
                    SizeSpec.makeSizeSpec(0, widthSpec),
                    SizeSpec.makeSizeSpec(0, heightSpec),
                    size
            )
            maxHeight = max(maxHeight, size.height)
            maxWidth = max(maxWidth, size.width)
        }
        return Row.create(c)
                .widthPx(maxWidth)
                .heightPx(maxHeight)
                .apply {
                    (children ?: emptyList()).forEach {
                        child(Row.create(c)
                                .positionType(YogaPositionType.ABSOLUTE)
                                .positionPx(YogaEdge.TOP, 0)
                                .positionPx(YogaEdge.LEFT, 0)
                                .child(it))
                    }
                }
                .build()
    }
}