package com.guet.flexbox.litho.drawable.rounded

import com.guet.flexbox.litho.drawable.NoOpDrawable

class RoundedNoOpDrawable(
        override val leftTop: Float,
        override val rightTop: Float,
        override val rightBottom: Float,
        override val leftBottom: Float
) : NoOpDrawable(), RoundedRadius