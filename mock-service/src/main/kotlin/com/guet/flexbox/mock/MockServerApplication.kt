package com.guet.flexbox.mock

import com.guet.flexbox.mock.gui.GuiApplication
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class MockServerApplication : ApplicationRunner {

    companion object {
        @Volatile
        var port: Int = 8080

        @Volatile
        var focus: String? = null
    }

    override fun run(args: ApplicationArguments) {
        GuiApplication.run()
        if (args.containsOption("package.focus")) {
            focus = args.getOptionValues("focus").first()
        }
        if (args.containsOption("server.port")) {
            port = args.getOptionValues("server.port").first()
                    .toIntOrNull() ?: 8080
        }
    }
}

