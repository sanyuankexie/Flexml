package com.luke.skywalker.build

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.text.Layout
import android.text.TextUtils
import android.view.View
import com.facebook.litho.ComponentContext
import com.facebook.litho.widget.Text
import com.facebook.litho.widget.VerticalGravity
import com.luke.skywalker.el.PropsELContext

internal object TextFactory : WidgetFactory<Text.Builder>(
        AttributeSet {
            enumAttr("verticalGravity", mapOf(
                    "top" to VerticalGravity.TOP,
                    "bottom" to VerticalGravity.BOTTOM,
                    "center" to VerticalGravity.CENTER
            )) { _, _, it ->
                verticalGravity(it)
            }
            @Suppress("NewApi")
            enumAttr("horizontalGravity", mapOf(
                    "left" to Layout.Alignment.ALIGN_LEFT,
                    "right" to Layout.Alignment.ALIGN_RIGHT,
                    "center" to Layout.Alignment.ALIGN_CENTER
            ), Layout.Alignment.ALIGN_LEFT) { _, _, it ->
                textAlignment(it)
            }
            textAttr("text") { _, _, it ->
                this.text(it)
            }
            boolAttr("clipToBounds") { _, _, it ->
                this.clipToBounds(it)
            }
            numberAttr("maxLines", Int.MAX_VALUE) { _, _, it ->
                this.maxLines(it)
            }
            numberAttr("minLines", Int.MIN_VALUE) { _, _, it ->
                this.minLines(it)
            }
            colorAttr("textColor") { _, _, it ->
                this.textColor(it)
            }
            numberAttr("textSize", 13.0) { _, _, it ->
                this.textSizePx(it.toPx())
            }
            scopeAttr("textStyle",
                    mapOf(
                            "normal" to Typeface.NORMAL,
                            "bold" to Typeface.BOLD
                    ),
                    Typeface.NORMAL
            ) { _, _, it ->
                this.typeface(Typeface.defaultFromStyle(it))
            }
            enumAttr("ellipsize",
                    mapOf(
                            "start" to TextUtils.TruncateAt.START,
                            "end" to TextUtils.TruncateAt.END,
                            "middle" to TextUtils.TruncateAt.MIDDLE,
                            "marquee" to TextUtils.TruncateAt.MARQUEE
                    )) { _, _, it ->
                ellipsize(it)
            }
        }
) {

    private val invisibleColor = ColorStateList.valueOf(Color.TRANSPARENT)

    override fun onCreateWidget(
            c: ComponentContext,
            data: PropsELContext,
            attrs: Map<String, String>?,
            visibility: Int
    ): Text.Builder {
        return Text.create(c)
    }

    override fun onLoadStyles(
            owner: Text.Builder,
            c: ComponentContext,
            data: PropsELContext,
            attrs: Map<String, String>?,
            visibility: Int
    ) {
        super.onLoadStyles(owner, c, data, attrs, visibility)
        if (visibility == View.INVISIBLE) {
            owner.textColor(Color.TRANSPARENT)
            owner.textColorStateList(invisibleColor)
        }
    }

}
