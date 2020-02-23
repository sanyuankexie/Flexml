package com.guet.flexbox

import com.guet.flexbox.event.HttpAction

interface HttpClient {
    fun enqueue(action: HttpAction)
}