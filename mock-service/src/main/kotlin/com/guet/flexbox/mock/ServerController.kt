package com.guet.flexbox.mock

import com.google.gson.Gson
import com.guet.flexbox.compiler.JsonCompiler
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.File
import javax.servlet.http.HttpServletRequest

@RestController
class ServerController {

    private val gson = Gson()

    @RequestMapping(
            "/",
            method = [RequestMethod.GET]
    )
    @ResponseBody
    fun index() {

    }

    @RequestMapping(
            "/template",
            "/datasource",
            method = [RequestMethod.GET],
            produces = ["application/json"]
    )
    fun loadPackage(request: HttpServletRequest): ResponseEntity<String> {
        val focus = MockServerApplication.focus
        if (focus != null) {
            val packageFile = File(focus, "package.json")
            if (packageFile.exists()) {
                val packageJson = gson
                        .fromJson<Map<String, Any>>(
                                packageFile.reader(),
                                Map::class.java
                        )
                when (request.servletPath) {
                    "/template" -> {
                        val template = packageJson["template"] as? String
                        if (template != null) {
                            val templateFile = File(template)
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
                            val dataFile = File(data)
                            if (dataFile.exists()) {
                                return ResponseEntity.ok(dataFile.readText())
                            }
                        }
                    }
                }
            }
        }
        return ResponseEntity.notFound().build()
    }

    @RequestMapping(
            "/focus",
            method = [RequestMethod.POST]
    )
    fun focus(
            request: HttpServletRequest,
            @RequestBody current: String
    ) {
        MockServerApplication.focus = current
    }
}