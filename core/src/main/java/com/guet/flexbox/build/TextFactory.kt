package com.guet.flexbox.build

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.text.Layout.Alignment
import android.text.TextUtils.TruncateAt.*
import android.view.View
import com.facebook.litho.ComponentContext
import com.facebook.litho.widget.Text
import com.facebook.litho.widget.VerticalGravity

internal object TextFactory : WidgetFactory<Text.Builder>() {

    private val invisibleColor = ColorStateList.valueOf(Color.TRANSPARENT)

    init {
        enumAttr("verticalGravity", mapOf(
                "top" to VerticalGravity.TOP,
                "bottom" to VerticalGravity.BOTTOM,
                "center" to VerticalGravity.CENTER
        )) { _, _, it ->
            verticalGravity(it)
        }
        @Suppress("NewApi")
        enumAttr("horizontalGravity", mapOf(
                "left" to Alignment.ALIGN_LEFT,
                "right" to Alignment.ALIGN_RIGHT,
                "center" to Alignment.ALIGN_CENTER
        ), Alignment.ALIGN_LEFT) { _, _, it ->
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
                        "start" to START,
                        "end" to END,
                        "middle" to MIDDLE,
                        "marquee" to MARQUEE
                )) { _, _, it ->
            ellipsize(it)
        }
    }

    override fun onCreateWidget(
            c: ComponentContext,
            dataBinding: DataContext,
            attrs: Map<String, String>?,
            visibility: Int
    ): Text.Builder {
        return Text.create(c)
    }

    override fun onLoadStyles(
            owner: Text.Builder,
            c: ComponentContext,
            dataBinding: DataContext,
            attrs: Map<String, String>?,
            visibility: Int
    ) {
        super.onLoadStyles(owner, c, dataBinding, attrs, visibility)
        if (visibility == View.INVISIBLE) {
            owner.textColor(Color.TRANSPARENT)
            owner.textColorStateList(invisibleColor)
        }
    }

}
