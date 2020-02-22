package com.guet.flexbox.handshake.lan

import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*

class MacOsLANAddressProvider : LANAddressProvider {
    override fun get(): InetAddress? {
        var net: Process? = null
        try {
            net = Runtime.getRuntime()
                    .exec("netstat -rn")
            val output = net
                    .inputStream
                    .bufferedReader()
            var line: String?
            do {
                line = output.readLine()
                if (line.trim().startsWith("default")) {
                    val tokenizer = StringTokenizer(line)
                    // default
                    tokenizer.nextToken()
                    // gateway
                    tokenizer.nextToken()
                    // flags
                    tokenizer.nextToken()
                    // interface name
                    val name = tokenizer.nextToken()
                    Iterable {
                        Iterable {
                            NetworkInterface.getNetworkInterfaces().iterator()
                        }.first {
                            it.name == name
                        }.inetAddresses.iterator()
                    }.mapNotNull {
                        it as? Inet4Address
                    }
                }
            } while (line != null)
        } catch (e: Throwable) {

        } finally {
            net?.destroy()
        }
        return null
    }
}