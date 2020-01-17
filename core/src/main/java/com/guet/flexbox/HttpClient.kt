package com.guet.flexbox

interface HttpClient {
    fun enqueue(
            url: String,
            method: String,
            prams: Map<String, String>,
            success: ((Any) -> Unit)?,
            error: (() -> Unit)?
    )
}