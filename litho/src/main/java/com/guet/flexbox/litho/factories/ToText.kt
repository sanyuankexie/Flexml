package com.guet.flexbox.litho.factories

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.Html
import android.text.TextUtils.TruncateAt
import androidx.core.text.HtmlCompat
import com.facebook.litho.ComponentContext
import com.facebook.litho.widget.Text
import com.guet.flexbox.build.AttributeSet
import com.guet.flexbox.enums.Horizontal
import com.guet.flexbox.enums.TextStyle
import com.guet.flexbox.enums.Vertical
import com.guet.flexbox.litho.drawable.ColorDrawable
import com.guet.flexbox.litho.drawable.LazyImageDrawable
import com.guet.flexbox.litho.drawable.lazyDrawable
import com.guet.flexbox.litho.resolve.UrlType
import com.guet.flexbox.litho.resolve.createProvider
import com.guet.flexbox.litho.resolve.mapping
import com.guet.flexbox.litho.toPx
import java.util.regex.Pattern


internal object ToText : ToComponent<Text.Builder>(CommonAssigns) {

    private val invisibleColor = ColorStateList.valueOf(Color.TRANSPARENT)

    private val htmlTester = Pattern.compile(".*<(.*)>.*")

    override val matcherProvider = createProvider<Text.Builder> {
        register("verticalGravity") { _, _, value: Vertical ->
            verticalGravity(value.mapping())
        }
        register("horizontalGravity") { _, _, value: Horizontal ->
            textAlignment(value.mapping())
        }
        register("text") { display, _, value: String ->
            val htmlText = if (htmlTester.matcher(value).find()) {
                try {
                    HtmlCompat.fromHtml(
                            value,
                            HtmlCompat.FROM_HTML_MODE_COMPACT,
                            GlideImageGetter(context!!.androidContext),
                            null
                    )
                } catch (e: Throwable) {
                    value
                }
            } else {
                value
            }
            text(htmlText)
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

    private class GlideImageGetter(
            private val context: Context
    ) : Html.ImageGetter {
        override fun getDrawable(source: String?): Drawable? {
            if (source.isNullOrEmpty()) {
                return null
            } else {
                val (type, prams) = UrlType.parseUrl(context, source)
                return when (type) {
                    UrlType.GRADIENT -> {
                        val orientation = prams[0] as GradientDrawable.Orientation
                        val colors = prams[1] as IntArray
                        lazyDrawable {
                            GradientDrawable(
                                    orientation, colors
                            )
                        }
                    }
                    UrlType.COLOR -> {
                        val color = prams[0] as Int
                        lazyDrawable {
                            ColorDrawable(
                                    color
                            )
                        }
                    }
                    UrlType.URL, UrlType.RESOURCE -> {
                        val model = prams[0]
                        LazyImageDrawable(
                                context,
                                model
                        )
                    }
                    else -> null
                }
            }
        }
    }
}