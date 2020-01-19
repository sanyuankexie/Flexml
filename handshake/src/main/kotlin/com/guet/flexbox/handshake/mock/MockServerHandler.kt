package com.guet.flexbox.handshake.mock

import com.google.gson.internal.Streams
import com.google.gson.stream.JsonWriter
import com.guet.flexbox.handshake.compile.Compiler
import com.guet.flexbox.handshake.util.EmbeddedHandler
import com.guet.flexbox.handshake.util.nowTime
import com.intellij.execution.process.ProcessOutputTypes
import com.intellij.util.concurrency.AppExecutorUtil
import com.sun.net.httpserver.HttpServer
import java.awt.EventQueue
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import java.io.PrintWriter
import java.io.RandomAccessFile
import java.io.StringWriter
import java.net.Inet4Address
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.nio.channels.Channels


class MockServerHandler(
    private val configuration: FlexmlMockRunConfiguration
) : EmbeddedHandler() {


    private var form: QrCodeForm? = null

    private var server: HttpServer? = null

    override fun onStart() {
        val address = findHostAddress()
        if (address != null) {
            val port = configuration.state?.port ?: 8080
            val url = "http://$address:$port"
            println("host url：$url")
            val template = configuration.state?.template
            if (template.isNullOrEmpty()) {
                notifyTextAvailable("No target file found\n", ProcessOutputTypes.STDERR)
                killProcess()
                return
            }
            val dataSource = configuration.state?.dataSource
            val fixedDataSource = if (dataSource.isNullOrEmpty()) {
                File(File(template).parentFile, "data.json").absolutePath
            } else {
                dataSource
            }
            val server = HttpServer.create(InetSocketAddress(port), 0)
            server.executor = AppExecutorUtil.getAppExecutorService()
            server.createContext("/") { httpExchange ->
                val html = javaClass.classLoader.getResourceAsStream("host.html")
                    ?.reader()?.readText()
                if (html != null) {
                    val out = String.format(html, "$url/template", "$url/datasource")
                    httpExchange.sendResponseHeaders(200, 0)
                    httpExchange.responseBody.writer().buffered().use {
                        it.write(out)
                    }
                } else {
                    httpExchange.sendResponseHeaders(404, 0)
                }
            }
            server.createContext("/template") { httpExchange ->
                println(httpExchange.remoteAddress.toString() + " request layout at time $nowTime")
                try {
                    httpExchange.sendResponseHeaders(200, 0)
                    JsonWriter(
                        httpExchange.responseBody
                            .writer()
                            .buffered()
                    ).apply {
                        isLenient = true
                    }.use { writer ->
                        val jsonObject = Compiler.compile(template)
                        Streams.write(jsonObject, writer)
                    }

                } catch (e: Exception) {
                    val w = StringWriter()
                    e.printStackTrace(PrintWriter(w))
                    notifyTextAvailable(
                        w.toString(),
                        ProcessOutputTypes.STDERR
                    )
                }
            }
            server.createContext("/datasource") { httpExchange ->
                println(httpExchange.remoteAddress.toString() + " request datasource at time $nowTime")
                try {
                    httpExchange.sendResponseHeaders(200, 0)
                    val output = Channels.newChannel(httpExchange.responseBody)
                    val file = RandomAccessFile(fixedDataSource, "r")
                    val input = file.channel
                    input.transferTo(0, file.length(), output)
                    output.close()
                    file.close()
                } catch (e: Exception) {
                    val w = StringWriter()
                    e.printStackTrace(PrintWriter(w))
                    notifyTextAvailable(
                        w.toString(),
                        ProcessOutputTypes.STDERR
                    )
                }
            }
            server.start()
            this.server = server
            EventQueue.invokeLater {
                val form = QrCodeForm(url)
                form.addWindowListener(object : WindowAdapter() {
                    override fun windowClosed(e: WindowEvent?) {
                        killProcess()
                    }
                })
                this.form = form
            }
        } else {
            notifyTextAvailable(
                "An error occurred while searching the local IP address. " +
                        "Please check the network status of the machine and make " +
                        "sure that the mobile phone and the computer are on the same network",
                ProcessOutputTypes.STDERR
            )
            killProcess()
        }
    }

    override fun onDestroy() {
        EventQueue.invokeLater {
            this.form?.dispose()
            this.form = null
        }
        server?.stop(0)
        server = null
        super.onDestroy()
    }

    companion object {
        private fun findHostAddress(): String? {
            try {
                val allNetInterfaces = NetworkInterface.getNetworkInterfaces()
                while (allNetInterfaces.hasMoreElements()) {
                    val netInterface = allNetInterfaces.nextElement()
                            as NetworkInterface
                    val addresses = netInterface.inetAddresses
                    while (addresses.hasMoreElements()) {
                        val ip = addresses.nextElement()
                                as InetAddress
                        if (ip is Inet4Address && !ip.isLoopbackAddress
                            && ip.hostAddress.indexOf(":") == -1
                        ) {
                            return ip.hostAddress
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }
}