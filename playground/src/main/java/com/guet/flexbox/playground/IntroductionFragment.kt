package com.guet.flexbox.playground

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.guet.flexbox.litho.HostingView
import com.guet.flexbox.playground.model.AppLoader
import com.guet.flexbox.playground.model.Homepage

class IntroductionFragment : Fragment() {

    private lateinit var scroller: NestedScrollView
    private lateinit var host: HostingView
    private val homepageInfo: Homepage by AppLoader.lockHomepage()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        scroller = NestedScrollView(inflater.context)
        scroller.isFillViewport = true
        scroller.isNestedScrollingEnabled = true
        host = HostingView(inflater.context)
        scroller.addView(host)
        return scroller
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        scroller.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            host.performIncrementalMount()
        }
        host.templatePage = homepageInfo.introduction
    }
}