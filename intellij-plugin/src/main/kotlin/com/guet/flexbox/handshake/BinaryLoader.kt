package com.guet.flexbox.handshake

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import java.io.File
import java.nio.file.Files
import java.util.*

class BinaryLoader : StartupActivity.Background {

    override fun runActivity(project: Project) {
        synchronized(lock) {
            BinaryLoader::class.java.classLoader.getResourceAsStream(
                    "bin/flexmlc.jar"
            )?.use {
                Files.copy(it, compiler.toPath())
            }
            BinaryLoader::class.java.classLoader.getResourceAsStream(
                    "bin/mock-service.jar"
            )?.use {
                Files.copy(it, mock.toPath())
            }
        }
    }

    companion object {

        private val lock = Any()

        private val compiler = File.createTempFile(
                UUID.randomUUID().toString(),
                "flexmlc.jar"
        ).apply { deleteOnExit() }

        val compilerJarPath: String
            get() = synchronized(lock) { compiler.absolutePath }

        private val mock = File.createTempFile(
                UUID.randomUUID().toString(),
                "mock-service.jar"
        ).apply { deleteOnExit() }

        val mockJarPath: String
            get() = synchronized(lock) { mock.absolutePath }
    }
}