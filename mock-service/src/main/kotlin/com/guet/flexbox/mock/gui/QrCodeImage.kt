package com.guet.flexbox.mock.gui

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage

object QrCodeImage {
    fun generate(
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
}