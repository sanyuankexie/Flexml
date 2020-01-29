package com.guet.flexbox.intellij

import com.intellij.ide.ApplicationLoadListener
import com.intellij.openapi.application.Application
import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.concurrency.AppExecutorUtil
import org.apache.commons.io.FileUtils
import java.io.File

class BinaryLoader : ApplicationLoadListener {

    override fun beforeApplicationLoaded(
            application: Application,
            configPath: String
    ) {
        compiler.loadFrom("bin/flexmlc.jar")
        mock.loadFrom("bin/handshake.jar")
    }

    companion object {

        private val LOG = Logger.getInstance(BinaryLoader::class.java)

        init {
            //clear
            File(FileUtils.getTempDirectory(), "flexml/bin").apply {
                if (exists()) {
                    delete()
                }
            }
        }

        private fun File.loadFrom(
                name: String
        ) {
            AppExecutorUtil.getAppExecutorService().execute {
                synchronized(this) {
                    println(this)
                    val input = BinaryLoader::class
                            .java.classLoader
                            .getResourceAsStream(
                                    name
                            )
                    if (input != null) {
                        FileUtils.copyInputStreamToFile(
                                input,
                                this
                        )
                    } else {
                        LOG.error("$name is null")
                    }
                }
            }
        }

        private fun createTempFile(name: String): File {
            return File(FileUtils.getTempDirectory(), name).apply {
                parentFile.mkdirs()
                if (exists()) {
                    delete()
                }
                createNewFile()
                deleteOnExit()
            }
        }

        private val compiler = createTempFile("flexml/bin/flexmlc.jar")

        val compilerJarPath: String
            get() = synchronized(compiler) { compiler.absolutePath }

        private val mock = createTempFile("flexml/bin/handshake.jar")

        val mockJarPath: String
            get() = synchronized(mock) { mock.absolutePath }
    }
}