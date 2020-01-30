package com.guet.flexbox.handshake

import com.google.gson.Gson
import com.guet.flexbox.compiler.JsonCompiler
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.io.File
import javax.servlet.http.HttpServletRequest

@Controller
class ServerController {

    private val gson = Gson()

    @RequestMapping(
            "/",
            method = [RequestMethod.GET]
    )
    fun index(): String {
       return "/index"
    }

    @RequestMapping(
            "/template",
            "/datasource",
            method = [RequestMethod.GET],
            produces = ["application/json"]
    )
    @ResponseBody
    fun loadPackage(request: HttpServletRequest): ResponseEntity<String> {
        val focus = MockServerApplication.focus
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
        MockServerApplication.focus = focus
    }
}