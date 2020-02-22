package com.guet.flexbox.handshake

import com.guet.flexbox.handshake.lan.LANAddressProvider
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.context.WebServerInitializedEvent
import org.springframework.context.ApplicationContext
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

/***
 * 要在Java中跨平台的获取真正的首选出站ip，只能用这种极其恶心的写法
 */
@Component
class NetworkWatcher : Thread() {

    private val logger = LoggerFactory.getLogger(NetworkWatcher::class.java)

    @Autowired
    private lateinit var addressProvider: LANAddressProvider

    @Autowired
    private lateinit var context: ApplicationContext

    @Autowired
    private lateinit var attributes: ConcurrentHashMap<String, Any>

    private var last: String? = null

    override fun run() {
        while (true) {
            val host = addressProvider.get()?.hostAddress
            if (!host.isNullOrEmpty() && host != attributes["host"]) {
                attributes["host"] = host
                logger.info("now host:${host}")
                context.publishEvent(NetworkChangedEvent(this, host))
            }
            sleep(1000)
        }
    }

    @EventListener
    fun onInitialized(event: WebServerInitializedEvent) {
        start()
    }

}