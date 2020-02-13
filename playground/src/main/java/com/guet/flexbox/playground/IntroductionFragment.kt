package com.guet.flexbox.playground

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.guet.flexbox.litho.HostingView
import com.guet.flexbox.playground.model.AppLoader
import com.guet.flexbox.playground.model.Homepage

class IntroductionFragment : Fragment() {

    private lateinit var host: HostingView
    private val homepageInfo: Homepage by AppLoader.lockHomepage()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        host = HostingView(inflater.context)
        return host
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


    }
}