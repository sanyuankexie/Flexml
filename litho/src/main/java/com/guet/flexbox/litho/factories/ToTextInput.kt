package com.guet.flexbox.litho.factories

import android.graphics.Typeface
import android.text.TextUtils.TruncateAt
import com.facebook.litho.ComponentContext
import com.facebook.litho.widget.TextInput
import com.guet.flexbox.EventHandler
import com.guet.flexbox.build.AttributeSet
import com.guet.flexbox.enums.TextStyle
import com.guet.flexbox.litho.event.EventHandlerWrapper
import com.guet.flexbox.litho.resolve.mapping

internal object ToTextInput : ToComponent<TextInput.Builder>(CommonAssigns) {
    override val attributeAssignSet: AttributeAssignSet<TextInput.Builder> by create {
        register("maxLines") { _, _, value: Double ->
            maxLines(value.toInt())
        }
        register("minLines") { _, _, value: Double ->
            minLines(value.toInt())
        }
        register("textSize") { _, _, value: Double ->
            textSizePx(value.toInt())
        }
        register("textStyle") { _, _, value: TextStyle ->
            typeface(Typeface.defaultFromStyle(value.mapping()))
        }
        register("ellipsize") { _, _, value: TruncateAt ->
            ellipsize(value)
        }
        register("onTextChanged") { _, _, value: EventHandler ->
            textChangedEventHandler(EventHandlerWrapper<Any>(value))
        }
    }

    override fun create(
            c: ComponentContext,
            visibility: Boolean,
            attrs: AttributeSet
    ): TextInput.Builder {
        return TextInput.create(c)
    }
}