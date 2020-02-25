package com.guet.flexbox.litho.drawable.load

class CornerRadius private constructor(
        private val array: FloatArray
) {

    val hasEqualRadius: Boolean
        get() = array.size == 1

    val hasRadius: Boolean
        get() = array.isNotEmpty()

    val radii: FloatArray
        get() = array

    val radius
        get() = array[0]

    override fun hashCode(): Int {
        return array.contentHashCode()
    }

    override fun equals(other: Any?): Boolean {
        return (other === this) || (other is CornerRadius
                && array.contentEquals(other.array))
    }

    companion object {
        operator fun invoke(
                leftTop: Float,
                rightTop: Float,
                rightBottom: Float,
                leftBottom: Float
        ): CornerRadius {
            if (leftTop + rightTop + rightBottom + leftBottom != 0f) {
                val array = if (leftTop == rightTop
                        && leftTop == rightBottom
                        && leftTop == leftBottom) {
                    floatArrayOf(leftTop)
                } else {
                    floatArrayOf(
                            leftTop, leftTop,
                            rightTop, rightTop,
                            rightBottom, rightBottom,
                            leftBottom, leftBottom
                    )
                }
                return CornerRadius(array)
            } else {
                return empty
            }
        }

        operator fun invoke(
                value: Float
        ): CornerRadius {
            return if (value == 0f) {
                empty
            } else {
                CornerRadius(floatArrayOf(value))
            }
        }

        val empty = CornerRadius(FloatArray(0))
    }
}