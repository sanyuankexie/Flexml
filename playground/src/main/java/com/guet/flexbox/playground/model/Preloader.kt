package com.guet.flexbox.playground.model

import android.content.Context
import android.os.AsyncTask
import androidx.annotation.WorkerThread
import com.google.gson.Gson
import com.guet.flexbox.Page
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.databinding.Toolkit
import com.guet.flexbox.playground.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.FileNotFoundException
import java.util.concurrent.CountDownLatch
import kotlin.random.Random.Default

object Preloader {

    private val gson = Gson()
    private val templateSource = HashMap<String, String>()
    private val templateCache = HashMap<String, TemplateNode>()
    private val dataSourceCache = HashMap<String, Map<String, Any>>()
    private val randomImageUrls = ArrayList<String>(5)
    private lateinit var homepageCache: Homepage

    fun init(ctx: Context, callback: () -> Unit) {
        val c = ctx.applicationContext
        AsyncTask.THREAD_POOL_EXECUTOR.execute {
            synchronized(this) {
                val res = c.resources
                //头部轮播图
                val banner = res.getStringArray(R.array.banner_paths)
                        .map { url ->
                            loadPage(c, url)
                        }
                //功能区
                val function = loadPage(c, res.getString(R.string.function_path))
                //Feed流
                val feed = loadMoreFeedItem(c, 10)
                homepageCache = Homepage(banner, function, feed)
                callback()
            }
        }
    }

    @WorkerThread
    private fun loadNewImages() {
        randomImageUrls.clear()
        val randomImageCount = Default.nextInt(5, 10)
        val result = (1..randomImageCount).map {
            object : CountDownLatch(1), Callback<ACGImage> {

                @Volatile
                private var result: String? = null

                override fun onFailure(call: Call<ACGImage>, t: Throwable) {
                    countDown()
                }

                override fun onResponse(call: Call<ACGImage>, response: Response<ACGImage>) {
                    result = response.body()?.imgurl
                    countDown()
                }

                fun get(): String? {
                    await()
                    return result
                }
            }.apply {
                ImageService.random.get().enqueue(this)
            }
        }.mapNotNull {
            it.get()
        }
        randomImageUrls.addAll(result)
    }

    @WorkerThread
    fun loadMoreFeedItem(c: Context, count: Int): List<Page> {
        loadNewImages()
        val res = c.resources
        val feedUrls = res.getStringArray(R.array.feed_paths)
        return (1..count).map {
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
            loadPage(c, url, data)
        }
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
            Compiler.compile(source)
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
        return Toolkit.preload(c, template, HashMap(dataSource).apply {
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