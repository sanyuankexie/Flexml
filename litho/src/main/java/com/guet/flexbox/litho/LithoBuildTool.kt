package com.guet.flexbox.litho

import android.content.Context
import android.util.ArrayMap
import com.facebook.yoga.YogaNodeManager
import com.guet.flexbox.build.*
import com.guet.flexbox.litho.drawable.load.DrawableLoaderModule
import com.guet.flexbox.litho.factories.*
import com.guet.flexbox.litho.widget.ComponentTreePool

object LithoBuildTool : BuildTool() {

    override val widgets: Map<String, ToWidget> by lazy {
        val arr = arrayOf(
                "Empty" to ToWidget(Empty, ToEmpty),
                "Flex" to ToWidget(Flex, ToFlex),
                "Banner" to ToWidget(Banner, ToBanner),
                "Image" to ToWidget(Image, ToImage),
                "Scroller" to ToWidget(Scroller, ToScroller),
                "TextInput" to ToWidget(TextInput, ToTextInput),
                "Text" to ToWidget(Text, ToText),
                "Stack" to ToWidget(CommonDefine, ToStack),
                "for" to ToWidget(For, null),
                "foreach" to ToWidget(ForEach, null),
                "when" to ToWidget(When, null),
                "if" to ToWidget(If, null)
        )
        arr.toMap(ArrayMap<String, ToWidget>(arr.size))
    }

    override val kits: List<BuildKit> by lazy {
        return@lazy listOf(
                YogaNodeManager,
                DrawableLoaderModule,
                ComponentTreePool
        )
    }

    @JvmStatic
    fun init(c: Context) {
        install(c)
    }

}