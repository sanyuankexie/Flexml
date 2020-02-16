package com.guet.flexbox.litho.widget

import android.app.Application
import android.content.ComponentCallbacks
import android.content.Context
import android.content.res.Configuration
import androidx.annotation.MainThread
import androidx.recyclerview.widget.RecyclerView
import com.facebook.litho.ComponentContext
import com.facebook.litho.ComponentTree
import com.guet.flexbox.litho.LayoutThreadHandler
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

internal object PoolManager : ComponentCallbacks {

    override fun onConfigurationChanged(newConfig: Configuration?) {}

    override fun onLowMemory() {
        lithoViewPool.clear()
        synchronized(componentTreePool) {
            componentTreePool.clear()
        }
    }

    private val isInit = AtomicBoolean(false)

    @get:MainThread
    val lithoViewPool = RecyclerView.RecycledViewPool()

    private val componentTreePool = LinkedList<ComponentTree>()

    fun init(c: Context) {
        if (isInit.compareAndSet(false, true)) {
            val application = c.applicationContext as Application
            application.registerComponentCallbacks(this)
        }
    }

    fun releaseTree(tree: ComponentTree) {
        synchronized(componentTreePool) {
            if (componentTreePool.size < 10) {
                componentTreePool.push(tree)
            } else {
                tree.release()
            }
        }
    }

    fun obtainTree(c: ComponentContext): ComponentTree {
        return synchronized(componentTreePool) {
            if (componentTreePool.isEmpty()) {
                ComponentTree.create(c)
                        .layoutThreadHandler(LayoutThreadHandler)
                        .isReconciliationEnabled(false)
                        .build()
            } else {
                componentTreePool.pop()
            }
        }
    }
}