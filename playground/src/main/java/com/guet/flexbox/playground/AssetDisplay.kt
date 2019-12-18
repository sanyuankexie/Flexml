package com.guet.flexbox.playground

import android.content.Context
import androidx.annotation.WorkerThread
import com.google.gson.Gson
import com.guet.flexbox.compiler.Compiler
import com.guet.flexbox.data.RenderNode
import com.guet.flexbox.data.LayoutNode
import com.guet.flexbox.databinding.DataBindingUtils
import java.util.Collections.singletonMap

class AssetDisplay(
        val banner: List<RenderNode>,
        val function: RenderNode,
        val feed: List<RenderNode>
) {
    companion object Default {
        @JvmStatic
        @WorkerThread
        fun loadDefault(c: Context): AssetDisplay {
            val res = c.resources
            val gson = Gson()
            val assets = res.assets
            val banner = res.getStringArray(R.array.banner_paths).map {
                val input = assets.open(it)
                val lockedInfo = DataBindingUtils.bind(c, gson.fromJson(
                        Compiler.compile(input),
                        LayoutNode::class.java
                ), singletonMap("url", it))
                input.close()
                lockedInfo
            }
            val feed = res.getStringArray(R.array.feed_paths).map {
                val input = assets.open(it)
                val lockedInfo = DataBindingUtils.bind(c, gson.fromJson(
                        Compiler.compile(input),
                        LayoutNode::class.java
                ), singletonMap("url", it))
                input.close()
                lockedInfo
            }.toMutableList()
            (0..100).forEach { _ ->
                feed.add(feed[0])
            }
            val functionPath = res.getString(R.string.function_path)
            val input = assets.open(functionPath)
            val function = DataBindingUtils.bind(c, gson.fromJson(
                    Compiler.compile(input),
                    LayoutNode::class.java
            ), singletonMap("url", functionPath))
            input.close()
            return AssetDisplay(banner, function, feed)
        }
    }
}