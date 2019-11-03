package com.guet.flexbox.build

import com.facebook.litho.*
import com.facebook.yoga.YogaEdge
import com.facebook.yoga.YogaPositionType
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import kotlin.math.max

internal object FrameFactory : WidgetFactory<Row.Builder>() {

    private val exec = Executors.newFixedThreadPool(
            max(4, Runtime.getRuntime().availableProcessors())
    )

    override fun create(
            c: BuildContext,
            attrs: Map<String, String>): Row.Builder {
        return Row.create(c.componentContext)
                .apply {
                    applyDefault(c, attrs)
                }
    }

    override fun Row.Builder.applyChildren(
            c: BuildContext,
            attrs: Map<String, String>,
            children: List<Component.Builder<*>>
    ) {
        val width = c.tryGetValue(attrs["width"], Int::class.java, -1).toPx()
        val height = c.tryGetValue(attrs["height"], Int::class.java, -1).toPx()
        val widthSpec = if (width > 0) {
            SizeSpec.makeSizeSpec(width, SizeSpec.AT_MOST)
        } else {
            SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED)
        }
        val heightSpec = if (height > 0) {
            SizeSpec.makeSizeSpec(height, SizeSpec.AT_MOST)
        } else {
            SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED)
        }
        var maxWidth = 0
        var maxHeight = 0
        if (children.size > 1) {
            children.map {
                it.build()
            }.map {
                val context = c.componentContext
                Callable {
                    val size = Size()
                    it.measure(
                            context,
                            widthSpec,
                            heightSpec,
                            size
                    )
                    createWrapper(
                            context,
                            size.width,
                            size.height,
                            it
                    ) to size
                }
            }.map {
                exec.submit(it)
            }.forEach {
                val (row, size) = it.get()
                maxWidth = max(maxWidth, size.width)
                maxHeight = max(maxHeight, size.height)
                child(row)
            }
        } else {
            val context = c.componentContext
            val size = Size()
            val content = children.single().build()
            content.measure(
                    context,
                    widthSpec,
                    heightSpec,
                    size
            )
            maxWidth = max(maxWidth, size.width)
            maxHeight = max(maxHeight, size.height)
            child(createWrapper(context, size.width, size.height, content))
        }
        if (width < 0) {
            widthPx(maxWidth)
        }
        if (height < 0) {
            heightPx(maxHeight)
        }
    }

    private fun createWrapper(
            c: ComponentContext,
            width: Int,
            height: Int,
            inner: Component
    ): Wrapper {
        return Wrapper.create(c)
                .positionType(YogaPositionType.ABSOLUTE)
                .widthPx(width)
                .heightPx(height)
                .positionPx(YogaEdge.LEFT, 0)
                .positionPx(YogaEdge.TOP, 0)
                .delegate(inner)
                .build()
    }
}