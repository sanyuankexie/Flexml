package com.guet.flexbox.handshake.lan

import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*

class MacOsLANAddressProvider : LANAddressProvider {
    private val interfaceName = "Netif"
    override fun get(): InetAddress? {
        var net: Process? = null
        try {
            net = Runtime.getRuntime()
                    .exec("netstat -rn")
            val output = net
                    .inputStream
                    .bufferedReader()
            var line: String?
            var interfaceIndex = -1
            do {
                line = output.readLine()

                if (line.contains(interfaceName)) {
                    val strArray = line.split(" ")
                    for (item in strArray) {
                        if (item.trim().isNotEmpty()) {
                            interfaceIndex++
                        }
                        if (item.trim() == interfaceName) {
                            break
                        }
                    }
                }

                if (line.trim().startsWith("default") && interfaceIndex >= 0) {
                    val tokenizer = StringTokenizer(line)

                    repeat(interfaceIndex){
                        tokenizer.nextToken()
                    }

                    val name = tokenizer.nextToken()
                    return Iterable {
                        Iterable {
                            NetworkInterface.getNetworkInterfaces().iterator()
                        }.first {
                            it.name == name
                        }.inetAddresses.iterator()
                    }.first {
                        it is Inet4Address
                    }
                }
            } while (line != null)
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            net?.destroy()
        }
        return null
    }
}