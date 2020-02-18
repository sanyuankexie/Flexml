package com.guet.flexbox.handshake.ui

import com.guet.flexbox.handshake.utils.HostAddressFinder
import com.guet.flexbox.handshake.utils.QrcodeImageBuilder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import java.awt.Desktop
import java.awt.EventQueue
import java.awt.GraphicsEnvironment
import java.awt.Toolkit
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.net.URI
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.imageio.ImageIO
import javax.swing.JFrame
import kotlin.concurrent.schedule
import kotlin.system.exitProcess

class QrcodeForm : JFrame() {

    companion object {
        private const val WINDOW_SIZE: Int = 300
        private val logger = LoggerFactory.getLogger(QrcodeForm::class.java)
    }

    @Autowired
    private lateinit var attributes: ConcurrentHashMap<String, Any>

    private var lastHostAddress: String? = null

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
        isVisible = true
        isAlwaysOnTop = true
        EventQueue.invokeLater {
            isAlwaysOnTop = false
        }
    }

    fun start() {
        if (GraphicsEnvironment.isHeadless()) {
            return
        } else {
            val port = attributes["port"] as? Int ?: 8080
            Timer().schedule(0, 2000) {
                val host = HostAddressFinder.findHostAddress()
                if (host != lastHostAddress) {
                    lastHostAddress = host
                    val url = "http://$host:${port}"
                    logger.info("Network changed: $url")
                    val image = QrcodeImageBuilder
                            .buildImage(url, WINDOW_SIZE)
                    EventQueue.invokeLater {
                        imageView.image = image
                    }
                }
            }
            EventQueue.invokeLater {
                if (Desktop.isDesktopSupported()) {
                    val desktop = Desktop.getDesktop()
                    if (desktop.isSupported(Desktop.Action.BROWSE)) {
                        desktop.browse(URI("http://localhost:$port"))
                    }
                }
            }
        }
    }
}