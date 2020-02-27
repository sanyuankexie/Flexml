package com.guet.flexbox.litho.drawable.load

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.Option
import com.bumptech.glide.module.LibraryGlideModule
import com.guet.flexbox.build.BuildKit
import com.guet.flexbox.litho.drawable.BitmapDrawable
import java.io.File
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean


@GlideModule
class DrawableLoaderModule : LibraryGlideModule() {

    companion object : BuildKit {

        val scaleType = Option.memory(
                BitmapDrawable::class.java.name + ".scaleType",
                ImageView.ScaleType.FIT_XY
        )
        val cornerRadius = Option.memory<CornerRadius>(
                BitmapDrawable::class.java.name + ".cornerRadius"
        )

        private val isInit = AtomicBoolean(false)

        internal fun init(
                registry: Registry
        ) {
            if (!isInit.compareAndSet(false, true)) {
                return
            }
            registry.register(Bitmap::class.java,
                    BitmapDrawable::class.java,
                    BitmapDrawableTranscoder()
            ).prepend(File::class.java, ByteBuffer::class.java, FileBufferLoader.Factory())
        }

        override fun init(c: Context) {
            if (isInit.get()) {
                return
            }
            val glide = Glide.get(c)
            init(glide.registry)
        }
    }

    override fun registerComponents(
            context: Context,
            glide: Glide,
            registry: Registry
    ) {
        init(registry)
    }
}