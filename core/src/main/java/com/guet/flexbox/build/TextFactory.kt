package com.guet.flexbox.build

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.text.Layout.Alignment
import android.text.TextUtils.TruncateAt.*
import android.view.View
import com.facebook.litho.widget.Text
import com.facebook.litho.widget.VerticalGravity

internal object TextFactory : WidgetFactory<Text.Builder>() {

    private val invisibleColor = ColorStateList.valueOf(Color.TRANSPARENT)

    init {
        flagsAttr("textAlign",
                mapOf(
                        "centerHorizontal" to 1.shl(1),
                        "left" to 1.shl(2),
                        "right" to 1.shl(3),
                        "centerVertical" to 1.shl(4),
                        "top" to 1.shl(5),
                        "bottom" to 1.shl(6),
                        "center" to (1.shl(1) or 1.shl(4))
                )
        ) { _, set ->
            when {
                set[1] -> {
                    textAlignment(Alignment.ALIGN_CENTER)
                }
                set[2] -> {
                    textAlignment(Alignment.valueOf("ALIGN_LEFT"))
                }
                set[3] -> {
                    textAlignment(Alignment.valueOf("ALIGN_RIGHT"))
                }
                set[4] -> {
                    verticalGravity(VerticalGravity.CENTER)
                }
                set[5] -> {
                    verticalGravity(VerticalGravity.TOP)
                }
                set[6] -> {
                    verticalGravity(VerticalGravity.BOTTOM)
                }
            }
        }
        textAttr("text") { _, it ->
            this.text(it)
        }
        boolAttr("clipToBounds") { _, it ->
            this.clipToBounds(it)
        }
        numberAttr("maxLines", Int.MAX_VALUE) { _, it ->
            this.maxLines(it)
        }
        numberAttr("minLines", Int.MIN_VALUE) { _, it ->
            this.minLines(it)
        }
        colorAttr("textColor") { _, it ->
            this.textColor(it)
        }
        numberAttr("textSize", 13.0) { _, it ->
            this.textSizePx(it.toPx())
        }
        scopeAttr("textStyle",
                mapOf(
                        "normal" to Typeface.NORMAL,
                        "bold" to Typeface.BOLD
                ),
                Typeface.NORMAL
        ) { _, it ->
            this.typeface(Typeface.defaultFromStyle(it))
        }
        enumAttr("ellipsize",
                mapOf(
                        "start" to START,
                        "end" to END,
                        "middle" to MIDDLE,
                        "marquee" to MARQUEE
                )) { _, it ->
            ellipsize(it)
        }
    }

    override fun onCreateWidget(
            c: BuildContext,
            attrs: Map<String, String>?,
            visibility: Int
    ): Text.Builder {
        return Text.create(c.componentContext)
    }

    override fun onLoadStyles(
            owner: Text.Builder,
            c: BuildContext,
            attrs: Map<String, String>?,
            visibility: Int
    ) {
        super.onLoadStyles(owner, c, attrs, visibility)
        if (visibility == View.INVISIBLE) {
            owner.textColor(Color.TRANSPARENT)
            owner.textColorStateList(invisibleColor)
        }
    }

}
