package com.guet.flexbox.litho

import android.content.Context
import android.os.Looper
import android.util.ArrayMap
import androidx.annotation.AnyThread
import androidx.annotation.RestrictTo
import androidx.annotation.WorkerThread
import com.facebook.litho.*
import com.guet.flexbox.PageContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.ScopeContext
import com.guet.flexbox.eventsystem.EventDispatcher
import com.guet.flexbox.eventsystem.EventTarget
import com.guet.flexbox.eventsystem.event.RefreshPageEvent
import com.guet.flexbox.eventsystem.event.TemplateEvent
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.ObjectContext

class TemplatePage @WorkerThread internal constructor(
        builder: Builder
) : TreeManager(builder) {
    private inner class LocalEventTarget(
            private val dispatcher: EventDispatcher
    ) : EventTarget {
        override fun dispatchEvent(e: TemplateEvent<*, *>) {
            if (e is RefreshPageEvent) {
                computeNewLayout()
            }
            dispatcher.dispatchEvent(e)
        }
    }

    private val dispatcher = EventDispatcher()
    private val size = Size()
    private val template: TemplateNode = requireNotNull(builder.template)
    private val localTarget = LocalEventTarget(dispatcher)
    private val dataContext: JexlContext

    init {
        dataContext = ScopeContext(
                ArrayMap(),
                ObjectContext<Any>(
                        LithoBuildTool.engine,
                        builder.data
                )
        )
        dataContext.set("pageContext", PageContext(dataContext, localTarget))
    }

    private val computeRunnable = Runnable {
        val oldWidth = size.width
        val oldHeight = size.height
        val com = LithoBuildTool.buildRoot(
                template,
                dataContext,
                localTarget,
                context
        ) as Component
        setRootAndSizeSpec(
                com,
                SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED),
                SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED),
                size
        )
        if (oldWidth != width || oldHeight != height) {
            val hostingView = lithoView
                    as? HostingView
                    ?: return@Runnable
            hostingView.post { hostingView.requestLayout() }
        }
    }

    init {
        computeRunnable.run()
    }

    val width: Int
        get() = size.width

    val height: Int
        get() = size.height

    init {
        super.setSizeSpec(
                SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED),
                SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED),
                size
        )
    }

    internal var outerTarget: EventTarget?
        set(value) {
            dispatcher.target = value
        }
        get() = dispatcher.target

    override fun attach() {
        super.attach()
        val host = lithoView as? HostingView
        outerTarget = null
        if (host != null) {
            outerTarget = host.eventBus
        }
    }

    override fun detach() {
        outerTarget = null
        super.detach()
    }

    override fun release() {
        outerTarget = null
        super.release()
    }

    @AnyThread
    private fun computeNewLayout() {
        InternalThreads.runOnAsyncThread(computeRunnable)
    }

    class Builder(
            private val context: ComponentContext
    ) : ComponentTree.Builder(context) {

        @JvmSynthetic
        @JvmField
        @RestrictTo(RestrictTo.Scope.LIBRARY)
        internal var template: TemplateNode? = null
        @JvmSynthetic
        @JvmField
        @RestrictTo(RestrictTo.Scope.LIBRARY)
        internal var data: Any? = null

        @Deprecated(
                message = "use template() and data()",
                level = DeprecationLevel.HIDDEN
        )
        @RestrictTo(RestrictTo.Scope.LIBRARY)
        override fun withRoot(root: Component?): ComponentTree.Builder {
            throw IllegalStateException("use template() and data()")
        }

        @Deprecated(
                message = "framework use default thread pool",
                level = DeprecationLevel.HIDDEN
        )
        @RestrictTo(RestrictTo.Scope.LIBRARY)
        override fun layoutThreadHandler(handler: LithoHandler?): ComponentTree.Builder {
            throw IllegalStateException("framework use default thread pool")
        }

        @Deprecated(
                message = "framework use default thread pool",
                level = DeprecationLevel.HIDDEN
        )
        @RestrictTo(RestrictTo.Scope.LIBRARY)
        override fun layoutThreadLooper(looper: Looper?): ComponentTree.Builder {
            throw IllegalStateException("framework use default thread pool")
        }

        fun template(templateNode: TemplateNode): Builder {
            this.template = templateNode
            return this
        }

        fun data(data: Any?): Builder {
            this.data = data
            return this
        }

        @WorkerThread
        override fun build(): TemplatePage {
            super.layoutThreadHandler(LayoutThreadHandler)
            super.withRoot(Row.create(context).build())
            isReconciliationEnabled(false)
            return TemplatePage(this)
        }
    }

    companion object {
        @JvmStatic
        fun create(context: Context): Builder {
            //由于预加载时间点于使用时间点不同，所以ctx必须是app
            return Builder(ComponentContext(context.applicationContext))
        }
    }
}