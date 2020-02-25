package com.guet.flexbox.litho.factories

import android.graphics.Typeface
import com.facebook.litho.ComponentContext
import com.facebook.litho.widget.TextInput
import com.guet.flexbox.build.AttributeSet
import com.guet.flexbox.enums.TextStyle
import com.guet.flexbox.litho.resolve.Assignment
import com.guet.flexbox.litho.resolve.AttrsAssigns
import com.guet.flexbox.litho.resolve.EnumMappings

internal object ToTextInput : ToComponent<TextInput.Builder>() {
    override val attrsAssigns by AttrsAssigns
            .create<TextInput.Builder>(CommonAssigns.attrsAssigns) {
                value("maxLines", TextInput.Builder::maxLines)
                value("minLines", TextInput.Builder::minLines)
                pt("textSize", TextInput.Builder::textSizePx)
                enum("ellipsize", TextInput.Builder::ellipsize)
                event("onTextChanged", TextInput.Builder::textChangedEventHandler)
                register("textStyle", object : Assignment<TextInput.Builder, TextStyle> {
                    override fun assign(c: TextInput.Builder, display: Boolean, other: Map<String, Any>, value: TextStyle) {
                        c.typeface(Typeface.defaultFromStyle(EnumMappings.get(value)))
                    }
                })
            }

    override fun create(
            c: ComponentContext,
            visibility: Boolean,
            attrs: AttributeSet
    ): TextInput.Builder {
        return TextInput.create(c)
                .inputBackground(null)
    }
}