package com.guet.flexbox.playground.model

import android.content.Context
import android.os.SystemClock
import androidx.annotation.WorkerThread
import com.guet.flexbox.AppExecutors
import com.guet.flexbox.litho.TemplatePage
import com.guet.flexbox.playground.R
import com.orhanobut.logger.Logger
import java.util.concurrent.Callable
import kotlin.concurrent.thread
import kotlin.random.Random.Default

object AppLoader {

    private lateinit var appBundle: AppBundle
    private val randomImageUrls = ArrayList<String>(5)
    private lateinit var homepageCache: Homepage

    fun init(ctx: Context, callback: () -> Unit) {
        val c = ctx.applicationContext
        thread {
            synchronized(this) {
                val start = SystemClock.uptimeMillis()
                val res = c.resources
                appBundle = AppBundle.loadAppBundle(
                        c,
                        res.getString(R.string.banner_path),
                        res.getString(R.string.function_path),
                        *res.getStringArray(R.array.feed_paths)
                )
                randomImageUrls.addAll(res.getStringArray(R.array.images))

                //头部轮播图
                val bannerTask = AppExecutors.threadPool.submit<TemplatePage> {
                    val page = loadPage(c, res.getString(R.string.banner_path))
                    Logger.d("load banner ${SystemClock.uptimeMillis() - start}")
                    return@submit page
                }
                //功能区
                val functionTask = AppExecutors.threadPool.submit<TemplatePage> {
                    val page = loadPage(c, res.getString(R.string.function_path))
                    Logger.d("load function ${SystemClock.uptimeMillis() - start}")
                    return@submit page
                }
                //Feed流
                val feed = loadMoreFeedItem(c, 10, false) as ArrayList
                feed.add(0, functionTask.get())
                feed.add(0, bannerTask.get())
                homepageCache = Homepage(
                        feed
                )
                Logger.d("AppLoader: load time:" + (SystemClock.uptimeMillis() - start))
                AppExecutors.runOnUiThread(callback)
            }
        }
    }

    @WorkerThread
    fun loadMoreFeedItem(c: Context, count: Int, needNewImage: Boolean = true): List<TemplatePage> {
        if (needNewImage) {
            try {
                val start = SystemClock.uptimeMillis()
                val url = ImageService.random.get().execute().body()?.imgurl
                if (!url.isNullOrEmpty()) {
                    randomImageUrls.add(url)
                }
                Logger.d("AppLoader: request new image time:" + (SystemClock.uptimeMillis() - start))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val res = c.resources
        val feedUrls = res.getStringArray(R.array.feed_paths)
        val pageStart = SystemClock.uptimeMillis()
        val tasks = (1..count).map {
            Callable<TemplatePage> {
                val start = SystemClock.uptimeMillis()
                val type = Default.nextInt(0, feedUrls.size)
                val url = feedUrls[type]
                val data = if (type == 1) {
                    HashMap<String, Any>().apply {
                        val sum = Default.nextInt(50, 100)
                        val text = TextProvider.generation("这段文字是用随机生成的", sum)
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
                Logger.d("AppLoader: load single page time:" + (SystemClock.uptimeMillis() - start))
                return@Callable page
            }
        }.map {
            AppExecutors.threadPool.submit(it)
        }
        val result = tasks.map {
            it.get()
        }
        Logger.d("AppLoader: load more page time:" + (SystemClock.uptimeMillis() - pageStart))
        return result
    }

    @WorkerThread
    fun loadPage(c: Context, url: String, data: (Map<String, Any>) = emptyMap()): TemplatePage {
        val template = appBundle.templateNode.getValue(url)
        val dataSource = appBundle.dataSource.getValue(url)
        val myData =  HashMap(dataSource).apply {
            put("url", url)
            putAll(data)
        }
        return TemplatePage.create(c)
                .template(template)
                .data(myData)
                .build()
    }

    fun waitHomepage(): Lazy<Homepage> {
        return synchronized(this) {
            lazyOf(homepageCache)
        }
    }

    fun findSourceCode(url: String): String {
        return appBundle.sourceCodes.getValue(url)
    }
}