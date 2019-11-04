package com.guet.flexbox.build

import com.facebook.litho.*
import com.facebook.yoga.YogaEdge
import com.facebook.yoga.YogaPositionType
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max
import kotlin.math.min

internal object FrameFactory : WidgetFactory<Row.Builder>() {

    private val count = AtomicInteger(0)

    private val measureThreadPool = Executors.newFixedThreadPool(
            min(4, Runtime.getRuntime().availableProcessors())
    ) {
        Thread(it, "frame-measure-${count.getAndIncrement()}")
    }

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
        val context = c.componentContext
        var width = c.tryGetValue(attrs["width"], Int::class.java, Int.MIN_VALUE)
        var height = c.tryGetValue(attrs["height"], Int::class.java, Int.MIN_VALUE)
        if (width > 0 && height > 0) {
            children.forEach {
                child(it.positionType(YogaPositionType.ABSOLUTE)
                        .widthPx(width)
                        .heightPx(height))
            }
        } else {
            width = width.toPx()
            height = height.toPx()
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
            if (children.isNotEmpty()) {
                val futures = children
                        .subList(1, children.size - 1)
                        .map {
                            val component = it.build()
                            measureThreadPool.submit(Callable {
                                val s = Size()
                                component.measure(
                                        context,
                                        widthSpec,
                                        heightSpec,
                                        s
                                )
                                createWrapper(
                                        context,
                                        s.width,
                                        s.height,
                                        component
                                ) to s
                            })
                        }
                val size = Size()
                val content = children.first().build()
                content.measure(
                        context,
                        widthSpec,
                        heightSpec,
                        size
                )
                maxWidth = max(maxWidth, size.width)
                maxHeight = max(maxHeight, size.height)
                child(createWrapper(
                        context,
                        size.width,
                        size.height,
                        content
                ))
                futures.forEach {
                    val (wrapper, s) = it.get()
                    maxWidth = max(maxWidth, s.width)
                    maxHeight = max(maxHeight, s.height)
                    child(wrapper)
                }
            }
            if (width < 0) {
                widthPx(maxWidth)
            }
            if (height < 0) {
                heightPx(maxHeight)
            }
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