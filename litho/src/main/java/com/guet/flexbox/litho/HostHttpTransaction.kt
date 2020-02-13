package com.guet.flexbox.litho

import android.view.View
import com.guet.flexbox.AppExecutors
import com.guet.flexbox.transaction.HttpTransaction

internal class HostHttpTransaction(
        private val host: HostingView,
        private val source: View?
) : HttpTransaction() {
    override fun commit() {
        host.pageEventListener?.run {
            sends.forEach {
                onEventDispatched(
                        host,
                        source,
                        it
                )
            }
        }
        val url = url ?: return
        val method = method ?: return
        val http = host.httpClient ?: return
        val success = success
        val error = error
        val onSuccess: ((Any) -> Unit)? = if (success != null) {
            {
                AppExecutors.runOnUiThread {
                    success.invoke(
                            host.target.toPageContext(source),
                            it
                    )
                }
            }
        } else {
            null
        }
        val onError: (() -> Unit)? = if (error != null) {
            {
                AppExecutors.runOnUiThread {
                    error.invoke(
                            host.target.toPageContext(source)
                    )
                }
            }
        } else {
            null
        }
        http.enqueue(
                url,
                method,
                prams,
                onSuccess,
                onError
        )
    }
}