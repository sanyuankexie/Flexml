package com.guet.flexbox.handshake.lan

import java.net.DatagramSocket
import java.net.InetAddress

class OtherLANAddressProvider : LANAddressProvider {
    override fun get(): InetAddress? {
        return DatagramSocket().use {
            it.connect(InetAddress.getByName("8.8.8.8"), 10086)
            it.localAddress
        }
    }
}