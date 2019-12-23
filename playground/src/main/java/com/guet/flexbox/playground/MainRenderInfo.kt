package com.guet.flexbox.playground

import android.content.Context
import android.os.AsyncTask
import com.google.gson.Gson
import com.guet.flexbox.ContentNode
import com.guet.flexbox.PageUtils
import com.guet.flexbox.PreloadPage
import com.guet.flexbox.compiler.Compiler
import java.util.*

class MainRenderInfo(
        val banner: List<PreloadPage>,
        val function: PreloadPage,
        val feed: List<PreloadPage>
) {
    companion object {

        internal fun initAsync(c: Context) {
            AsyncTask.THREAD_POOL_EXECUTOR.execute {
                synchronized(this) {
                    val res = c.resources
                    val gson = Gson()
                    val assets = res.assets
                    val banner = res.getStringArray(R.array.banner_paths).map {
                        val input = assets.open(it)
                        val lockedInfo = PageUtils.preload(
                                c,
                                gson.fromJson(
                                        Compiler.compile(input),
                                        ContentNode::class.java
                                ),
                                Collections.singletonMap("url", it))
                        input.close()
                        lockedInfo
                    }
                    val feed = res.getStringArray(R.array.feed_paths).map {
                        val input = assets.open(it)
                        val json = gson.fromJson(
                                Compiler.compile(input),
                                ContentNode::class.java
                        )
                        input.close()
                        val node = (1..50).map { index ->
                            PageUtils.preload(
                                    c,
                                    json,
                                    mapOf("url" to it, "index" to index)
                            )
                        }
                        node
                    }.flatten()
                    val functionPath = res.getString(R.string.function_path)
                    val input = assets.open(functionPath)
                    val function = PageUtils.preload(
                            c,
                            gson.fromJson(Compiler.compile(input), ContentNode::class.java),
                            mapOf(
                                    "url" to functionPath,
                                    "icons" to res.getStringArray(R.array.function_icons)
                            ))
                    input.close()
                    cache = MainRenderInfo(banner, function, feed)
                }
            }
        }

        private lateinit var cache: MainRenderInfo

        fun wait(): Lazy<MainRenderInfo> {
            return synchronized(this) {
                lazyOf(cache)
            }
        }
    }
}