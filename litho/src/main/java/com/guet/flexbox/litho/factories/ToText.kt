package com.guet.flexbox.litho.factories

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.text.TextUtils.TruncateAt
import com.facebook.litho.ComponentContext
import com.facebook.litho.widget.Text
import com.guet.flexbox.build.AttributeSet
import com.guet.flexbox.enums.Horizontal
import com.guet.flexbox.enums.TextStyle
import com.guet.flexbox.enums.Vertical
import com.guet.flexbox.litho.resolve.mapping
import com.guet.flexbox.litho.toPx


internal object ToText : ToComponent<Text.Builder>(CommonAssigns) {

    private val invisibleColor = ColorStateList.valueOf(Color.TRANSPARENT)

    override val attributeAssignSet: AttributeAssignSet<Text.Builder> by create {
        register("verticalGravity") { _, _, value: Vertical ->
            verticalGravity(value.mapping())
        }
        register("horizontalGravity") { _, _, value: Horizontal ->
            textAlignment(value.mapping())
        }
        register("text") { display, _, value: String ->
            text(value)
            if (!display) {
                textColor(Color.TRANSPARENT)
                textColorStateList(invisibleColor)
            }
        }
        register("clipToBounds") { _, _, value: Boolean ->
            clipToBounds(value)
        }
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
        register("textColor") { display, _, value: Int ->
            if (display) {
                textColor(value)
            } else {
                textColor(Color.TRANSPARENT)
                textColorStateList(invisibleColor)
            }
        }
    }

    override fun create(
            c: ComponentContext,
            visibility: Boolean,
            attrs: AttributeSet
    ): Text.Builder {
        return Text.create(c)
    }
}