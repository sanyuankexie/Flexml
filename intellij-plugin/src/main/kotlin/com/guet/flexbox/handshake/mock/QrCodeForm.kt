package com.guet.flexbox.handshake.mock

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.guet.flexbox.handshake.util.ImageView
import com.guet.flexbox.handshake.util.fileIcon
import com.intellij.ui.JBColor
import com.intellij.util.IconUtil
import com.intellij.util.concurrency.AppExecutorUtil
import com.intellij.util.ui.UIUtil
import java.awt.EventQueue
import java.awt.Graphics2D
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit
import javax.swing.JFrame

class QrCodeForm(url: String) : JFrame() {

    init {
        iconImage = IconUtil.toImage(fileIcon)
        val size = 300
        title = "For playground"
        defaultCloseOperation = DISPOSE_ON_CLOSE
        setSize(size, size)
        isResizable = false
        val content = contentPane
        val panel = ImageView()
        AppExecutorUtil.getAppExecutorService().execute {
            val image = generateQR(url, size)
            EventQueue.invokeLater {
                panel.image = image
            }
        }
        panel.setSize(size, size)
        content.add(panel)
        val kit = Toolkit.getDefaultToolkit() //定义工具包
        val screenSize = kit.screenSize //获取屏幕的尺寸
        val screenWidth = screenSize.width //获取屏幕的宽
        val screenHeight = screenSize.height //获取屏幕的高
        setLocation(screenWidth / 2 - size / 2, screenHeight / 2 - size / 2)//设置窗口居中显示
        isVisible = true
        isAlwaysOnTop = true
        val cancel = object : WeakReference<JFrame>(this), Runnable {
            override fun run() {
                get()?.isAlwaysOnTop = false
            }
        }
        AppExecutorUtil.getAppScheduledExecutorService().schedule({
            EventQueue.invokeLater(cancel)
        }, 100, TimeUnit.MILLISECONDS)
    }


    companion object {
        /**
         * Generating a qr code with provided content
         *
         * @param content The content that should be in the QR
         * @return An Buffered image object containing the qr code
         */
        private fun generateQR(content: String, size: Int): BufferedImage {
            try {

                val hintMap = HashMap<EncodeHintType, ErrorCorrectionLevel>()
                hintMap[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.L
                val qrCodeWriter = QRCodeWriter()
                val byteMatrix = qrCodeWriter.encode(
                    content,
                    BarcodeFormat.QR_CODE, size, size, hintMap
                )
                val width = byteMatrix.width
                val image = UIUtil.createImage(
                    width,
                    width,
                    BufferedImage.TYPE_INT_RGB
                )
                val graphics = image.createGraphics() as Graphics2D
                graphics.color = JBColor.WHITE
                graphics.fillRect(0, 0, width, width)
                graphics.color = JBColor.BLACK

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
    }
}