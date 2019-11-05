package com.guet.flexbox.build

import android.graphics.Color
import android.graphics.Typeface
import android.text.Layout.Alignment.ALIGN_CENTER
import android.text.Layout.Alignment.valueOf
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
        color("textColor") { display, it ->
            if (display) {
                this.textColor(it)
            } else {
                this.textColor(Color.TRANSPARENT)
            }
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

    override fun create(
            c: BuildContext,
            attrs: Map<String, String>
    ): Text.Builder {
        return Text.create(c.componentContext)
    }
}
