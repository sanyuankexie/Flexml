package com.guet.flexbox.litho.factories

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.Html
import androidx.core.text.HtmlCompat
import com.facebook.litho.ComponentContext
import com.facebook.litho.widget.Text
import com.guet.flexbox.build.AttributeSet
import com.guet.flexbox.build.UrlType
import com.guet.flexbox.enums.TextStyle
import com.guet.flexbox.litho.drawable.ColorDrawable
import com.guet.flexbox.litho.drawable.LazyImageDrawable
import com.guet.flexbox.litho.drawable.lazyDrawable
import com.guet.flexbox.litho.factories.assign.Assignment
import com.guet.flexbox.litho.factories.assign.AttrsAssigns
import com.guet.flexbox.litho.factories.assign.EnumMappings
import java.util.regex.Pattern


internal object ToText : ToComponent<Text.Builder>() {

    private val invisibleColor = ColorStateList.valueOf(Color.TRANSPARENT)

    private val htmlTester = Pattern.compile(".*<(.*)>.*")

    override val attrsAssigns by AttrsAssigns
            .create<Text.Builder>(CommonAssigns.attrsAssigns) {
                enum("verticalGravity", Text.Builder::verticalGravity)
                enum("horizontalGravity", Text.Builder::textAlignment)
                bool("clipToBounds", Text.Builder::clipToBounds)
                value("maxLines", Text.Builder::maxLines)
                value("minLines", Text.Builder::minLines)
                pt("textSize", Text.Builder::textSizePx)
                enum("ellipsize", Text.Builder::ellipsize)
                register("textStyle", object : Assignment<Text.Builder, TextStyle> {
                    override fun assign(c: Text.Builder, display: Boolean, other: Map<String, Any>, value: TextStyle) {
                        c.typeface(Typeface.defaultFromStyle(EnumMappings.get(value)))
                    }
                })
                register("textColor", object : Assignment<Text.Builder, Int> {
                    override fun assign(c: Text.Builder, display: Boolean, other: Map<String, Any>, value: Int) {
                        if (display) {
                            c.textColor(value)
                        } else {
                            c.textColor(Color.TRANSPARENT)
                            c.textColorStateList(invisibleColor)
                        }
                    }
                })
                register("text", object : Assignment<Text.Builder, String> {
                    override fun assign(c: Text.Builder, display: Boolean, other: Map<String, Any>, value: String) {
                        val htmlText = if (htmlTester.matcher(value).find()) {
                            try {
                                HtmlCompat.fromHtml(
                                        value,
                                        HtmlCompat.FROM_HTML_MODE_COMPACT,
                                        GlideImageGetter(c.context
                                        !!.androidContext),
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
                })
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