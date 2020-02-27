package com.guet.flexbox.litho.factories

import com.facebook.litho.ComponentContext
import com.facebook.litho.widget.Text
import com.guet.flexbox.build.PropSet
import com.guet.flexbox.litho.factories.filler.PropsFiller
import com.guet.flexbox.litho.factories.filler.TextColorFiller
import com.guet.flexbox.litho.factories.filler.TextFiller


internal object ToText : ToComponent<Text.Builder>() {

    override val propsFiller by PropsFiller
            .create<Text.Builder>(CommonProps) {
                enum("verticalGravity", Text.Builder::verticalGravity)
                enum("horizontalGravity", Text.Builder::textAlignment)
                bool("clipToBounds", Text.Builder::clipToBounds)
                value("maxLines", Text.Builder::maxLines)
                value("minLines", Text.Builder::minLines)
                pt("textSize", Text.Builder::textSizePx)
                enum("ellipsize", Text.Builder::ellipsize)
                textStyle("textStyle", Text.Builder::typeface)
                register("textColor", TextColorFiller)
                register("text", TextFiller)
            }

    override fun create(
            c: ComponentContext,
            visibility: Boolean,
            attrs: PropSet
    ): Text.Builder {
        return Text.create(c)
    }

}