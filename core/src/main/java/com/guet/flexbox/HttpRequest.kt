package com.guet.flexbox

class HttpRequest(
        val url: String,
        val method: String,
        val body: Map<String, String>,
        val callback: Callback
) {
    interface Callback {
        fun onResponse(data: String?)

        fun onError()
    }
}