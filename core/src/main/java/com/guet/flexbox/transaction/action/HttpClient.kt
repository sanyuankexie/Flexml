package com.guet.flexbox.transaction.action

interface HttpClient {
    fun enqueue(action: HttpAction)
}