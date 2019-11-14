package com.guet.flexbox.build

import android.view.View
import androidx.annotation.RestrictTo
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.NodeInfo

internal interface Transform {

    fun transform(
            c: ComponentContext,
            dataBinding: DataBinding,
            nodeInfo: NodeInfo,
            upperVisibility: Int
    ): List<Component>

    companion object {

        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        fun createLayout(
                context: ComponentContext,
                dataBinding: DataBinding,
                root: NodeInfo
        ): Component {
            return createFromElement(context, dataBinding, root).single()
        }

        internal fun createFromElement(
                context: ComponentContext,
                dataBinding: DataBinding,
                element: NodeInfo,
                upperVisibility: Int = View.VISIBLE
        ): List<Component> {
            return transforms[element.type]?.transform(
                    context,
                    dataBinding,
                    element,
                    upperVisibility
            ) ?: emptyList()
        }

        private val transforms = mapOf(
                "Image" to ImageFactory,
                "Flex" to FlexFactory,
                "Text" to TextFactory,
                "Frame" to FrameFactory,
                "Native" to NativeFactory,
                "Scroller" to ScrollerFactory,
                "Empty" to EmptyFactory,
                "for" to ForBehavior,
                "foreach" to ForEachBehavior,
                "if" to IfBehavior
        )
    }
}