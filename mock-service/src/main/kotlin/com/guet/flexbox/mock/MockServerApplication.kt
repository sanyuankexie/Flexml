package com.guet.flexbox.mock

import com.guet.flexbox.mock.gui.GuiApplication
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import java.awt.GraphicsEnvironment

@SpringBootApplication
object MockServerApplication : ApplicationRunner {

    @Volatile
    var focus: String? = null

    override fun run(args: ApplicationArguments) {
        if (!GraphicsEnvironment.isHeadless()) {
            GuiApplication.run()
        }
        if (args.containsOption("focus")) {
            focus = args.getOptionValues("focus").single()
        }
    }
}

