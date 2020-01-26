package com.guet.flexbox.litho

import android.view.View
import com.guet.flexbox.ConcurrentUtils
import com.guet.flexbox.el.ELContext
import com.guet.flexbox.transaction.HttpTransaction

internal class HttpTransactionImpl(
        private val host: HostingView,
        private val source: View?
) : HttpTransaction() {
    override fun commit(): (ELContext) -> Unit {
        return create { elContext ->
            host.pageEventListener?.run {
                sends.forEach {
                    onEventDispatched(
                            host,
                            source,
                            it
                    )
                }
            }
            val url = url ?: return@create
            val method = method ?: return@create
            val http = host.httpClient ?: return@create
            val success = success
            val error = error
            val onSuccess: ((Any) -> Unit)? = if (success != null) {
                {
                    ConcurrentUtils.runOnUiThread {
                        success.invoke(
                                elContext,
                                host.pageContext.toPageContext(source),
                                it
                        )
                    }
                }
            } else {
                null
            }
            val onError: (() -> Unit)? = if (error != null) {
                {
                    ConcurrentUtils.runOnUiThread {
                        error.invoke(elContext,
                                host.pageContext.toPageContext(source)
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
}