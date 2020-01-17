package com.guet.flexbox.litho

import android.view.View

abstract class PageEventAdapter : HostingView.PageEventListener {

    override fun onEventDispatched(
            h: HostingView,
            source: View,
            vararg values: Any?
    ) {

    }

    override fun onPageChanged(
            h: HostingView,
            page: Page,
            data: Any?
    ) {

    }
}