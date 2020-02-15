package com.guet.flexbox.handshake

import java.net.DatagramSocket
import java.net.InetAddress
/**
 * This way works well when there are multiple network interfaces.
 * It always returns the preferred outbound IP.
 * The destination 8.8.8.8 is not needed to be reachable.
 *
 *
 * https://stackoverflow.com/questions/9481865/getting-the-ip-address-of-the-current-machine-using-java
 */
object HostAddressFinder {

    fun findHostAddress(): String {
        return DatagramSocket().use { socket ->
            socket.connect(InetAddress.getByName("google.com"), 80)
            socket.localAddress.hostAddress
        }
    }
}