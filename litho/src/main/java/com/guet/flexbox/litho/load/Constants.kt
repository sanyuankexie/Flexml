package com.guet.flexbox.litho.load

import android.widget.ImageView.ScaleType
import com.bumptech.glide.load.Option
import com.guet.flexbox.litho.drawable.ExBitmapDrawable

object Constants {

    const val BUCKET_EX_BITMAP_DRAWABLE = "ExBitmapDrawable"

    val scaleType = Option.memory(
            ExBitmapDrawable::class.java.name + ".scaleType",
            ScaleType.FIT_XY
    )
    val cornerRadius = Option.memory<CornerRadius>(
            ExBitmapDrawable::class.java.name + ".cornerRadius"
    )

}