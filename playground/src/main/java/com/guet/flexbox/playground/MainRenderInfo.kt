package com.guet.flexbox.playground

import android.content.Context
import android.os.AsyncTask
import com.google.gson.Gson
import com.guet.flexbox.compiler.Compiler
import com.guet.flexbox.content.DynamicNode
import com.guet.flexbox.content.RenderContent
import com.guet.flexbox.databinding.DataBindingUtils
import java.util.*

class MainRenderInfo(
        val banner: List<RenderContent>,
        val function: RenderContent,
        val feed: List<RenderContent>
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
                        val lockedInfo = DataBindingUtils.bind(
                                c,
                                gson.fromJson(
                                        Compiler.compile(input),
                                        DynamicNode::class.java
                                ),
                                Collections.singletonMap("url", it))
                        input.close()
                        lockedInfo
                    }
                    val feed = res.getStringArray(R.array.feed_paths).map {
                        val input = assets.open(it)
                        val json = gson.fromJson(
                                Compiler.compile(input),
                                DynamicNode::class.java
                        )
                        input.close()
                        val node = (1..50).map { index ->
                            DataBindingUtils.bind(
                                    c,
                                    json,
                                    mapOf("url" to it, "index" to index)
                            )
                        }
                        node
                    }.flatten()
                    val functionPath = res.getString(R.string.function_path)
                    val input = assets.open(functionPath)
                    val function = DataBindingUtils.bind(
                            c,
                            gson.fromJson(Compiler.compile(input), DynamicNode::class.java),
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