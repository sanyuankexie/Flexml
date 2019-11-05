package com.guet.flexbox.build

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.text.Layout.Alignment.ALIGN_CENTER
import android.text.Layout.Alignment.valueOf
import android.view.View
import com.facebook.litho.widget.Text

internal object TextFactory : WidgetFactory<Text.Builder>() {

    init {
        bound("textAlign",
                ALIGN_CENTER,
                mapOf(
                        "center" to ALIGN_CENTER,
                        "left" to valueOf("ALIGN_LEFT"),
                        "right" to valueOf("ALIGN_RIGHT")
                )
        ) { _, it ->
            this.textAlignment(it)
        }
        text("text") { _, it ->
            this.text(it)
        }
        bool("clipToBounds") { _, it ->
            this.clipToBounds(it)
        }
        value("maxLines", Int.MAX_VALUE.toDouble()) { _, it ->
            this.maxLines(it.toInt())
        }
        value("minLines", Int.MIN_VALUE.toDouble()) { _, it ->
            this.minLines(it.toInt())
        }
        color("textColor") { _, it ->
            this.textColor(it)
        }
        value("textSize", 13.0) { _, it ->
            this.textSizePx(it.toPx())
        }
        bound("textStyle", Typeface.NORMAL,
                mapOf(
                        "normal" to Typeface.NORMAL,
                        "bold" to Typeface.BOLD
                )
        ) { _, it ->
            this.typeface(Typeface.defaultFromStyle(it))
        }
    }

    override fun onCreate(
            c: BuildContext,
            attrs: Map<String, String>,
            visibility: Int
    ): Text.Builder {
        return Text.create(c.componentContext)
    }

    override fun Text.Builder.onComplete(
            c: BuildContext,
            attrs: Map<String, String>,
            visibility: Int
    ) {
        if (visibility == View.INVISIBLE) {
            textColor(Color.TRANSPARENT)
            textColorStateList(invisibleColor)
        }
    }

    private val invisibleColor = ColorStateList.valueOf(Color.TRANSPARENT)
}
