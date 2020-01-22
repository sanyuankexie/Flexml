package com.guet.flexbox.playground.model

import android.content.Context
import android.os.SystemClock
import android.util.Log
import androidx.annotation.WorkerThread
import com.google.gson.Gson
import com.guet.flexbox.ConcurrentUtils
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.litho.LithoBuildTool
import com.guet.flexbox.litho.Page
import com.guet.flexbox.playground.R
import java.io.FileNotFoundException
import kotlin.random.Random.Default

object AppBundle {

    private val gson = Gson()
    private val templateSource = HashMap<String, String>()
    private val templateCache = HashMap<String, TemplateNode>()
    private val dataSourceCache = HashMap<String, Map<String, Any>>()
    private val randomImageUrls = ArrayList<String>(5)
    private lateinit var homepageCache: Homepage

    fun init(ctx: Context, callback: () -> Unit) {
        val c = ctx.applicationContext
        ConcurrentUtils.threadPool.execute {
            synchronized(this) {
                val start = SystemClock.uptimeMillis()
                val res = c.resources
                randomImageUrls.addAll(res.getStringArray(R.array.images))
                //头部轮播图
                val banner = loadPage(c, res.getString(R.string.banner_path))
                //功能区
                val function = loadPage(c, res.getString(R.string.function_path))
                //Feed流
                val feed = loadMoreFeedItem(c, 10, false)
                homepageCache = Homepage(banner, function, feed)
                Log.d("AppBundle", "load time:" + (SystemClock.uptimeMillis() - start))
                callback()
            }
        }
    }

    @WorkerThread
    fun loadMoreFeedItem(c: Context, count: Int, needNewImage: Boolean = true): List<Page> {
        if (needNewImage) {
            try {
                val start = SystemClock.uptimeMillis()
                val url = ImageService.random.get().execute().body()?.imgurl
                if (!url.isNullOrEmpty()) {
                    randomImageUrls.add(url)
                }
                Log.d("AppBundle", "request new image time:" + (SystemClock.uptimeMillis() - start))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val res = c.resources
        val feedUrls = res.getStringArray(R.array.feed_paths)
        val pageStart = SystemClock.uptimeMillis()
        val result = (1..count).map {
            val start = SystemClock.uptimeMillis()
            val type = Default.nextInt(0, feedUrls.size)
            val url = feedUrls[type]
            val data = if (type == 1) {
                HashMap<String, Any>().apply {
                    val sum = Default.nextInt(50, 80)
                    val text = TextProvider.generation("好评", sum)
                    put("text", text)
                    if (randomImageUrls.isNotEmpty()) {
                        val rowCount = Default.nextInt(1, 5)
                        val images = (1..rowCount).map {
                            (1..3).map {
                                val imageIndex = Default.nextInt(0, randomImageUrls.size)
                                randomImageUrls[imageIndex]
                            }
                        }.flatten()
                        put("images", images)
                    }
                    put("clicked", false)
                }
            } else {
                HashMap<String, Any>().apply {
                    if (randomImageUrls.isNotEmpty()) {
                        val images = (1..3).map {
                            val imageIndex = Default.nextInt(0, randomImageUrls.size)
                            randomImageUrls[imageIndex]
                        }
                        put("images", images)
                    }
                }
            }
            val page = loadPage(c, url, data)
            Log.d("AppBundle", "load single page time:" + (SystemClock.uptimeMillis() - start))
            return@map page
        }
        Log.d("AppBundle", "load more page time:" + (SystemClock.uptimeMillis() - pageStart))
        return result
    }

    @WorkerThread
    fun loadTemplateSource(c: Context, url: String): String {
        return templateSource.getOrPut(url) {
            c.assets.open("$url.xml").use {
                it.reader().readText()
            }
        }
    }

    @WorkerThread
    fun loadTemplateNode(c: Context, url: String): TemplateNode {
        return templateCache.getOrPut(url) {
            val source = loadTemplateSource(c, url)
            JitCompiler.compile(source)
        }
    }

    @WorkerThread
    fun loadDataSource(c: Context, url: String): Map<String, Any> {
        return dataSourceCache.getOrPut(url) {
            val jsonUrl = "$url.json"
            try {
                c.assets.open(jsonUrl).use {
                    @Suppress("UNCHECKED_CAST")
                    gson.fromJson(it.reader(), Map::class.java) as Map<String, Any>
                }
            } catch (e: FileNotFoundException) {
                emptyMap()
            }
        }
    }

    @WorkerThread
    fun loadPage(c: Context, url: String, data: (Map<String, Any>) = emptyMap()): Page {
        val template = loadTemplateNode(c, url)
        val dataSource = loadDataSource(c, url)
        return LithoBuildTool.preload(c, template, HashMap(dataSource).apply {
            put("url", url)
            putAll(data)
        })
    }

    fun waitHomepage(): Lazy<Homepage> {
        return synchronized(this) {
            lazyOf(homepageCache)
        }
    }
}