package com.guet.flexbox.playground.model

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.annotation.WorkerThread
import com.guet.flexbox.litho.TemplatePage
import com.guet.flexbox.playground.R
import com.orhanobut.logger.Logger
import es.dmoral.toasty.Toasty
import java.util.concurrent.Callable
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.random.Random.Default

object AppLoader {

    private val randomImageUrls = ArrayList<String>(5)
    private val loaderExecutor = ThreadPoolExecutor(0, Int.MAX_VALUE,
            10L, TimeUnit.SECONDS,
            SynchronousQueue())
    private lateinit var appBundle: AppBundle
    private lateinit var homepageCache: Homepage
    private val handler = Handler(Looper.getMainLooper())

    fun loadWithCallback(ctx: Context, callback: () -> Unit) {
        val c = ctx.applicationContext
        loaderExecutor.execute {
            synchronized(this) {
                val start = SystemClock.uptimeMillis()
                val res = c.resources
                appBundle = AppBundle.loadAppBundle(
                        c,
                        loaderExecutor,
                        res.getString(R.string.introduction_path),
                        res.getString(R.string.banner_path),
                        res.getString(R.string.function_path),
                        *res.getStringArray(R.array.feed_paths)
                )
                randomImageUrls.addAll(res.getStringArray(R.array.images))
                //头部轮播图
                val bannerTask = loaderExecutor.submit(Callable<TemplatePage> {
                    val start1 = SystemClock.uptimeMillis()
                    val page = loadPage(c, res.getString(R.string.banner_path))
                    Logger.d("load banner ${SystemClock.uptimeMillis() - start1}")
                    return@Callable page
                })
                val introductionTask = loaderExecutor.submit<TemplatePage> {
                    return@submit loadPage(c, res.getString(R.string.introduction_path))
                }
                //功能区
                val functionTask = loaderExecutor.submit(Callable<TemplatePage> {
                    val start1 = SystemClock.uptimeMillis()
                    val page = loadPage(c, res.getString(R.string.function_path))
                    Logger.d("load function ${SystemClock.uptimeMillis() - start1}")
                    return@Callable page
                })
                //Feed流
                val feed = loadMoreFeedItem(c, 10, false) as ArrayList
                feed.add(0, functionTask.get())
                feed.add(0, bannerTask.get())
                homepageCache = Homepage(
                        feed,
                        introductionTask.get()
                )
                val finish = (SystemClock.uptimeMillis() - start)
                Logger.d("AppLoader: load time:$finish")
                handler.post {
                    Toasty.info(c, "load time:${finish}").show()
                    callback()
                }
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
        }
        val result = loaderExecutor.invokeAll(tasks).map {
            it.get()
        }
        Logger.d("AppLoader: load more page time:" + (SystemClock.uptimeMillis() - pageStart))
        return result
    }

    @WorkerThread
    fun loadPage(c: Context, url: String, data: (Map<String, Any>) = emptyMap()): TemplatePage {
        val template = appBundle.templateNode.getValue(url)
        val dataSource = appBundle.dataSource[url] ?: emptyMap()
        val myData = HashMap(dataSource).apply {
            put("url", url)
            putAll(data)
        }
        return TemplatePage.create(c)
                .template(template)
                .data(myData)
                .build()
    }

    fun lockHomepage(): Lazy<Homepage> {
        return synchronized(this) {
            lazyOf(homepageCache)
        }
    }

    fun findSourceCode(url: String): String {
        return appBundle.sourceCodes.getValue(url)
    }
}