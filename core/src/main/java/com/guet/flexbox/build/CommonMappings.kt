package com.guet.flexbox.build

import com.facebook.litho.Component
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaEdge
import java.util.*

internal object CommonMappings : Mapper<Component.Builder<*>>() {

    private val common = Mappings<Component.Builder<*>>(8)

    override val mappings: Mappings<Component.Builder<*>>
        get() = common

    private val edges = arrayOf("Left", "Right", "Top", "Bottom")

    init {
        numberAttr<Double>("borderWidth") { _, _, it ->
            this.widthPx(it.toPx())
        }
        numberAttr<Double>("height") { _, _, it ->
            this.heightPx(it.toPx())
        }
        numberAttr<Float>("flexGrow") { _, _, it ->
            this.flexGrow(it)
        }
        numberAttr<Float>("flexShrink") { _, _, it ->
            this.flexShrink(it)
        }
        enumAttr("alignSelf",
                mapOf(
                        "auto" to YogaAlign.AUTO,
                        "flexStart" to YogaAlign.FLEX_START,
                        "flexEnd" to YogaAlign.FLEX_END,
                        "center" to YogaAlign.CENTER,
                        "baseline" to YogaAlign.BASELINE,
                        "stretch" to YogaAlign.STRETCH
                )
        ) { _, _, it ->
            this.alignSelf(it)
        }
        numberAttr<Double>("margin") { _, _, it ->
            this.marginPx(YogaEdge.ALL, it.toPx())
        }
        numberAttr<Double>("padding") { _, _, it ->
            this.paddingPx(YogaEdge.ALL, it.toPx())
        }
        for (index in edges.indices) {
            val yogaEdge = YogaEdge.valueOf(edges[index].toUpperCase(Locale.US))
            numberAttr<Double>("margin" + edges[index]) { map, _, it ->
                if (!map.containsKey("margin")) {
                    this.marginPx(yogaEdge, it.toPx())
                }
            }
            numberAttr<Double>("padding" + edges[index]) { map, _, it ->
                if (!map.containsKey("padding")) {
                    this.paddingPx(yogaEdge, it.toPx())
                }
            }
        }
    }

    internal fun <T> createByType(): Lazy<Mappings<T>> {
        @Suppress("UNCHECKED_CAST")
        return lazyOf(Mappings(common as Mappings<T>))
    }
}