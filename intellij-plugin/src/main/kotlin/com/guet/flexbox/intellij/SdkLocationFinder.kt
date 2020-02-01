package com.guet.flexbox.intellij

import com.intellij.ide.ApplicationLoadListener
import com.intellij.ide.plugins.PluginManager
import com.intellij.ide.plugins.cl.PluginClassLoader
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.Application
import java.io.File

class SdkLocationFinder : ApplicationLoadListener {

    override fun beforeApplicationLoaded(
            application: Application,
            configPath: String
    ) {
        val root = findPluginRootPath()
        if (root != null) {
            compilerJarFile = File(root, "lib/flexmlc.jar")
            mockJarFile = File(root, "lib/handshake.jar")
        } else {
            Notification(
                    GROUP_ID,
                    "插件在加载时发生错误",
                    "插件的根目录没有找到，请重新尝试安装插件以解决问题",
                    NotificationType.ERROR,
                    null
            ).notify(null)
        }
    }

    companion object {

        private lateinit var compilerJarFile: File
        private lateinit var mockJarFile: File

        val compilerPath: String
            get() = compilerJarFile.path
        val mockPath: String
            get() = mockJarFile.path

        private const val GROUP_ID = "Flexml plugin"

        private fun findPluginRootPath(): File? {
            return SdkLocationFinder::class.java
                    .classLoader
                    .run { this as? PluginClassLoader }
                    ?.pluginId
                    ?.let {
                        PluginManager.getPlugin(it)
                    }?.path
        }

    }
}