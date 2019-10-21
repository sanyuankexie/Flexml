package com.guet.flexbox.build

import android.graphics.Typeface
import android.text.Layout.Alignment.ALIGN_CENTER
import android.text.Layout.Alignment.valueOf
import com.facebook.litho.widget.Text
import org.dom4j.Attribute

internal object TextFactory : Factory<Text.Builder>() {

    init {
        bound("textAlign",
                ALIGN_CENTER,
                mapOf(
                        "center" to ALIGN_CENTER,
                        "left" to valueOf("ALIGN_LEFT"),
                        "right" to valueOf("ALIGN_RIGHT")
                )
        ) {
            this.textAlignment(it)
        }
        text("text") {
            this.text(it)
        }
        bool("clipToBounds") {
            this.clipToBounds(it)
        }
        value("maxLines", Int.MAX_VALUE.toDouble()) {
            this.maxLines(it.toInt())
        }
        value("minLines", Int.MIN_VALUE.toDouble()) {
            this.minLines(it.toInt())
        }
        color("textColor") {
            this.textColor(it)
        }
        value("textSize", 13.0) {
            this.textSizePx(it.toPx())
        }
        bound("textStyle", Typeface.NORMAL,
                mapOf(
                        "normal" to Typeface.NORMAL,
                        "bold" to Typeface.BOLD
                )
        ) {
            this.typeface(Typeface.defaultFromStyle(it))
        }
    }

    override fun create(
            c: BuildContext,
            attrs: List<Attribute>): Text.Builder {
        return Text.create(c.componentContext).apply {
            applyDefault(c, attrs)
        }
    }
}
