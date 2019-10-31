package com.guet.flexbox.mock

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.sun.net.httpserver.HttpServer
import okio.buffer
import okio.sink
import okio.source
import org.dom4j.Element
import org.dom4j.io.SAXReader
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress

abstract class MockSession {

    abstract fun close();

    companion object {
        private val sax = SAXReader()

        private const val DEFAULT_PORT = 8080

        @JvmOverloads
        @JvmStatic
        fun run(layout: String, data: String, executorType: RunnerType = RunnerType.Current): MockSession {
            val address = InetAddress.getLocalHost()
            val url = "http://${address.hostAddress}:$DEFAULT_PORT"
            ConsoleQRCode.print(url)
            println("布局地址：$url/layout")
            println("数据地址：$url/data")
            val server = HttpServer.create(InetSocketAddress(DEFAULT_PORT), 0)
            var looper: Looper? = null
            if (executorType == RunnerType.Current) {
                looper = Looper()
                server.executor = looper
            }
            server.createContext("/layout") { httpExchange ->
                println(httpExchange.remoteAddress.toString() + " request layout")
                try {
                    httpExchange.sendResponseHeaders(200, 0)
                    val os = httpExchange.responseBody
                    val string = toJson(sax.read(layout).rootElement).toString()
                    os.write(string.toByteArray())
                    httpExchange.close()
                } catch (e: Exception) {
                    throw IOException(e)
                }
            }
            server.createContext("/data") { httpExchange ->
                println(httpExchange.remoteAddress.toString() + " request data")
                try {
                    httpExchange.sendResponseHeaders(200, 0)
                    val os = httpExchange.responseBody
                    os.sink().buffer().apply {
                        writeAll(File(data).source())
                        flush()
                    }
                    os.close()
                    httpExchange.close()
                } catch (e: Exception) {
                    throw IOException(e)
                }
            }
            server.start()
            looper?.loop()
            return if (executorType == RunnerType.Current) {
                object : MockSession() {
                    override fun close() {
                        server.stop(0)
                    }
                }
            } else {
                throw IllegalStateException()
            }
        }

        @JvmStatic
        fun compile(layout: String, path: String) {
            val string = toJson(sax.read(layout).rootElement).toString()
            FileWriter(path).write(string)
        }

        private fun toJson(element: Element): JsonObject {
            val obj = JsonObject()
            obj.addProperty("type", element.name)
            element.attributes().apply {
                if (isNotEmpty()) {
                    val attrs = JsonObject()
                    forEach { attrs.addProperty(it.name, it.value) }
                    obj.add("attrs", attrs)
                }
            }
            element.elements().apply {
                if (isNotEmpty()) {
                    val children = JsonArray()
                    map { toJson(it) }.forEach { children.add(it) }
                    obj.add("children", children)
                }
            }
            return obj
        }
    }
}
