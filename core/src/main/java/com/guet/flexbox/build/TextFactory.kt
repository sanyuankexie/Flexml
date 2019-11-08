package com.guet.flexbox.build

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.text.Layout.Alignment.ALIGN_CENTER
import android.text.Layout.Alignment.valueOf
import android.text.TextUtils.TruncateAt.*
import android.view.View
import com.facebook.litho.widget.Text

internal object TextFactory : WidgetFactory<Text.Builder>() {

    private val invisibleColor = ColorStateList.valueOf(Color.TRANSPARENT)

    init {
        enumAttr("textAlign",
                mapOf(
                        "center" to ALIGN_CENTER,
                        "left" to valueOf("ALIGN_LEFT"),
                        "right" to valueOf("ALIGN_RIGHT")
                ),
                ALIGN_CENTER
        ) { _, it ->
            this.textAlignment(it)
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
