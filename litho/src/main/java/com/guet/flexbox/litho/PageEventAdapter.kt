package com.guet.flexbox.litho

import android.view.View

abstract class PageEventAdapter : HostingView.PageEventListener {

    override fun onEventDispatched(
            h: HostingView,
            source: View?,
            values: Array<out Any?>?
    ) {

    }

    override fun onPageChanged(
            h: HostingView,
            page: Page
    ) {

    }
}