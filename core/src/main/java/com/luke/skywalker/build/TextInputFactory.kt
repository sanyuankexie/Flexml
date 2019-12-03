package com.luke.skywalker.build

import android.graphics.Typeface
import android.text.TextUtils.TruncateAt.*
import com.facebook.litho.ComponentContext
import com.facebook.litho.widget.TextInput
import com.luke.skywalker.DynamicBox
import com.luke.skywalker.el.LambdaExpression
import com.luke.skywalker.el.PropsELContext

internal object TextInputFactory : WidgetFactory<TextInput.Builder>() {

    init {
        numberAttr("maxLines", Int.MAX_VALUE) { _, _, it ->
            this.maxLines(it)
        }
        numberAttr("minLines", Int.MIN_VALUE) { _, _, it ->
            this.minLines(it)
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
            data: PropsELContext,
            attrs: Map<String, String>?,
            visibility: Int
    ): TextInput.Builder {
        return TextInput.create(c)
    }

    override fun onLoadStyles(
            owner: TextInput.Builder,
            c: ComponentContext,
            data: PropsELContext,
            attrs: Map<String, String>?,
            visibility: Int
    ) {
        super.onLoadStyles(owner, c, data, attrs, visibility)
        data.tryGetValue<LambdaExpression?>(attrs?.get("onTextChanged"), null)?.run {
            owner.textChangedEventHandler(DynamicBox.onTextChanged(c, this))
        }
    }
}