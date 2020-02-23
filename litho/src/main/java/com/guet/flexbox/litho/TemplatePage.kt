package com.guet.flexbox.litho

import android.content.Context
import android.os.Looper
import androidx.annotation.AnyThread
import androidx.annotation.RestrictTo
import androidx.annotation.WorkerThread
import com.facebook.litho.*
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.litho.widget.ThreadChecker
import com.guet.flexbox.transaction.dispatch.ActionBridge
import com.guet.flexbox.transaction.dispatch.ActionTarget

class TemplatePage @WorkerThread internal constructor(
        builder: Builder
) : TreeManager(builder) {
    private val template: TemplateNode = requireNotNull(builder.template)
    private val data: Any? = builder.data
    private val actionBridge: ActionBridge = builder.actionBridge
    private val size = Size()
    private val computeRunnable = Runnable {
        val oldWidth = size.width
        val oldHeight = size.height
        val com = LithoBuildTool.buildRoot(
                template,
                data,
                actionBridge,
                context
        ) as Component
        setRootAndSizeSpec(
                com,
                SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED),
                SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED),
                size
        )
        if (oldWidth != width || oldHeight != height) {
            val hostingView = lithoView as? HostingView ?: return@Runnable
            hostingView.post { hostingView.requestLayout() }
        }
    }

    internal var actionTarget: ActionTarget?
        set(value) {
            actionBridge.target = value
        }
        get() {
            return actionBridge.target
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

    override fun attach() {
        super.attach()
        val host = lithoView as? HostingView
        actionBridge.target = null
        if (host != null) {
            actionBridge.target = host.target
        }
    }

    override fun detach() {
        actionBridge.target = null
        super.detach()
    }

    override fun release() {
        actionBridge.target = null
        super.release()
    }

    @AnyThread
    fun computeNewLayout() {
        InternalThreads.runOnAsyncThread(computeRunnable)
    }

    class Builder(
            private val context: ComponentContext
    ) : ComponentTree.Builder(context) {
        @JvmSynthetic
        @JvmField
        @RestrictTo(RestrictTo.Scope.LIBRARY)
        internal val actionBridge = ActionBridge()

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
            val com = LithoBuildTool.buildRoot(
                    requireNotNull(template),
                    data,
                    actionBridge,
                    context
            ) as Component
            super.withRoot(ThreadChecker.create(context)
                    .component((com))
                    .build())
            logger(null, com.simpleName)
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