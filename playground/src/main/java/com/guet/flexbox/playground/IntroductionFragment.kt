package com.guet.flexbox.playground

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.guet.flexbox.litho.HostingView
import com.guet.flexbox.playground.model.AppLoader
import com.guet.flexbox.playground.model.Homepage

class IntroductionFragment : Fragment() {

    private lateinit var scroller: RecyclerView
    private lateinit var host: HostingView
    private val homepageInfo: Homepage by AppLoader.lockHomepage()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        scroller = RecyclerView(inflater.context)
        host = HostingView(inflater.context)
        scroller.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                return object : RecyclerView.ViewHolder(host) {}
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            }

            override fun getItemCount(): Int {
                return 1
            }
        }
        scroller.layoutManager = LinearLayoutManager(inflater.context)
        return scroller
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        scroller.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                host.performIncrementalMount()
            }
        })
        host.templatePage = homepageInfo.introduction
    }
}