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
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class FileObserver
@Throws(IOException::class)
private constructor(
        executor: Executor,
        port: Int,
        template: File,
        json: File
) {

    companion object {

        private val sax = SAXReader()

        private const val DEFAULT_PORT = 8080

        private fun toJson(element: Element): JsonObject {
            val obj = JsonObject()
            val attrs = JsonObject()
            val children = JsonArray()
            element.elements().map { toJson(it) }.forEach { children.add(it) }
            element.attributes().forEach { attrs.addProperty(it.name, it.value) }
            obj.addProperty("type", element.name)
            obj.add("attrs", attrs)
            obj.add("children", children)
            return obj
        }

        @JvmStatic
        fun main(args: Array<String>) {
            try {
                val xml = File(args[0])
                val json = File(args[1])
                val looper = Looper()
                FileObserver(looper, DEFAULT_PORT, xml, json)
                looper.loop()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @Volatile
    private var files: Array<File>

    @Throws(IOException::class)
    constructor(
            port: Int,
            template: File,
            json: File
    ) : this(
            Executors.newSingleThreadExecutor(),
            port,
            template,
            json
    )

    init {
        files = arrayOf(template, json)
        val address = InetAddress.getLocalHost()
        val url = "http://${address.hostAddress}:$port"
        ConsoleQRCode.print(url)
        println("布局地址：$url/layout")
        println("数据地址：$url/data")
        val server = HttpServer.create(InetSocketAddress(port), 0)
        server.executor = executor
        server.createContext("/layout") { httpExchange ->
            println(httpExchange.remoteAddress.toString() + " request layout")
            try {
                httpExchange.sendResponseHeaders(200, 0)
                val os = httpExchange.responseBody
                val string = toJson(sax.read(files[0]).rootElement).toString()
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
                    writeAll(files[1].source())
                    flush()
                }
                os.close()
                httpExchange.close()
            } catch (e: Exception) {
                throw IOException(e)
            }
        }
        server.start()
    }

    fun change(template: File, json: File) {
        files = arrayOf(template, json)
    }
}