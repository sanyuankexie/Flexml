package com.guet.flexbox.litho.factories

import android.graphics.Typeface
import android.text.TextUtils.TruncateAt
import com.facebook.litho.ComponentContext
import com.facebook.litho.widget.TextInput
import com.guet.flexbox.event.EventHandler
import com.guet.flexbox.build.AttributeSet
import com.guet.flexbox.enums.TextStyle
import com.guet.flexbox.litho.EventAdapter
import com.guet.flexbox.litho.resolve.AttributeAssignSet
import com.guet.flexbox.litho.resolve.mapping
import com.guet.flexbox.litho.toPx

internal object ToTextInput : ToComponent<TextInput.Builder>(CommonAssigns) {
    override val attributeAssignSet: AttributeAssignSet<TextInput.Builder> by com.guet.flexbox.litho.resolve.create {
        register("maxLines") { _, _, value: Float ->
            maxLines(value.toInt())
        }
        register("minLines") { _, _, value: Float ->
            minLines(value.toInt())
        }
        register("textSize") { _, _, value: Float ->
            textSizePx(value.toPx())
        }
        register("textStyle") { _, _, value: TextStyle ->
            typeface(Typeface.defaultFromStyle(value.mapping()))
        }
        register("ellipsize") { _, _, value: TruncateAt ->
            ellipsize(value)
        }
        register("onTextChanged") { _, _, value: EventHandler ->
            textChangedEventHandler(EventAdapter<Any>(value))
        }
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