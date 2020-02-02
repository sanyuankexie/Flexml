package com.guet.flexbox.intellij.service.impl

import com.guet.flexbox.intellij.isOnFlexmlFile
import com.guet.flexbox.intellij.service.JarStartupManager
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.jar.JarApplicationCommandLineState
import com.intellij.execution.jar.JarApplicationConfiguration
import com.intellij.execution.jar.JarApplicationConfigurationType
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.ide.ApplicationLoadListener
import com.intellij.json.psi.JsonFile
import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.util.concurrency.AppExecutorUtil
import com.intellij.util.io.HttpRequests
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

    private val initializer by lazy {
        ApplicationLoadListener.EP_NAME.findExtension(
                PluginInitializer::class.java
        ) ?: throw InternalError("Plugin internal error")
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
            val factory = JarApplicationConfigurationType
                    .getInstance()
                    .configurationFactories
                    .first()
            return JarApplicationCommandLineState(
                    JarApplicationConfiguration(
                            project,
                            factory,
                            "Mock this package"
                    ).apply {
                        jarPath = jar
                        programParameters = args
                        vmParameters = "-Xmx64m -Xss256k -XX:ParallelGCThreads=1"
                    },
                    environment
            )
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
                initializer.compilerJarFilePath,
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
                initializer.mockJarFilePath,
                "--server.port=$port --package.focus=$focus"
        )
    }
}