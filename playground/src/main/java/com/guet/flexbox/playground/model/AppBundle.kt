package com.guet.flexbox.playground.model

import android.content.Context
import android.os.SystemClock
import com.google.gson.Gson
import com.guet.flexbox.TemplateNode
import com.orhanobut.logger.Logger
import java.io.FileNotFoundException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

class AppBundle(
        val sourceCodes: Map<String, String>,
        val templateNode: Map<String, TemplateNode>,
        val dataSource: Map<String, Map<String, Any>>
) {
    companion object {
        fun loadAppBundle(
                c: Context,
                executor: ExecutorService,
                vararg args: String
        ): AppBundle {
            val start = SystemClock.uptimeMillis()
            val gson = Gson()
            val dataSourcesF = args.map { url ->
                url to executor.submit<Map<String, Any>> {
                    val jsonUrl = "$url.json"
                    try {
                        c.assets.open(jsonUrl).use {
                            @Suppress("UNCHECKED_CAST")
                            gson.fromJson(it.reader(), Map::class.java)
                                    as Map<String, Any>
                        }
                    } catch (e: FileNotFoundException) {
                        emptyMap()
                    }
                }
            }
            val sourcesF = args.map {
                it to executor.submit<String> {
                    c.assets.open("$it.flexml").use { stream ->
                        stream.reader().buffered().readText()
                    }
                }
            }
            val sources = HashMap<String, String>()
            val templatesF = ArrayList<
                    Pair<String, Future<TemplateNode>>
                    >(sourcesF.size)
            sourcesF.forEach { source ->
                val text = source.second.get()
                sources[source.first] = text
                templatesF.add(source.first to executor
                        .submit<TemplateNode> {
                            TemplateCompiler.compile(text)
                        })
            }
            val dataSources = dataSourcesF.map {
                it.first to it.second.get()
            }.toMap()
            val templates = templatesF.map {
                it.first to it.second.get()
            }.toMap()
            Logger.d("AppBundle:load time: " + (SystemClock.uptimeMillis() - start))
            return AppBundle(sources, templates, dataSources)
        }
    }
}