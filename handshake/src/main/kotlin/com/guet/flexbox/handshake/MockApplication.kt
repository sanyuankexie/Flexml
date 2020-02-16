package com.guet.flexbox.handshake

import com.guet.flexbox.handshake.ui.QrcodeForm
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.context.WebServerInitializedEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Configuration
import java.util.concurrent.ConcurrentHashMap

@Configuration
@SpringBootApplication
open class MockApplication : ApplicationRunner,
        ApplicationListener<WebServerInitializedEvent> {

    private val logger = LoggerFactory.getLogger(MockApplication::class.java)

    @Autowired
    private lateinit var form: QrcodeForm

    @Autowired
    private lateinit var attributes: ConcurrentHashMap<String, Any>

    override fun run(args: ApplicationArguments) {
        if (args.containsOption("package.focus")) {
            val focus = args.getOptionValues("package.focus").first()
            attributes["focus"] = focus
            logger.info("focus=$focus")
        }
    }

    override fun onApplicationEvent(event: WebServerInitializedEvent) {
        attributes["port"] = event.webServer.port
        form.start()
    }
}

