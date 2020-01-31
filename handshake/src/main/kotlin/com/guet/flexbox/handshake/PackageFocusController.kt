package com.guet.flexbox.handshake

import com.google.gson.Gson
import com.guet.flexbox.compiler.JsonCompiler
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.web.context.WebServerInitializedEvent
import org.springframework.context.ApplicationListener
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.io.File
import javax.servlet.http.HttpServletRequest

@Controller
class PackageFocusController : ApplicationRunner,
        ApplicationListener<WebServerInitializedEvent> {

    @Volatile
    private var port: Int = 8080
    @Volatile
    private var focus: String? = null

    private val gson = Gson()

    @RequestMapping(
            "/",
            method = [RequestMethod.GET]
    )
    @ResponseBody
    fun index(): String {
        return "Hello world"
    }

    @RequestMapping(
            "/template",
            "/datasource",
            method = [RequestMethod.GET],
            produces = ["application/json"]
    )
    fun loadPackage(request: HttpServletRequest): ResponseEntity<String> {
        val focus = focus
        if (focus != null) {
            val packageFile = File(focus)
            if (packageFile.exists()) {
                val packageJson = gson
                        .fromJson<Map<String, Any>>(
                                packageFile.reader(),
                                Map::class.java
                        )
                when (request.servletPath) {
                    "/template" -> {
                        val template = packageJson["template"]
                                as? String
                        if (template != null) {
                            val templateFile = File(
                                    packageFile.parentFile,
                                    template
                            )
                            if (templateFile.exists()) {
                                return ResponseEntity.ok(
                                        JsonCompiler.compile(templateFile)
                                                .toString()
                                )
                            }
                        }
                    }
                    "/datasource" -> {
                        val data = packageJson["data"] as? String
                        if (data != null) {
                            val dataFile = File(
                                    packageFile.parentFile,
                                    data
                            )
                            if (dataFile.exists()) {
                                return ResponseEntity.ok(
                                        dataFile.readText()
                                )
                            }
                        }
                    }
                }
            }
        }
        return ResponseEntity.notFound().build<String>()
    }

    @RequestMapping(
            "/focus",
            method = [RequestMethod.POST]
    )
    fun focus(@RequestParam("focus") focus: String) {
        this.focus = focus
    }

    @RequestMapping(
            "/qrcode",
            produces = [MediaType.APPLICATION_JSON_VALUE],
            method = [RequestMethod.GET]
    )
    fun qrcode(): ResponseEntity<String> {
        val host = NetworkHostAddress.findHostAddress()
        return if (host != null) {
            val port = this.port
            ResponseEntity.ok("http://$host:${port}")
        } else {
            ResponseEntity.notFound().build()
        }
    }

    override fun run(args: ApplicationArguments) {
        if (args.containsOption("package.focus")) {
            focus = args.getOptionValues("package.focus").first()
        }
    }

    override fun onApplicationEvent(event: WebServerInitializedEvent) {
        port = event.webServer.port
    }
}