package com.guet.flexbox.litho.load

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.ParcelFileDescriptor
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.resource.bitmap.*
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder
import com.bumptech.glide.load.resource.transcode.TranscoderRegistry
import com.bumptech.glide.module.LibraryGlideModule
import com.guet.flexbox.litho.drawable.ExBitmapDrawable
import java.io.InputStream
import java.lang.reflect.Field
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean


@GlideModule
class ExBitmapDrawableLibraryModule : LibraryGlideModule() {

    companion object {
        private inline fun <reified T> lockFieldByType(name: String): Field {
            return T::class.java.getDeclaredField(name).apply {
                isAccessible = true
            }
        }

        private fun <TResource, Transcode> Registry.prepend(
                resourceClass: Class<TResource>,
                transcodeClass: Class<Transcode>,
                transcoder: ResourceTranscoder<TResource, Transcode>
        ): Registry {
            this.register(
                    resourceClass,
                    transcodeClass,
                    transcoder
            )
            val transcoderRegistryField = lockFieldByType<Registry>("transcoderRegistry")
            val transcodersField = lockFieldByType<TranscoderRegistry>("transcoders")
            val transcoderRegistry = transcoderRegistryField.get(this)
            @Suppress("UNCHECKED_CAST")
            val transcoders = transcodersField.get(transcoderRegistry) as ArrayList<Any>
            val tc = transcoders.removeAt(transcoders.size - 1)
            transcoders.add(0, tc)
            return this
        }

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
            registry.prepend(
                    Constants.BUCKET_EX_BITMAP_DRAWABLE,
                    ByteBuffer::class.java,
                    ExBitmapDrawable::class.java,
                    ExBitmapDrawableDecoder(byteBufferBitmapDecoder, resources)
            ).prepend(Constants.BUCKET_EX_BITMAP_DRAWABLE,
                    InputStream::class.java,
                    ExBitmapDrawable::class.java,
                    ExBitmapDrawableDecoder(streamBitmapDecoder, resources)
            ).prepend(Constants.BUCKET_EX_BITMAP_DRAWABLE,
                    ParcelFileDescriptor::class.java,
                    ExBitmapDrawable::class.java,
                    ExBitmapDrawableDecoder(parcelDecoder, resources)
            ).prepend(
                    ExBitmapDrawable::class.java,
                    ExBitmapDrawableEncoder(bitmapPool, bitmapEncoder)
            ).prepend(
                    Bitmap::class.java,
                    ExBitmapDrawable::class.java,
                    ExBitmapDrawableTranscoder(resources)
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val byteBufferDecoder = VideoDecoder.byteBuffer(bitmapPool)
                registry.prepend(
                        Constants.BUCKET_EX_BITMAP_DRAWABLE,
                        ByteBuffer::class.java,
                        ExBitmapDrawable::class.java,
                        ExBitmapDrawableDecoder(byteBufferDecoder, resources)
                )
            }
            registry.setResourceDecoderBucketPriorityList(listOf(
                    Registry.BUCKET_GIF,
                    Registry.BUCKET_BITMAP,
                    Constants.BUCKET_EX_BITMAP_DRAWABLE,
                    Registry.BUCKET_BITMAP_DRAWABLE
            ))
        }

        fun init(context: Context) {
            val glide = Glide.get(context)
            init(context, glide, glide.registry)
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