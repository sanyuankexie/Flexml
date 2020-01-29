package com.guet.flexbox.handshake.gui

import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.schedule

object NetworkWatcher {
    private var lastHostAddress: String? = null
    private val timer = Timer()
    private val listeners = CopyOnWriteArrayList<(String) -> Unit>()

    init {
        timer.schedule(0, 2000) {
            val host = findHostAddress()
            if (host != null && host != lastHostAddress) {
                lastHostAddress = host
                listeners.forEach {
                    it.invoke(host)
                }
            }
        }
    }

    fun addListener(l: (String) -> Unit) {
        listeners.add(l)
    }

    fun removeListener(l: (String) -> Unit) {
        listeners.remove(l)
    }

    private fun findHostAddress(): String? {
        try {
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