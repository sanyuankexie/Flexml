package com.guet.flexbox.litho

import android.graphics.Typeface
import android.text.TextUtils.TruncateAt
import com.facebook.litho.ComponentContext
import com.facebook.litho.EventHandler
import com.facebook.litho.widget.TextInput

internal object ToTextInput : ToComponent<TextInput.Builder>(Common) {
    override val attributeSet: AttributeSet<TextInput.Builder> by create {
        register("maxLines") { _, _, value: Double ->
            maxLines(value.toInt())
        }
        register("minLines") { _, _, value: Double ->
            minLines(value.toInt())
        }
        register("textSize") { _, _, value: Double ->
            textSizePx(value.toInt())
        }
        register("textStyle") { _, _, value: Int ->
            typeface(Typeface.defaultFromStyle(value))
        }
        register("ellipsize") { _, _, value: TruncateAt ->
            ellipsize(value)
        }
        register("onTextChanged") { _, _, value: EventHandler<*> ->
            textChangedEventHandler(value)
        }
    }

    override fun create(c: ComponentContext, visibility: Boolean, attrs: Map<String, Any>): TextInput.Builder {
        return TextInput.create(c)
    }
}