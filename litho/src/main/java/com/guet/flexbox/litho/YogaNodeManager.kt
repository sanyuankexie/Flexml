@file:Suppress("PackageDirectoryMismatch")

package com.facebook.yoga

import android.content.Context
import com.facebook.litho.NodeConfig
import com.facebook.soloader.SoLoader
import com.guet.flexbox.build.Kit
import java.lang.ref.PhantomReference
import java.lang.ref.ReferenceQueue
import kotlin.concurrent.thread

internal object YogaNodeManager : Kit, NodeConfig.InternalYogaNodeFactory {

    private val queue = ReferenceQueue<YogaNode>()
    private val entries = HashMap<PhantomReference<RefQueueFreeYogaNode>, Long>()

    init {
        thread(name = "YogaNodeFree", isDaemon = true) {
            while (true) {
                val entry = queue.remove()
                synchronized(entries) {
                    val instance = entries.remove(entry) ?: 0
                    if (instance != 0L) {
                        YogaNative.jni_YGNodeFree(instance)
                    }
                }
            }
        }
    }

    override fun init(c: Context) {
        SoLoader.init(c, false)
        NodeConfig.sYogaNodeFactory = this
    }

    override fun create(config: YogaConfig): YogaNode {
        val node = RefQueueFreeYogaNode(config)
        val ref = PhantomReference(node, queue)
        synchronized(entries) {
            entries[ref] = node.nativeInstance
        }
        return node
    }

    private class RefQueueFreeYogaNode(
            config: YogaConfig
    ) : YogaNodeJNIBase(config) {
        val nativeInstance: Long
            get() = mNativePointer
    }
}