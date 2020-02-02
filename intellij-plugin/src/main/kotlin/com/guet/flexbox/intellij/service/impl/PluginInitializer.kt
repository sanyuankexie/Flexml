package com.guet.flexbox.intellij.service.impl

import com.intellij.ide.ApplicationLoadListener
import com.intellij.ide.plugins.PluginManager
import com.intellij.ide.plugins.cl.PluginClassLoader
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.Application
import java.io.File

class PluginInitializer : ApplicationLoadListener {

    private lateinit var compilerJarFile: File
    private lateinit var mockJarFile: File

    val compilerJarFilePath: String
        get() = compilerJarFile.absolutePath
    val mockJarFilePath: String
        get() = mockJarFile.absolutePath

    override fun beforeApplicationLoaded(
            application: Application,
            configPath: String) {
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
            val cl = javaClass.classLoader as PluginClassLoader
            val pluginId = cl.pluginId.idString
            PluginManager.disablePlugin(pluginId)
        }
    }

    companion object {

        private const val GROUP_ID = "Flexml plugin"

        private fun findPluginRootPath(): File? {
            val cl = JarStartupManagerImpl::class.java
                    .classLoader as PluginClassLoader
            return PluginManager.getPlugins().firstOrNull {
                it.pluginId == cl.pluginId
            }?.path
        }
    }
}