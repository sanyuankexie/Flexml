package com.guet.flexbox.handshake.compile

import com.google.gson.internal.Streams
import com.google.gson.stream.JsonWriter
import com.guet.flexbox.handshake.util.EmbeddedHandler
import com.intellij.execution.process.ProcessOutputTypes
import com.intellij.util.concurrency.AppExecutorUtil
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class CompilerHandler(
    private val configuration: FlexmlCompileRunConfiguration
) : EmbeddedHandler() {

    override fun onStart() {
        val template = configuration.state?.template
        if (template.isNullOrEmpty()) {
            notifyTextAvailable("No target file found\n", ProcessOutputTypes.STDERR)
            killProcess()
            return
        }
        val output = configuration.state?.output
        val fixedOutput = if (output.isNullOrEmpty()) {
            File(File(template).parentFile, "out.json").absolutePath
        } else {
            output
        }
        AppExecutorUtil.getAppExecutorService().submit {
            try {
                val file = File(fixedOutput)
                if (!file.exists()) {
                    file.createNewFile()
                }
                JsonWriter(file.writer().buffered()).apply {
                    isLenient = true
                }.use { writer ->
                    val jsonObject = Compiler.compile(template)
                    Streams.write(jsonObject, writer)
                }
            } catch (e: Exception) {
                val w = StringWriter()
                e.printStackTrace(PrintWriter(w))
                notifyTextAvailable(w.toString() + "\n", ProcessOutputTypes.STDERR)
            } finally {
                killProcess()
            }
        }
    }
}