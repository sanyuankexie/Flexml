package com.guet.flexbox.litho.factories.filler

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.core.text.HtmlCompat
import com.facebook.litho.widget.Text
import java.util.regex.Pattern

internal object TextFiller : PropFiller<Text.Builder, String> {

    private val invisibleColor = ColorStateList.valueOf(Color.TRANSPARENT)

    private val htmlTester = Pattern.compile(".*<(.*)>.*")

    override fun fill(
            c: Text.Builder,
            display: Boolean,
            other: Map<String, Any>,
            value: String
    ) {
        val htmlText = if (htmlTester.matcher(value).find()) {
            try {
                HtmlCompat.fromHtml(
                        value,
                        HtmlCompat.FROM_HTML_MODE_COMPACT,
                        null,
                        null
                )
            } catch (e: Throwable) {
                value
            }
        } else {
            value
        }
        c.text(htmlText)
        if (!display) {
            c.textColor(Color.TRANSPARENT)
            c.textColorStateList(invisibleColor)
        }
    }
}