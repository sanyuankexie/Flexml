package com.guet.flexbox.transaction.dispatch

interface HttpClient {
    fun enqueue(action: HttpAction)
}