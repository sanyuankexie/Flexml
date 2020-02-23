package com.guet.flexbox.transaction.dispatch

class HttpAction(
        val url: String,
        val method: String,
        val formBody: Map<String, String>,
        val callback: Callback
) {
    interface Callback {
        fun onResponse(data: String?)

        fun onError()
    }
}