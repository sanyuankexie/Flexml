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
                        "centerHorizontal" to 0b0011,
                        "left" to 0b0001,
                        "right" to 0b0010,
                        "centerVertical" to 0b1100,
                        "top" to 0b0100,
                        "bottom" to 0b0100,
                        "center" to 0b1111
                )
        ) { _, _, set ->
            set.hasFlags(0b0011) {
                textAlignment(Alignment.ALIGN_CENTER)
            }
            set.hasFlags(0b0001) {
                textAlignment(Alignment.valueOf("ALIGN_LEFT"))
            }
            set.hasFlags(0b0010) {
                textAlignment(Alignment.valueOf("ALIGN_RIGHT"))
            }
            set.hasFlags(0b1100) {
                verticalGravity(VerticalGravity.CENTER)
            }
            set.hasFlags(0b0100) {
                verticalGravity(VerticalGravity.TOP)
            }
            set.hasFlags(0b1000) {
                verticalGravity(VerticalGravity.BOTTOM)
            }
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
                        "start" to START,
                        "end" to END,
                        "middle" to MIDDLE,
                        "marquee" to MARQUEE
                )) { _, _, it ->
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
