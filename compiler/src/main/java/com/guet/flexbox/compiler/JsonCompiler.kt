package com.guet.flexbox.compiler

import com.google.gson.JsonObject
import com.google.gson.internal.Streams
import com.google.gson.stream.JsonWriter
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Options
import java.io.File
import kotlin.system.exitProcess

object JsonCompiler : Compiler<JsonObject>(JsonNodeFactory) {
    @JvmStatic
    fun main(args: Array<String>) {
        val parser = DefaultParser()
        val options = Options().apply {
            addOption("i", "input", true, "布局源文件")
            addOption("o", "output", true, "输出文件")
        }
        val commandLine = parser.parse(options, args)
        var input: File? = null
        var output: File? = null
        if (commandLine.hasOption("i")) {
            input = File(commandLine.getOptionValue("i"))
        }
        if (commandLine.hasOption("o")) {
            output = File(commandLine.getOptionValue("o"))
        }
        if (input != null && output != null) {
            val ip = input.absoluteFile
            if (!ip.endsWith(".flexml") && !ip.endsWith(".xml")) {
                System.err.println("输入文件后准名不合规范（应该为.flexml或.xml）")
                exitProcess(-2)
            }
            val op = output.absoluteFile
            if (!op.endsWith(".json")) {
                System.err.println("输出文件后准名不合规范（应该为.json）")
                exitProcess(-3)
            }
            if (!output.exists()) {
                output.createNewFile()
            }
            JsonWriter(output.writer().buffered()).apply {
                isLenient = true
            }.use { writer ->
                val jsonObject = JsonCompiler.compile(input)
                Streams.write(jsonObject, writer)
            }
            println("编译完成，在：$op")
            exitProcess(0)
        } else {
            System.err.println("没有输出或输出文件的参数")
            exitProcess(-1)
        }
    }
}