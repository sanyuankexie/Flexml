package com.guet.flexbox.litho

import android.graphics.Typeface
import android.text.TextUtils.TruncateAt
import com.facebook.litho.ComponentContext
import com.facebook.litho.widget.TextInput
import com.guet.flexbox.TextStyle
import com.guet.flexbox.build.AttributeSet
import com.guet.flexbox.build.EventHandler

internal object ToTextInput : ToComponent<TextInput.Builder>(Common) {
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
            typeface(Typeface.defaultFromStyle(value.mapValue()))
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