package com.guet.flexbox.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.AbsoluteLayout

class AsyncFlexboxLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AbsoluteLayout(context, attrs, defStyleAttr) {



    init {

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(getDefaultSize(suggestedMinimumWidth, widthMeasureSpec),
                getDefaultSize(suggestedMinimumHeight, heightMeasureSpec))
    }

    private fun beginAsyncMeasure() {
        requestLayout()
    }
}