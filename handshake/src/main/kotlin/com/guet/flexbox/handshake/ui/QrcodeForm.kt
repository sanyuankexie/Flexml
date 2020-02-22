package com.guet.flexbox.handshake.ui

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import java.awt.Desktop
import java.awt.EventQueue
import java.awt.Toolkit
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.net.URI
import javax.imageio.ImageIO
import javax.swing.JFrame
import kotlin.system.exitProcess


class QrcodeForm : JFrame() {

    private companion object {
        private const val WINDOW_SIZE: Int = 300
    }

    private val logger = LoggerFactory.getLogger(QrcodeForm::class.java)

    @Autowired
    private lateinit var environment: Environment

    private val imageView: ImageView

    init {
        javaClass.classLoader
                .getResourceAsStream("static/icon.png")
                ?.use {
                    iconImage = ImageIO.read(it)
                }
        title = "Handshake"
        defaultCloseOperation = DISPOSE_ON_CLOSE
        setSize(WINDOW_SIZE, WINDOW_SIZE)
        isResizable = false
        val content = contentPane
        imageView = ImageView()
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
                screenWidth / 2 - WINDOW_SIZE / 2,
                screenHeight / 2 - WINDOW_SIZE / 2
        )
        addWindowListener(object : WindowAdapter() {
            override fun windowClosed(e: WindowEvent?) {
                exitProcess(0)
            }
        })

    }

    fun start() {
        EventQueue.invokeLater {
            isVisible = true
            isAlwaysOnTop = true
            if (Desktop.isDesktopSupported()) {
                val desktop = Desktop.getDesktop()
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    val port = environment.getProperty("local.server.port")
                    desktop.browse(URI("http://localhost:$port"))
                }
            }
            isAlwaysOnTop = false
        }
    }

    fun notifyChanged(host: String) {
        val port = environment.getProperty("local.server.port")
        val url = "http://$host:${port}"
        logger.info("now url:${url}")
        val image = QrcodeImageUtils.buildImage(url, WINDOW_SIZE)
        EventQueue.invokeLater {
            imageView.image = image
        }
    }
}