package com.guet.flexbox.playground

import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ch.ielse.view.SwitchView
import com.facebook.litho.LithoView
import com.guet.flexbox.litho.HostingView
import com.guet.flexbox.litho.TemplatePage
import com.guet.flexbox.playground.model.MockService
import es.dmoral.toasty.Toasty
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class OverviewActivity : AppCompatActivity() {

    private lateinit var hostingView: HostingView
    private lateinit var console: LithoView
    private lateinit var mockService: MockService
    private lateinit var isLiveReload: SwitchView
    private lateinit var isOpenConsole: SwitchView
    private var toast: Toast? = null
    private val mainThread = Handler(Looper.getMainLooper())
    private val workThread = Handler(
            HandlerThread("network-thread").apply {
                start()
            }.looper
    )
    private val requestRunnable = object : Runnable {
        override fun run() {
            requestLayout()
            mainThread.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val url = intent?.extras?.getString("url")
        if (url.isNullOrEmpty() || !url.startsWith("http://")) {
            finishAfterTransition()
            Toasty.error(applicationContext, "url格式不对").show()
        } else {
            setContentView(R.layout.activity_overview)
            val title: TextView = findViewById(R.id.title)
            title.text = url
            val consoleRoot = findViewById<View>(R.id.console_root)
            console = findViewById(R.id.console)
            hostingView = findViewById(R.id.host)
            isOpenConsole = findViewById(R.id.is_open_console)
            isOpenConsole.setOnClickListener {
                if (isOpenConsole.isOpened) {
                    consoleRoot.visibility = View.VISIBLE
                } else {
                    consoleRoot.visibility = View.GONE
                }
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
                    .client(OkHttpClient.Builder()
                            .connectTimeout(500, TimeUnit.MILLISECONDS)
                            .build()
                    )
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(MockService::class.java)
            requestRunnable.run()
        }
    }

    private fun requestLayout() {
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
                    hostingView.templatePage = page
                }
            } catch (e: Exception) {
                e.printStackTrace()
                mainThread.post {
                    toast?.cancel()
                    Toasty.error(
                            c,
                            e.javaClass.simpleName + "：请求刷新出错"
                    ).show()
                }
            }
        }
    }
}