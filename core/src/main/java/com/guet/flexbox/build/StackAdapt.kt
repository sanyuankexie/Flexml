package com.guet.flexbox.build

import androidx.core.math.MathUtils
import com.facebook.litho.*
import com.facebook.yoga.YogaEdge
import com.facebook.yoga.YogaPositionType
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max

internal object StackAdapt : ComponentAdapt<Component.ContainerBuilder<*>>(CommonAdapt), ThreadFactory {

    private val count = AtomicInteger(0)

    private val measureThreadPool = Executors.newFixedThreadPool(
            MathUtils.clamp(Runtime.getRuntime().availableProcessors(), 2, 4),
            this
    )

    override val attributeSet: AttributeSet<Component.ContainerBuilder<*>>
        get() = emptyMap()

    override fun onCreate(c: ComponentContext, type: String, visibility: Boolean, attrs: Map<String, Any>): Component.ContainerBuilder<*> {
        return Row.create(c)
    }

    override fun onInstallChildren(owner: Component.ContainerBuilder<*>, type: String, visibility: Boolean, attrs: Map<String, Any>, children: List<Component>) {
        if (children.isEmpty()) {
            return
        }
        var width = (attrs.getOrElse("width") { Int.MIN_VALUE } as Number).toInt()
        var height = (attrs.getOrElse("height") { Int.MIN_VALUE } as Number).toInt()
        val wrappers = children.map {
            Row.create(owner.getContext())
                    .positionType(YogaPositionType.ABSOLUTE)
                    .positionPx(YogaEdge.LEFT, 0)
                    .positionPx(YogaEdge.TOP, 0)
                    .child(it)
                    .build()
        }
        wrappers.forEach {
            owner.child(it)
        }
        if (width > 0 && height > 0) {
            return
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
            var futures: List<Future<Size>>? = null
            if (children.size > 1) {
                val componentContext = owner.getContext()
                futures = wrappers.subList(1, children.size).map {
                    measureThreadPool.submit<Size> {
                        val size = Size()
                        it.measure(
                                componentContext,
                                widthSpec,
                                heightSpec,
                                size
                        )
                        size
                    }
                }
            }
            val size = Size()
            wrappers.first().measure(
                    owner.getContext(),
                    widthSpec,
                    heightSpec,
                    size
            )
            maxWidth = max(maxWidth, size.width)
            maxHeight = max(maxHeight, size.height)
            futures?.map {
                it.get()
            }?.forEach {
                maxWidth = max(maxWidth, it.width)
                maxHeight = max(maxHeight, it.height)
            }
            if (width <= 0) {
                owner.widthPx(maxWidth)
            }
            if (height <= 0) {
                owner.heightPx(maxHeight)
            }
        }
    }

    override fun newThread(r: Runnable): Thread {
        return Thread(r, "frame_thread${count.getAndIncrement()}")
    }

}