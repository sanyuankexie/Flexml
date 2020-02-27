package com.guet.flexbox.litho.drawable.load

import android.os.Build
import android.os.SharedMemory
import android.util.Log
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoader.LoadData
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import com.bumptech.glide.util.ByteBufferUtil
import java.io.File
import java.io.IOException
import java.lang.reflect.Method
import java.nio.ByteBuffer

/**
 * Loads [java.nio.ByteBuffer]s using NIO for [java.io.File].
 */
class FileBufferLoader : ModelLoader<File, ByteBuffer> {
    override fun buildLoadData(
            file: File, width: Int, height: Int, options: Options): LoadData<ByteBuffer>? {
        return LoadData<ByteBuffer>(ObjectKey(file), ByteBufferFetcher(file))
    }

    override fun handles(file: File): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1
                || freeBuffer != null
    }

    /**
     * Factory for [com.bumptech.glide.load.model.ByteBufferFileLoader].
     */
    class Factory : ModelLoaderFactory<File, ByteBuffer> {
        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<File, ByteBuffer> {
            return FileBufferLoader()
        }

        override fun teardown() { // Do nothing.
        }
    }

    private class ByteBufferFetcher(private val file: File) : DataFetcher<ByteBuffer> {
        private lateinit var result: ByteBuffer
        override fun loadData(
                priority: Priority, callback: DataFetcher.DataCallback<in ByteBuffer?>) {
            result = try {
                ByteBufferUtil.fromFile(file)
            } catch (e: IOException) {
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "Failed to obtain ByteBuffer for file", e)
                }
                callback.onLoadFailed(e)
                return
            }
            callback.onDataReady(result)
        }

        override fun cleanup() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                SharedMemory.unmap(result)
            } else {
                try {
                    freeBuffer?.invoke(null, result)
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }

        override fun cancel() { // Do nothing.
        }

        override fun getDataClass(): Class<ByteBuffer> {
            return ByteBuffer::class.java
        }

        override fun getDataSource(): DataSource {
            return DataSource.LOCAL
        }

    }

    private companion object {

        private val freeBuffer: Method?

        init {
            freeBuffer = try {
                @Suppress("DiscouragedPrivateApi")
                Class.forName("java.nio.NioUtils")
                        .getDeclaredMethod(
                                "freeDirectBuffer",
                                ByteBuffer::class.java
                        ).apply {
                            isAccessible = true
                        }
            } catch (e: Throwable) {
                e.printStackTrace()
                null
            }
        }

        private const val TAG = "ByteBufferFileLoader"
    }
}