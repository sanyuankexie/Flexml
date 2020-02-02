package com.guet.flexbox.intellij.service.impl

import com.guet.flexbox.intellij.isOnFlexmlFile
import com.guet.flexbox.intellij.service.JarStartupManager
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.jar.JarApplicationCommandLineState
import com.intellij.execution.jar.JarApplicationConfiguration
import com.intellij.execution.jar.JarApplicationConfigurationType
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.ide.plugins.PluginManager
import com.intellij.ide.plugins.cl.PluginClassLoader
import com.intellij.json.psi.JsonFile
import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.util.concurrency.AppExecutorUtil
import com.intellij.util.io.HttpRequests
import java.io.File
import java.io.IOException

class JarStartupManagerImpl(
        private val project: Project
) : JarStartupManager, FileEditorManagerListener {

    init {
        project.messageBus.connect().subscribe(
                        FileEditorManagerListener.FILE_EDITOR_MANAGER,
                        this
                )
    }

    @Volatile
    private var lastPort: Int? = null

    companion object {

        private fun runJar(
                project: Project,
                environment: ExecutionEnvironment,
                jar: String,
                args: String
        ): RunProfileState {
            return JarApplicationCommandLineState(
                    JarApplicationConfiguration(
                            project,
                            JarApplicationConfigurationType.getInstance(),
                            "Mock this package"
                    ).apply {
                        jarPath = jar
                        programParameters = args
                        vmParameters = "-Xmx64m -Xss256k -XX:ParallelGCThreads=1"
                    },
                    environment
            )
        }

        private fun findPluginRootPath(): File? {
            return JarStartupManagerImpl::class.java
                    .classLoader
                    .run { this as? PluginClassLoader }
                    ?.pluginId
                    ?.let {
                        PluginManager.getPlugin(it)
                    }?.path
        }

        private val PsiElement.isPackageJson: Boolean
            get() {
                val file = this.containingFile
                        ?.let { it as? JsonFile }
                val obj = file?.topLevelValue
                        ?.let { it as? JsonObject }
                if (file?.name != "package.json") {
                    return false
                }
                val template = obj?.findProperty("template")
                        ?: return false
                if (template.value?.let { it as? JsonStringLiteral }
                                ?.value?.let { file.parent?.findFile(it) }
                                ?.isOnFlexmlFile == true) {
                    return true
                }
                return false
            }

        private const val GROUP_ID = "Flexml plugin"
        private val compilerJarFile: File
        private val mockJarFile: File

        init {
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
                throw AssertionError()
            }
        }
    }

    override fun selectionChanged(event: FileEditorManagerEvent) {
        val lastPort = lastPort ?: return
        val newFile = event.newFile ?: return
        val targetFile = if (newFile.isDirectory) {
            newFile.findChild("package.json")
        } else {
            newFile.parent?.findChild("package.json")
        } ?: return
        val psiFile = PsiManager.getInstance(project)
                .findFile(targetFile) ?: return
        if (psiFile.isPackageJson) {
            val path = targetFile.path
            AppExecutorUtil.getAppExecutorService().execute {
                try {
                    HttpRequests.post(
                            "http://localhost:$lastPort/focus",
                            "application/json"
                    ).connectTimeout(500)
                            .write(path)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun runCompiler(
            environment: ExecutionEnvironment,
            input: String,
            output: String
    ): RunProfileState {
        return runJar(
                project,
                environment,
                compilerJarFile.absolutePath,
                "-i $input -o $output"
        )
    }

    override fun runMockServer(
            environment: ExecutionEnvironment,
            focus: String,
            port: Int
    ): RunProfileState {
        lastPort = port
        return runJar(
                project,
                environment,
                mockJarFile.absolutePath,
                "--server.port=$port --package.focus=$focus"
        )
    }
}