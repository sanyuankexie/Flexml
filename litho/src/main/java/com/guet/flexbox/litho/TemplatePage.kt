package com.guet.flexbox.litho

import android.content.Context
import android.os.Looper
import androidx.annotation.RestrictTo
import androidx.annotation.WorkerThread
import com.facebook.litho.*
import com.guet.flexbox.EventBridge
import com.guet.flexbox.EventContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.PropsELContext
import com.guet.flexbox.litho.widget.ThreadChecker

class TemplatePage @WorkerThread internal constructor(
        builder: Builder
) : TreeManager(builder) {
    private val template: TemplateNode = requireNotNull(builder.template)
    private val data: Any? = builder.data
    private val eventBridge: EventBridge = builder.eventBridge
    private val size = Size()

    var eventTarget: EventContext?
        set(value) {
            eventBridge.target = value
        }
        get() {
            return eventBridge.target
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
        eventBridge.target = null
        if (host != null) {
            eventBridge.target = host.target
        }
    }

    override fun detach() {
        eventBridge.target = null
        super.detach()
    }

    override fun release() {
        eventBridge.target = null
        super.release()
    }

    @WorkerThread
    fun computeNewLayout() {
        val oldWidth = size.width
        val oldHeight = size.height
        val com = LithoBuildTool.build(
                template,
                eventBridge,
                PropsELContext(data),
                context
        ) as Component
        setRootAndSizeSpec(
                com,
                SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED),
                SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED),
                size
        )
        if (oldWidth != width || oldHeight != height) {
            val hostingView = lithoView as? HostingView ?: return
            hostingView.post { hostingView.requestLayout() }
        }
    }


    class Builder(
            private val context: ComponentContext
    ) : ComponentTree.Builder(context) {
        @JvmSynthetic
        @JvmField
        @RestrictTo(RestrictTo.Scope.LIBRARY)
        internal val eventBridge = EventBridge()

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
            val com = LithoBuildTool.build(
                    requireNotNull(template),
                    eventBridge,
                    PropsELContext(data),
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