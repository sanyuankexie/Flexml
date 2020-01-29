package com.guet.flexbox.handshake

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
    fun index(): String {
       return "Hello world"
    }

    @RequestMapping(
            "/template",
            "/datasource",
            method = [RequestMethod.GET],
            produces = ["application/json"]
    )
    fun loadPackage(request: HttpServletRequest): ResponseEntity<*> {
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
                        val template = packageJson["template"] as? String
                        if (template != null) {
                            val templateFile = File(focus, template)
                            if (templateFile.exists()) {
                                return ResponseEntity.ok(
                                        JsonCompiler.compile(templateFile)
                                )
                            }
                        }
                    }
                    "/datasource" -> {
                        val data = packageJson["data"] as? String
                        if (data != null) {
                            val dataFile = File(focus, data)
                            if (dataFile.exists()) {
                                return ResponseEntity.ok(dataFile.readText())
                            }
                        }
                    }
                }
            }
        }
        return ResponseEntity.notFound().build<Any>()
    }

    @RequestMapping(
            "/focus",
            method = [RequestMethod.POST]
    )
    fun focus(@RequestParam focus: String) {
        MockServerApplication.focus = focus
    }
}