package com.guet.flexbox.handshake

import com.google.gson.Gson
import com.guet.flexbox.compiler.JsonCompiler
import com.guet.flexbox.handshake.utils.HostAddressFinder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import javax.servlet.http.HttpServletRequest

@Controller
class HomepageController {

    @Autowired
    private lateinit var attributes: ConcurrentHashMap<String, Any>

    @RequestMapping(
            "/",
            method = [RequestMethod.GET]
    )
    fun index(): String {
        return "/index.html"
    }

    @RequestMapping(
            "/template",
            "/datasource",
            method = [RequestMethod.GET]
    )
    fun loadPackage(
            request: HttpServletRequest,
            @Autowired gson: Gson
    ): ResponseEntity<String> {
        val focus = attributes["focus"] as? String
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
    fun focus(@RequestBody url: String?): ResponseEntity<Void> {
        if (url != null) {
            if (File(url).run { exists() && isFile }
                    && url.endsWith("package.json")) {
                attributes["focus"] = url
            }
            return ResponseEntity.ok().build()
        }
        return ResponseEntity.badRequest().build()
    }

    @RequestMapping(
            "/qrcode",
            method = [RequestMethod.GET]
    )
    @ResponseBody
    fun qrcode(): ResponseEntity<String> {
        val host = HostAddressFinder.findHostAddress()
        val port = attributes["port"]
        return ResponseEntity.ok("http://$host:${port}")
    }
}