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
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class MockSession private constructor(
        executor: Executor,
        private var layout: String,
        private var data: String
) {

    constructor(layout: String, data: String)
            : this(Executors.newSingleThreadExecutor(), layout, data)

    private val server: HttpServer

    init {
        val address = InetAddress.getLocalHost()
        val url = "http://${address.hostAddress}:$DEFAULT_PORT"
        ConsoleQRCode.print(url)
        println("布局地址：$url/layout")
        println("数据地址：$url/data")
        server = HttpServer.create(InetSocketAddress(DEFAULT_PORT), 0)
        server.executor = executor
        server.createContext("/layout") { httpExchange ->
            println(httpExchange.remoteAddress.toString() + " request layout")
            try {
                httpExchange.sendResponseHeaders(200, 0)
                val os = httpExchange.responseBody
                val string = toJson(synchronized(server) {
                    sax.read(layout)
                }.rootElement).toString()
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
                val source = File(synchronized(server) { data }).source()
                os.sink().buffer().apply {
                    writeAll(source)
                    flush()
                }
                source.close()
                os.close()
                httpExchange.close()
            } catch (e: Exception) {
                throw IOException(e)
            }
        }
    }

    fun change(layout: String, data: String) {
        synchronized(server) {
            this.layout = layout
            this.data = data
        }
    }

    fun start() {
        server.start()
    }

    fun close() {
        server.stop(0)
    }

    companion object {

        private val sax = SAXReader()

        private const val DEFAULT_PORT = 8080

        @JvmStatic
        fun open(layout: String, data: String) {
            val looper = Looper()
            MockSession(layout, data).start()
            looper.loop()
        }

        @JvmStatic
        fun compile(layout: String, path: String) {
            val string = toJson(sax.read(layout).rootElement).toString()
            FileWriter(path).apply {
                write(string)
                close()
            }
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
