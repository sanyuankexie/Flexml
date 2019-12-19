package com.guet.flexbox.playground

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.facebook.litho.LithoView
import com.facebook.litho.Row
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaJustify
import com.guet.flexbox.DynamicBox
import com.guet.flexbox.data.RenderNode
import com.guet.flexbox.playground.widget.BannerHolder
import com.guet.flexbox.playground.widget.FlexBoxAdapter
import com.guet.flexbox.playground.widget.PullToRefreshLayout
import com.yzq.zxinglibrary.android.CaptureActivity
import com.yzq.zxinglibrary.bean.ZxingConfig
import com.yzq.zxinglibrary.common.Constant
import com.zhouwei.mzbanner.MZBannerView
import es.dmoral.toasty.Toasty
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : AppCompatActivity() {

    private val feedAdapter = FlexBoxAdapter()
    private val loaded = AtomicBoolean(false)
    private lateinit var pullToRefresh: PullToRefreshLayout
    private lateinit var floatToolbar: LinearLayout
    private lateinit var banner: MZBannerView<RenderNode>
    private lateinit var feed: RecyclerView
    private lateinit var function: LithoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pullToRefresh = findViewById(R.id.pull_to_refresh)
        floatToolbar = findViewById(R.id.toolbar)
        feed = findViewById(R.id.feed)
        val headerView = layoutInflater.inflate(R.layout.feed_header, feed, false)
        val fQrCode = findViewById<View>(R.id.qr_code)
        val qrCode = headerView.findViewById<View>(R.id.qr_code)
        val handleToQr = View.OnClickListener {
            startQRCodeActivity()
        }
        fQrCode.setOnClickListener(handleToQr)
        qrCode.setOnClickListener(handleToQr)
        banner = headerView.findViewById(R.id.banner)
        val fSearch = findViewById<View>(R.id.search)
        val search = headerView.findViewById<View>(R.id.search)
        val handleToSearch = View.OnClickListener {
            startActivity(Intent(this, SearchActivity::class.java),
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                            this,
                            it,
                            "search"
                    ).toBundle())
        }
        fSearch.setOnClickListener(handleToSearch)
        search.setOnClickListener(handleToSearch)
        function = headerView.findViewById(R.id.function)
        feedAdapter.apply {
            onClickListener = this@MainActivity::handleEvent
            addHeaderView(headerView)
        }
        feed.apply {
            adapter = feedAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val y = recyclerView.computeVerticalScrollOffset()
                    if (!pullToRefresh.isRefreshOrLoad && y > 0) {
                        floatToolbar.visibility = View.VISIBLE
                    } else {
                        floatToolbar.visibility = View.GONE
                    }
                }
            })
        }
        pullToRefresh.apply {
            onRefreshListener = object : PullToRefreshLayout.OnRefreshListener() {

                override fun onLoadMore(v: PullToRefreshLayout) {
                    v.finish()
                }

                override fun onRefresh(v: PullToRefreshLayout) {
                    loadAssetsDisplay()
                    v.finish()
                    Toasty.success(
                            this@MainActivity,
                            "资源加载成功",
                            Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        val handleToJump = View.OnClickListener {

        }
        val fJump = findViewById<View>(R.id.jump)
        val jump = headerView.findViewById<View>(R.id.jump)
        fJump.setOnClickListener(handleToJump)
        jump.setOnClickListener(handleToJump)
    }

    override fun onResume() {
        super.onResume()
        if (loaded.compareAndSet(false, true)) {
            pullToRefresh.postDelayed({ pullToRefresh.refresh() }, 500L)
        }
    }

    private fun loadAssetsDisplay() {
        AsyncTask.THREAD_POOL_EXECUTOR.execute {
            val assetsDisplay = AssetDisplay.loadDefault(this)
            val bannerData = assetsDisplay.banner.let {
                return@let when (it.size) {
                    1 -> {
                        listOf(it[0], it[0], it[0])
                    }
                    2 -> {
                        listOf(it[0], it[1], it[0])
                    }
                    else -> {
                        it
                    }
                }
            }
            runOnUiThread {
                banner.setPages(bannerData) {
                    BannerHolder(this::handleEvent)
                }
                feedAdapter.setNewData(assetsDisplay.feed)
                val c = function.componentContext
                function.setComponentAsync(Row.create(c)
                        .alignItems(YogaAlign.CENTER)
                        .flexGrow(1f)
                        .justifyContent(YogaJustify.CENTER)
                        .child(DynamicBox.create(c)
                                .content(assetsDisplay.function)
                        ).build())
            }
        }
    }

    private fun handleEvent(key: String) {
        startActivity(Intent(this, CodeActivity::class.java)
                .apply {
                    putExtra("url", key)
                }
        )
    }

    private fun startQRCodeActivity() {
        val intent = Intent(this, CaptureActivity::class.java)
        //ZxingConfig是配置类
        val config = ZxingConfig().apply {
            isPlayBeep = true //是否播放扫描声音 默认为true
            isShake = true //是否震动  默认为true
            isDecodeBarCode = false //是否扫描条形码 默认为true
            reactColor = R.color.colorAccent //设置扫描框四个角的颜色 默认为白色
            frameLineColor = R.color.colorAccent //设置扫描框边框颜色 默认无色
            scanLineColor = R.color.colorAccent //设置扫描线的颜色 默认白色
            isFullScreenScan = false //是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
        }
        intent.putExtra(Constant.INTENT_ZXING_CONFIG, config)
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (null != data) {
            data.extras ?: return
            // 扫描二维码/条码回传
            if (requestCode == REQUEST_CODE) {
                if (resultCode == Activity.RESULT_OK) {
                    val content = data.getStringExtra(Constant.CODED_CONTENT)
                    Toasty.success(this, "扫码成功", Toast.LENGTH_LONG)
                            .show()
                    val intent = Intent(this, OverviewActivity::class.java)
                    intent.putExtra("url", content)
                    startActivity(intent)
                    finish()
                } else {
                    Toasty.error(this, "解析二维码失败", Toast.LENGTH_LONG)
                            .show()
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE = 8080
    }
}