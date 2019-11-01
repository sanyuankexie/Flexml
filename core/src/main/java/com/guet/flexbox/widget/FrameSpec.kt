package com.guet.flexbox.widget

import android.view.ViewGroup
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.Row
import com.facebook.litho.Size
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
        val list = children?.map {
            it.measure(
                    c,
                    ViewGroup.getChildMeasureSpec(widthSpec,
                            0, ViewGroup.LayoutParams.WRAP_CONTENT),
                    ViewGroup.getChildMeasureSpec(heightSpec,
                            0, ViewGroup.LayoutParams.WRAP_CONTENT),
                    size
            )
            maxHeight = max(maxHeight, size.height)
            maxWidth = max(maxWidth, size.width)
            Row.create(c).positionType(YogaPositionType.ABSOLUTE)
                    .widthPx(size.width)
                    .heightPx(size.height)
                    .positionPx(YogaEdge.LEFT, 0)
                    .positionPx(YogaEdge.TOP, 0)
                    .child(it)
        }
        return Row.create(c)
                .widthPx(maxWidth)
                .heightPx(maxHeight)
                .apply {
                    list?.forEach {
                        child(it)
                    }
                }.build()
    }
}