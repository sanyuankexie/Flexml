package com.guet.flexbox.handshake

import java.net.Inet4Address
import java.net.NetworkInterface

object NetworkHostAddress {
    fun findHostAddress(): String? {
        try {
            Inet4Address.getLocalHost()
            return NetworkInterface.getNetworkInterfaces()
                    .asSequence()
                    .map {
                        it.inetAddresses.toList()
                    }.flatten()
                    .firstOrNull { ip ->
                        ip is Inet4Address && !ip.isLoopbackAddress
                                && ip.hostAddress.indexOf(":") == -1
                    }?.hostAddress
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}