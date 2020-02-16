package com.guet.flexbox.litho.widget

import android.app.Application
import android.content.ComponentCallbacks
import android.content.Context
import android.content.res.Configuration
import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import androidx.recyclerview.widget.RecyclerView
import com.facebook.litho.ComponentContext
import com.facebook.litho.ComponentTree
import com.facebook.litho.LithoView
import com.guet.flexbox.build.Kit
import com.guet.flexbox.litho.LayoutThreadHandler
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

internal object LithoPoolsManager : ComponentCallbacks, Kit {

    override fun onConfigurationChanged(newConfig: Configuration?) {}

    override fun onLowMemory() {
        recycledLithoViewPool.clear()
        synchronized(componentTreePool) {
            componentTreePool.clear()
        }
    }

    private val isInit = AtomicBoolean(false)

    val LITHO_VIEW_TYPE = LithoView::class.java.name.hashCode()

    @get:MainThread
    val recycledLithoViewPool = RecyclerView.RecycledViewPool()

    private val componentTreePool = LinkedList<ComponentTree>()

    override fun init(c: Context) {
        if (isInit.compareAndSet(false, true)) {
            val application = c.applicationContext as Application
            application.registerComponentCallbacks(this)
        }
    }

    @MainThread
    fun obtainViewHolder(c: Context): LithoViewHolder {
        return recycledLithoViewPool.getRecycledView(LITHO_VIEW_TYPE)
                as? LithoViewHolder
                ?: LithoViewHolder(c)
    }

    @AnyThread
    fun releaseTree(tree: ComponentTree) {
        synchronized(componentTreePool) {
            if (componentTreePool.size < 10) {
                componentTreePool.push(tree)
            } else {
                tree.release()
            }
        }
    }

    @AnyThread
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