package com.guet.flexbox.handshake.lan

import java.net.InetAddress

interface LANAddressProvider {
    fun get(): InetAddress?
}