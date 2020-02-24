package com.guet.flexbox.playground

import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import ch.ielse.view.SwitchView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.guet.flexbox.litho.HostingView
import com.guet.flexbox.litho.TemplatePage
import com.guet.flexbox.playground.model.MockService
import com.guet.flexbox.playground.widget.MyRefreshViewImpl
import es.dmoral.toasty.Toasty
import io.iftech.android.library.refresh.RefreshViewLayout
import io.iftech.android.library.slide.SlideLayout
import io.iftech.android.library.slide.configSlideChildTypeHeader
import io.iftech.android.library.slide.configSlideChildTypeSlider
import kotlinx.android.synthetic.main.activity_overview.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class OverviewActivity : AppCompatActivity() {

    private lateinit var slideLayout: SlideLayout
    private lateinit var header: NestedScrollView
    private lateinit var hostingView: HostingView
    private lateinit var console: RecyclerView
    private lateinit var mockService: MockService
    private lateinit var isLiveReload: SwitchView
    private lateinit var refresh: RefreshViewLayout
    private lateinit var title: TextView
    private var toast: Toast? = null
    private val httpClient = OkHttpClient.Builder()
            .connectTimeout(500, TimeUnit.MILLISECONDS)
            .build()
    private val adapter = ConsoleAdapter()
    private val mainThread = Handler(Looper.getMainLooper())
    private val workThread = Handler(
            HandlerThread("network-thread").apply {
                start()
            }.looper
    )
    private val requestRunnable = object : Runnable {
        override fun run() {
            requestLayout {
                toast?.cancel()
                if (it != null) {
                    Toasty.error(
                            application,
                            it.javaClass.simpleName + "：请求刷新出错"
                    ).show()
                }
            }
            mainThread.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val url = intent?.extras?.getString("url")
        if (url.isNullOrEmpty() || !url.startsWith("http://")) {
            ActivityCompat.finishAfterTransition(this)
            Toasty.error(applicationContext, "url格式不对").show()
        } else {
            setContentView(R.layout.activity_overview)
            title = findViewById(R.id.title)
            title.text = url
            refresh = findViewById(R.id.refresh)
            refresh.refreshInterface = MyRefreshViewImpl(this)
            slideLayout = findViewById(R.id.slide_layout)
            slideLayout.expandHeader()
            slideLayout.setOnRefreshListener { _, _ ->
                if (isLiveReload.isOpened) {
                    slideLayout.finishRefresh()
                    Toasty.warning(this, "自动刷新开关打开时禁止手动刷新").show()
                } else {
                    requestLayout {
                        slideLayout.finishRefresh()
                        toast?.cancel()
                        if (it != null) {
                            Toasty.error(
                                    application,
                                    it.javaClass.simpleName + "：请求刷新出错"
                            ).show()
                        } else {
                            Toasty.success(
                                    application,
                                    "刷新成功"
                            ).show()
                        }
                    }
                }
            }
            console = findViewById(R.id.console)
            console.configSlideChildTypeSlider()
            console.adapter = adapter
            hostingView = findViewById(R.id.host)
//            hostingView.httpClient = object : HttpClient {
//                override fun enqueue(request: HttpRequest) {
//                    val body: FormBody? = if (request.formBody.isNotEmpty()) {
//                        val builder = FormBody.Builder()
//                        request.formBody.forEach {
//                            builder.add(it.key, it.value)
//                        }
//                        builder.build()
//                    } else {
//                        null
//                    }
//                    val request = Request.Builder()
//                            .url(request.url)
//                            .method(request.method, body)
//                            .build()
//                    httpClient.newCall(request).enqueue(object : Callback {
//                        override fun onFailure(call: Call, e: IOException) {
//                            e.printStackTrace()
//                            request.callback.onError()
//                        }
//
//                        override fun onResponse(call: Call, response: Response) {
//                            request.callback.onResponse(response.body()?.string())
//                        }
//                    })
//                }
//            }
//
//            hostingView.pageEventListener = object : HostingView.PageEventListener {
//                override fun onEventDispatched(
//                        h: HostingView,
//                        source: View?,
//                        values: Array<out Any?>?
//                ) {
//                    adapter.addData("event dispatched:\nvalues=${Arrays.deepToString(values)}")
//                }
//            }
            header = findViewById(R.id.header)
            header.configSlideChildTypeHeader()
            header.setOnScrollChangeListener { _: NestedScrollView?, _: Int, _: Int, _: Int, _: Int ->
                host.performIncrementalMount()
            }
            isLiveReload = findViewById(R.id.is_live_reload)
            isLiveReload.setOnClickListener {
                mainThread.removeCallbacks(requestRunnable)
                if (isLiveReload.isOpened) {
                    mainThread.post(requestRunnable)
                }
            }
            mockService = Retrofit.Builder()
                    .baseUrl(url)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(MockService::class.java)
            requestRunnable.run()
        }
    }

    override fun onResume() {
        super.onResume()
        mainThread.removeCallbacks(requestRunnable)
        if (isLiveReload.isOpened) {
            mainThread.post(requestRunnable)
        }
    }

    override fun onPause() {
        super.onPause()
        mainThread.removeCallbacks(requestRunnable)
    }

    private fun requestLayout(callback: (Throwable?) -> Unit) {
        val c = application
        workThread.post {
            try {
                val data = mockService
                        .data()
                        .execute()
                        .body()
                val layout = mockService
                        .layout()
                        .execute()
                        .body() ?: return@post
                val page = TemplatePage.create(c)
                        .data(data)
                        .template(layout)
                        .build()
                mainThread.post {
                    hostingView.unmountAllItems()
                    val last = hostingView.templatePage
                    last?.release()
                    hostingView.templatePage = page
                    callback(null)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                mainThread.post {
                    callback(e)
                }
            }
        }
    }

    private class ConsoleAdapter
        : BaseQuickAdapter<String, BaseViewHolder>(
            R.layout.console_item
    ) {
        override fun convert(helper: BaseViewHolder, item: String) {
            helper.getView<TextView>(R.id.text).text = item
        }
    }
}