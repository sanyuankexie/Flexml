package com.guet.flexbox.litho.bitmap

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.ParcelFileDescriptor
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.resource.bitmap.*
import com.bumptech.glide.module.LibraryGlideModule
import com.guet.flexbox.build.Kit
import com.guet.flexbox.litho.drawable.ExBitmapDrawable
import java.io.InputStream
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean


@GlideModule
class ExBitmapDrawableLibraryModule : LibraryGlideModule() {

    companion object : Kit {

        private val isInit = AtomicBoolean(false)

        internal fun init(
                context: Context,
                glide: Glide,
                registry: Registry
        ) {
            if (!isInit.compareAndSet(false, true)) {
                return
            }
            val arrayPool = glide.arrayPool
            val bitmapPool = glide.bitmapPool
            val resources = context.resources
            val downSampler = Downsampler(
                    registry.imageHeaderParsers,
                    resources.displayMetrics, bitmapPool, arrayPool)
            val bitmapEncoder = BitmapEncoder(arrayPool)
            val parcelDecoder = VideoDecoder.parcel(bitmapPool)
            val byteBufferBitmapDecoder: ResourceDecoder<ByteBuffer, Bitmap>
            val streamBitmapDecoder: ResourceDecoder<InputStream, Bitmap>
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                streamBitmapDecoder = InputStreamBitmapImageDecoderResourceDecoder()
                byteBufferBitmapDecoder = ByteBufferBitmapImageDecoderResourceDecoder()
            } else {
                byteBufferBitmapDecoder = ByteBufferBitmapDecoder(downSampler)
                streamBitmapDecoder = StreamBitmapDecoder(downSampler, arrayPool)
            }
            registry.append(
                    GlideConstants.BUCKET_EX_BITMAP_DRAWABLE,
                    ByteBuffer::class.java,
                    ExBitmapDrawable::class.java,
                    ExBitmapDrawableDecoder(byteBufferBitmapDecoder, resources)
            ).append(GlideConstants.BUCKET_EX_BITMAP_DRAWABLE,
                    InputStream::class.java,
                    ExBitmapDrawable::class.java,
                    ExBitmapDrawableDecoder(streamBitmapDecoder, resources)
            ).append(GlideConstants.BUCKET_EX_BITMAP_DRAWABLE,
                    ParcelFileDescriptor::class.java,
                    ExBitmapDrawable::class.java,
                    ExBitmapDrawableDecoder(parcelDecoder, resources)
            ).append(
                    ExBitmapDrawable::class.java,
                    ExBitmapDrawableEncoder(bitmapPool, bitmapEncoder)
            ).register(
                    Bitmap::class.java,
                    ExBitmapDrawable::class.java,
                    ExBitmapDrawableTranscoder(resources)
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val byteBufferDecoder = VideoDecoder.byteBuffer(bitmapPool)
                registry.append(
                        GlideConstants.BUCKET_EX_BITMAP_DRAWABLE,
                        ByteBuffer::class.java,
                        ExBitmapDrawable::class.java,
                        ExBitmapDrawableDecoder(byteBufferDecoder, resources)
                )
            }
        }

        override fun init(c: Context) {
            if (isInit.get()) {
                return
            }
            val glide = Glide.get(c)
            init(c, glide, glide.registry)
        }
    }

    override fun registerComponents(
            context: Context,
            glide: Glide,
            registry: Registry
    ) {
        init(context, glide, registry)
    }

}