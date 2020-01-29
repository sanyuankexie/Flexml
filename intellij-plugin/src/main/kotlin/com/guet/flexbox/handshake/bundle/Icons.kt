package com.guet.flexbox.handshake.bundle

import com.intellij.openapi.util.IconLoader

object Icons {

    val fileIcon by lazy {
        IconLoader.getIcon("/icons/icon_file.png")
    }

    val tagIcon by lazy {
        IconLoader.getIcon("/icons/icon_tag.png")
    }

    val colors: Map<String, Int> by lazy {
        HashMap<String, Int>().apply {
            this["black"] = 0xFF000000.toInt()
            this["darkgray"] = 0xFF444444.toInt()
            this["gray"] = 0xFF888888.toInt()
            this["lightgray"] = 0xFFCCCCCC.toInt()
            this["white"] = 0xFFFFFFFF.toInt()
            this["red"] = 0xFFFF0000.toInt()
            this["green"] = 0xFF00FF00.toInt()
            this["blue"] = 0xFF0000FF.toInt()
            this["yellow"] = 0xFFFFFF00.toInt()
            this["cyan"] = 0xFF00FFFF.toInt()
            this["magenta"] = 0xFFFF00FF.toInt()
            this["aqua"] = 0xFF00FFFF.toInt()
            this["fuchsia"] = 0xFFFF00FF.toInt()
            this["lime"] = 0xFF00FF00.toInt()
            this["maroon"] = 0xFF800000.toInt()
            this["navy"] = 0xFF000080.toInt()
            this["olive"] = 0xFF808000.toInt()
            this["purple"] = 0xFF800080.toInt()
            this["silver"] = 0xFFC0C0C0.toInt()
            this["teal"] = 0xFF008080.toInt()
        }
    }

}