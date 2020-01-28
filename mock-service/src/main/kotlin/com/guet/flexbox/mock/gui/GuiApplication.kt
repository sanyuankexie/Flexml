package com.guet.flexbox.mock.gui

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.awt.Color
import java.awt.EventQueue
import java.awt.Graphics2D
import java.awt.Toolkit
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.awt.image.BufferedImage
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import javax.swing.JFrame
import kotlin.concurrent.thread
import kotlin.system.exitProcess

object GuiApplication {

    private const val windowSize: Int = 300

    private var lastHostAddress: String? = null

    fun run() {
        EventQueue.invokeLater {
            val imageView = createImageWindow()
            monitorNetworkChange(imageView)
        }
    }

    private fun monitorNetworkChange(imageView: ImageView) {
        thread {
            while (true) {
                val content = findHostAddress()
                if (content != null && content != lastHostAddress) {
                    lastHostAddress = content
                    val image = generateQrCodeImage(
                            content, windowSize
                    )
                    EventQueue.invokeLater {
                        imageView.image = image
                    }
                    Thread.sleep(2000)
                }
                Thread.sleep(1000)
            }
        }
    }

    private fun createImageWindow(): ImageView {
        val imageView = ImageView()
        imageView.setSize(windowSize, windowSize)
        JFrame().apply {
            title = "For playground"
            defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
            setSize(windowSize, windowSize)
            isResizable = false
            val content = contentPane
            content.add(imageView)
            val kit = Toolkit.getDefaultToolkit()
            //获取屏幕的尺寸
            val screenSize = kit.screenSize
            //获取屏幕的宽
            val screenWidth = screenSize.width
            //获取屏幕的高
            val screenHeight = screenSize.height
            //设置窗口居中显示
            setLocation(
                    screenWidth / 2 - windowSize / 2,
                    screenHeight / 2 - windowSize / 2
            )
            addWindowListener(object : WindowAdapter() {
                override fun windowClosed(e: WindowEvent?) {
                    exitProcess(0)
                }
            })
            isVisible = true
            isAlwaysOnTop = true
            EventQueue.invokeLater {
                isAlwaysOnTop = false
            }
        }
        return imageView
    }

    @Suppress("SameParameterValue")
    private fun generateQrCodeImage(
            content: String,
            size: Int
    ): BufferedImage {
        try {
            val hintMap = HashMap<EncodeHintType, ErrorCorrectionLevel>()
            hintMap[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.L
            val qrCodeWriter = QRCodeWriter()
            val byteMatrix = qrCodeWriter.encode(
                    content,
                    BarcodeFormat.QR_CODE, size, size, hintMap
            )
            val width = byteMatrix.width
            val image = BufferedImage(
                    width,
                    width,
                    BufferedImage.TYPE_INT_RGB
            )
            val graphics = image.createGraphics() as Graphics2D
            graphics.color = Color.WHITE
            graphics.fillRect(0, 0, width, width)
            graphics.color = Color.BLACK

            for (i in 0 until width) {
                for (j in 0 until width) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1)
                    }
                }
            }
            return image
        } catch (e: WriterException) {
            throw RuntimeException(e)
        }
    }

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