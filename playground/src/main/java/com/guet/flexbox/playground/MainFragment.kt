package com.guet.flexbox.playground

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.NetworkUtils
import com.guet.flexbox.ConcurrentUtils
import com.guet.flexbox.litho.HostingView
import com.guet.flexbox.playground.model.AppBundle
import com.guet.flexbox.playground.model.Homepage
import com.guet.flexbox.playground.widget.FlexBoxAdapter
import com.guet.flexbox.playground.widget.PullToRefreshLayout
import com.yzq.zxinglibrary.android.CaptureActivity
import com.yzq.zxinglibrary.bean.ZxingConfig
import com.yzq.zxinglibrary.common.Constant
import es.dmoral.toasty.Toasty
import java.util.concurrent.atomic.AtomicBoolean

class MainFragment : Fragment() {

    private val handler = object : HostingView.PageEventListener {
        override fun onEventDispatched(h: HostingView, source: View?, values: Array<out Any?>?) {
            val url = values!![0] as? String
            if (url != null) {
                handleEvent(h, url)
            }
        }
    }
    private val feedAdapter = FlexBoxAdapter(this::handleEvent)
    private val loaded = AtomicBoolean(false)
    private lateinit var pullToRefresh: PullToRefreshLayout
    private lateinit var floatToolbar: LinearLayout
    private lateinit var banner: HostingView
    private lateinit var feed: RecyclerView
    private lateinit var function: HostingView
    private val homepageInfo: Homepage by AppBundle.waitHomepage()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(
            view: View,
            savedInstanceState: Bundle?
    ) {
        pullToRefresh = view.findViewById(R.id.pull_to_refresh)
        floatToolbar = view.findViewById(R.id.toolbar)
        feed = view.findViewById(R.id.feed)
        val headerView = layoutInflater.inflate(R.layout.feed_header, feed, false)
        val fQrCode = view.findViewById<View>(R.id.qr_code)
        val qrCode = headerView.findViewById<View>(R.id.qr_code)
        val handleToQr = View.OnClickListener {
            startQRCodeActivity()
        }
        fQrCode.setOnClickListener(handleToQr)
        qrCode.setOnClickListener(handleToQr)
        banner = headerView.findViewById(R.id.banner)
        banner.setPageEventListener(handler)
        val fSearch = view.findViewById<View>(R.id.search)
        val search = headerView.findViewById<View>(R.id.search)
        val handleToSearch = View.OnClickListener {
            startActivity(Intent(requireContext(), SearchActivity::class.java),
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                            requireActivity(),
                            it,
                            "search"
                    ).toBundle())
        }
        fSearch.setOnClickListener(handleToSearch)
        search.setOnClickListener(handleToSearch)
        function = headerView.findViewById(R.id.function)
        function.setPageEventListener(handler)
        load()
        feedAdapter.addHeaderView(headerView)
        feedAdapter.setNewData(homepageInfo.feed)
        feed.apply {
            adapter = feedAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(
                        recyclerView: RecyclerView,
                        dx: Int,
                        dy: Int
                ) {
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
                    val count = feedAdapter.itemCount
                    if (count >= 100) {
                        Toasty.warning(requireContext(), "再拉也没有了").show()
                        v.finish()
                        return
                    }
                    ConcurrentUtils.threadPool.execute {
                        val pages = AppBundle.loadMoreFeedItem(
                                requireContext().applicationContext,
                                10
                        )
                        requireActivity().runOnUiThread {
                            v.finish(Runnable {
                                feedAdapter.addData(pages)
                            })
                            Toasty.success(requireContext().applicationContext, "加载成功").show()
                        }
                    }
                }

                override fun onRefresh(v: PullToRefreshLayout) {
                    pullToRefresh.finish(Runnable {
                        Toasty.success(
                                requireContext(),
                                "刷新成功",
                                Toast.LENGTH_SHORT
                        ).show()
                    })
                }
            }
        }
        val handleToJump = View.OnClickListener {
            startActivity(Intent(requireContext(), Activity::class.java))
        }
        val fJump = view.findViewById<View>(R.id.jump)
        val jump = headerView.findViewById<View>(R.id.jump)
        fJump.setOnClickListener(handleToJump)
        jump.setOnClickListener(handleToJump)
        NetworkUtils.isAvailableAsync {
            if (!it) {
                requireActivity().runOnUiThread {
                    Toasty.error(requireContext(), "没网加载不了图片哦").show()
                }
            }
        }
    }

    private fun load() {
        banner.setContentAsync(homepageInfo.banner)
        function.setContentAsync(homepageInfo.function)
        feedAdapter.setNewData(homepageInfo.feed)
    }

    override fun onResume() {
        super.onResume()
        if (loaded.compareAndSet(false, true)) {
            pullToRefresh.postDelayed({ pullToRefresh.refresh() }, 500L)
        }
    }

    private fun startQRCodeActivity() {
        val intent = Intent(requireContext(), CaptureActivity::class.java)
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

    private fun handleEvent(v: View, url: String) {
        startActivity(
                Intent(requireContext(), CodeActivity::class.java).apply {
                    putExtra("url", url)
                },
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                        requireActivity(),
                        v,
                        "litho"
                ).toBundle())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (null != data) {
            data.extras ?: return
            // 扫描二维码/条码回传
            if (requestCode == REQUEST_CODE) {
                if (resultCode == Activity.RESULT_OK) {
                    val content = data.getStringExtra(Constant.CODED_CONTENT)
                    Toasty.success(requireContext(), "扫码成功", Toast.LENGTH_LONG)
                            .show()
                    val intent = Intent(requireContext(), OverviewActivity::class.java)
                    intent.putExtra("url", content)
                    startActivity(intent)
                } else {
                    Toasty.error(requireContext(), "解析二维码失败", Toast.LENGTH_LONG)
                            .show()
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE = 8080
    }
}