package com.guet.flexbox.widget

import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.getChildMeasureSpec
import androidx.core.math.MathUtils
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.Row
import com.facebook.litho.Size
import com.facebook.litho.annotations.LayoutSpec
import com.facebook.litho.annotations.OnCreateLayoutWithSizeSpec
import com.facebook.litho.annotations.Prop
import com.facebook.yoga.YogaEdge
import com.facebook.yoga.YogaPositionType
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max

@LayoutSpec
object StackSpec {

    private val count = AtomicInteger(0)

    private val measureThreadPool = Executors.newFixedThreadPool(
            MathUtils.clamp(Runtime.getRuntime().availableProcessors(), 2, 4)) {
        Thread(it, "frame_thread${count.getAndIncrement()}")
    }

    @OnCreateLayoutWithSizeSpec
    fun onCreateLayoutWithSizeSpec(
            c: ComponentContext,
            widthSpec: Int,
            heightSpec: Int,
            @Prop(optional = true, varArg = "child") children: List<Component>?
    ): Component {
        val owner = Row.create(c)
        if (children.isNullOrEmpty()) {
            return owner.build()
        }
        val sizes = children.map {
            measureChild(c, it, widthSpec, heightSpec)
        }.map {
            it.get()
        }
        var maxHeight = 0
        var maxWidth = 0
        for (index in children.indices) {
            val size = sizes[index]
            maxWidth = max(maxWidth, size.width)
            maxHeight = max(maxHeight, size.height)
            owner.child(Row.create(c).positionType(YogaPositionType.ABSOLUTE)
                    .positionPx(YogaEdge.LEFT, 0)
                    .positionPx(YogaEdge.TOP, 0)
                    .child(children[index])
                    .build())
        }
        return owner.widthPx(maxWidth)
                .heightPx(maxHeight)
                .build()
    }

    private fun measureChild(
            c: ComponentContext,
            child: Component,
            parentWidthMeasureSpec: Int,
            parentHeightMeasureSpec: Int
    ): Future<Size> {
        return measureThreadPool.submit<Size> {
            val size = Size()
            val childWidthMeasureSpec = getChildMeasureSpec(
                    parentWidthMeasureSpec,
                    0,
                    LayoutParams.WRAP_CONTENT
            )
            val childHeightMeasureSpec = getChildMeasureSpec(
                    parentHeightMeasureSpec,
                    0,
                    LayoutParams.WRAP_CONTENT
            )
            child.measure(c, childWidthMeasureSpec, childHeightMeasureSpec, size)
            return@submit size
        }
    }
}