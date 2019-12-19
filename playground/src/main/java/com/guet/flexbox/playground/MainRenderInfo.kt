package com.guet.flexbox.playground

import android.content.Context
import android.os.AsyncTask
import com.google.gson.Gson
import com.guet.flexbox.compiler.Compiler
import com.guet.flexbox.data.LayoutNode
import com.guet.flexbox.data.RenderNode
import com.guet.flexbox.databinding.DataBindingUtils
import java.util.*
import kotlin.reflect.KProperty

class MainRenderInfo(
        val banner: List<RenderNode>,
        val function: RenderNode,
        val feed: List<RenderNode>
) {
    companion object {

        internal fun init(c: Context) {
            AsyncTask.THREAD_POOL_EXECUTOR.execute {
                synchronized(this) {
                    val res = c.resources
                    val gson = Gson()
                    val assets = res.assets
                    val banner = res.getStringArray(R.array.banner_paths).map {
                        val input = assets.open(it)
                        val lockedInfo = DataBindingUtils.bind(c, gson.fromJson(
                                Compiler.compile(input),
                                LayoutNode::class.java
                        ), Collections.singletonMap("url", it))
                        input.close()
                        lockedInfo
                    }
                    val feed = res.getStringArray(R.array.feed_paths).map {
                        val input = assets.open(it)
                        val json = gson.fromJson(
                                Compiler.compile(input),
                                LayoutNode::class.java
                        )
                        input.close()
                        val node = (1..50).map { index ->
                            DataBindingUtils.bind(c, json, mapOf("url" to it, "index" to index))
                        }
                        node
                    }.flatten()
                    val functionPath = res.getString(R.string.function_path)
                    val input = assets.open(functionPath)
                    val function = DataBindingUtils.bind(c, gson.fromJson(
                            Compiler.compile(input),
                            LayoutNode::class.java
                    ), mapOf(
                            "url" to functionPath,
                            "icons" to res.getStringArray(R.array.function_icons)
                    ))
                    input.close()
                    cache = MainRenderInfo(banner, function, feed)
                }
            }
        }

        private lateinit var cache: MainRenderInfo

        operator fun getValue(thisRef: Any?, property: KProperty<*>): MainRenderInfo {
            return synchronized(this) { cache }
        }
    }
}