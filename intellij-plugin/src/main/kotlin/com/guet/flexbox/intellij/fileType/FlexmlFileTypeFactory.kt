package com.guet.flexbox.intellij.fileType

import com.intellij.openapi.fileTypes.FileTypeConsumer
import com.intellij.openapi.fileTypes.FileTypeFactory

class FlexmlFileTypeFactory : FileTypeFactory() {
    override fun createFileTypes(consumer: FileTypeConsumer) {
        consumer.consume(FlexmlFileType)
    }
}