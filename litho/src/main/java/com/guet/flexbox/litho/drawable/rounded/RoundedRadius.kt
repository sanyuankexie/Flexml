package com.guet.flexbox.litho.drawable.rounded

import android.graphics.drawable.Drawable

interface RoundedRadius {
    val leftTop: Float
    val rightTop: Float
    val rightBottom: Float
    val leftBottom: Float

    val hasRoundedCorners: Boolean
        get() = leftTop != 0f || rightTop != 0f || rightBottom != 0f || leftBottom != 0f

    fun toRadiiArray(array: FloatArray): FloatArray {
        array[0] = leftTop
        array[1] = leftTop
        array[2] = rightTop
        array[3] = rightTop
        array[4] = rightBottom
        array[5] = rightBottom
        array[6] = leftBottom
        array[7] = leftBottom
        return array
    }

    object Default : RoundedRadius {
        override val leftTop: Float
            get() = 0f
        override val rightTop: Float
            get() = 0f
        override val rightBottom: Float
            get() = 0f
        override val leftBottom: Float
            get() = 0f
    }

    companion object {

        fun equals(left: RoundedRadius, right: RoundedRadius): Boolean {
            return left.leftBottom == right.leftBottom
                    && left.leftTop == right.leftTop
                    && left.rightBottom == right.rightBottom
                    && left.rightTop == right.rightTop
        }

        fun from(drawable: Drawable): RoundedRadius {
            return if (drawable is RoundedRadius) {
                drawable
            } else {
                Default
            }
        }

    }
}