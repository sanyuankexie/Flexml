package com.guet.flexbox.playground

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.NetworkUtils
import com.google.android.material.appbar.AppBarLayout
import com.guet.flexbox.playground.model.AppLoader
import com.guet.flexbox.playground.model.Homepage
import com.guet.flexbox.playground.widget.FlexBoxAdapter
import com.yzq.zxinglibrary.android.CaptureActivity
import com.yzq.zxinglibrary.bean.ZxingConfig
import com.yzq.zxinglibrary.common.Constant
import es.dmoral.toasty.Toasty
import razerdp.basepopup.QuickPopupBuilder
import razerdp.basepopup.QuickPopupConfig


class HomepageFragment : Fragment() {

    private lateinit var appBarLayout: AppBarLayout
    private lateinit var coordinator: CoordinatorLayout
    private lateinit var feed: RecyclerView
    private val feedAdapter = FlexBoxAdapter(this::handleEvent)
    private val homepageInfo: Homepage by AppLoader.lockHomepage()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_homepage, container, false)
    }


    override fun onViewCreated(
            view: View,
            savedInstanceState: Bundle?
    ) {
        view.findViewById<View>(R.id.qr_code).setOnClickListener {
            startQRCodeActivity()
        }
        val idea = view.findViewById<View>(R.id.idea)
        idea.setOnClickListener {
            val intent = Intent()
            intent.action = "android.intent.action.VIEW"
            val uri: Uri = Uri.parse("https://github.com/sanyuankexie/Flexml/wiki/环境配置")
            intent.data = uri
            startActivity(intent)
        }
        val sharedPreferences = requireActivity().getSharedPreferences("startup", Context.MODE_PRIVATE)
        if (!sharedPreferences.contains("first") || BuildConfig.DEBUG) {
            QuickPopupBuilder.with(requireContext())
                    .contentView(R.layout.idea_popup_window)
                    .config(QuickPopupConfig()
                            .gravity(Gravity.CENTER)
                            .blurBackground(true))
                    .show(idea)
            sharedPreferences.edit()
                    .putInt("first", 0)
                    .apply()
        }
        view.findViewById<View>(R.id.search).setOnClickListener {
            startActivity(Intent(requireContext(), SearchActivity::class.java),
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                            requireActivity(),
                            it,
                            "search"
                    ).toBundle())
        }
        appBarLayout = view.findViewById(R.id.appbar)
        coordinator = view.findViewById(R.id.coordinator)
        feed = view.findViewById(R.id.feed)
        feed.itemAnimator = null
        val foot = layoutInflater.inflate(
                R.layout.load_more,
                feed,
                false
        )
        feedAdapter.addFooterView(foot)
        feedAdapter.setNewData(homepageInfo.feed)
        feed.adapter = feedAdapter
        feedAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (feedAdapter.itemCount - 1 >= 100) {
                    feed.postDelayed({
                        val text = foot.findViewById<TextView>(R.id.text)
                        text.text = "我也是有底线的。"
                        val progress = foot.findViewById<View>(R.id.progress)
                        progress.visibility = View.GONE
                    }, 200)
                }
            }
        })
        feed.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!recyclerView.canScrollVertically(1) && feedAdapter.itemCount - 1 < 100) {
                    val app = requireContext().applicationContext
                    val activity = requireActivity()
                    AsyncTask.THREAD_POOL_EXECUTOR.execute {
                        val list = AppLoader.loadMoreFeedItem(app, 10, false)
                        if (!activity.isFinishing) {
                            activity.runOnUiThread {
                                feedAdapter.addData(list)
                            }
                        }
                    }
                }
            }
        })
        NetworkUtils.isAvailableAsync {
            if (!it) {
                requireActivity().runOnUiThread {
                    Toasty.error(requireContext(), "没网加载不了图片哦").show()
                }
            }
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
        (requireActivity() as MainActivity).showCodePanel(url)
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