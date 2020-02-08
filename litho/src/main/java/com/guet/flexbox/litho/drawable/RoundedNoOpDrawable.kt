package com.guet.flexbox.litho.drawable

class RoundedNoOpDrawable(
        override val leftTop: Float,
        override val rightTop: Float,
        override val rightBottom: Float,
        override val leftBottom: Float
) : NoOpDrawable(), RoundedCorners