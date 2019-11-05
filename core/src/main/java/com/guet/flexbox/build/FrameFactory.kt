package com.guet.flexbox.build

import com.facebook.litho.Component
import com.facebook.litho.Row
import com.facebook.litho.Size
import com.facebook.litho.SizeSpec
import com.facebook.yoga.YogaEdge
import com.facebook.yoga.YogaPositionType
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max

internal object FrameFactory : WidgetFactory<Row.Builder>() {

    private val count = AtomicInteger(0)

    private val measureThreadPool = Executors.newCachedThreadPool {
        Thread(it, "Frame:Measure_${count.getAndIncrement()}")
    }

    override fun onCreate(
            c: BuildContext,
            attrs: Map<String, String>,
            visibility: Int
    ): Row.Builder {
        return Row.create(c.componentContext)
    }

    override fun Row.Builder.onApplyChildren(
            c: BuildContext,
            attrs: Map<String, String>,
            children: List<Component.Builder<*>>,
            visibility: Int
    ) {
        val context = c.componentContext
        var width = c.tryGetValue(attrs["width"], Int::class.java, Int.MIN_VALUE)
        var height = c.tryGetValue(attrs["height"], Int::class.java, Int.MIN_VALUE)
        val wrappers = children.map {
            Row.create(context)
                    .positionType(YogaPositionType.ABSOLUTE)
                    .positionPx(YogaEdge.LEFT, 0)
                    .positionPx(YogaEdge.TOP, 0)
                    .child(it)
                    .build()
        }
        if (width > 0 && height > 0) {
            wrappers.forEach {
                child(it)
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
            //concurrent measure
            var maxWidth = 0
            var maxHeight = 0
            if (children.isNotEmpty()) {
                var futures: List<Future<Pair<Component, Size>>>? = null
                if (children.size > 1) {
                    futures = wrappers.subList(1, children.size)
                            .map {
                                measureThreadPool.submit<Pair<Component, Size>> {
                                    val s = Size()
                                    it.measure(
                                            context,
                                            widthSpec,
                                            heightSpec,
                                            s
                                    )
                                    it to s
                                }
                            }
                }
                val size = Size()
                val first = wrappers.first()
                first.measure(
                        context,
                        widthSpec,
                        heightSpec,
                        size
                )
                maxWidth = max(maxWidth, size.width)
                maxHeight = max(maxHeight, size.height)
                child(first)
                futures?.forEach {
                    val (wrapper, s) = it.get()
                    maxWidth = max(maxWidth, s.width)
                    maxHeight = max(maxHeight, s.height)
                    child(wrapper)
                }
            }
            if (width <= 0) {
                widthPx(maxWidth)
            }
            if (height <= 0) {
                heightPx(maxHeight)
            }
        }
    }
}