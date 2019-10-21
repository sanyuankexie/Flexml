package com.guet.flexbox.mock

import com.google.zxing.WriterException
import com.sun.net.httpserver.HttpServer
import okio.buffer
import okio.sink
import okio.source
import java.io.File
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class MockServer @Throws(IOException::class, WriterException::class)
private constructor(executor: Executor,
                    port: Int,
                    template: File,
                    json: File) {

    @Volatile
    private lateinit var files: Array<File>

    @Throws(IOException::class, WriterException::class)
    constructor(port: Int,
                template: File,
                json: File) : this(Executors.newSingleThreadExecutor(), port, template, json) {
    }

    init {
        change(template, json)
        val address = InetAddress.getLocalHost()
        val url = "http://" + address.hostAddress + ":" + port
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
                os.sink().buffer().apply {
                    writeAll(files[0].source())
                    flush()
                }
                os.close()
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
        files = arrayOf(Objects.requireNonNull(template), Objects.requireNonNull(json))
    }

    companion object {

        private const val DEFAULT_PORT = 8080

        @JvmStatic
        fun main(args: Array<String>) {
            try {
                val xml = File(args[0])
                val json = File(args[1])
                val looper = Looper()
                MockServer(looper, DEFAULT_PORT, xml, json)
                looper.loop()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
}