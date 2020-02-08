package com.guet.flexbox.litho

import android.content.Context
import android.os.Looper
import androidx.annotation.RestrictTo
import androidx.annotation.WorkerThread
import com.facebook.litho.*
import com.guet.flexbox.EventBridge
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.PropsELContext
import com.guet.flexbox.litho.widget.Root

class TemplatePage @WorkerThread internal constructor(
        builder: Builder
) : TreeManager(builder) {
    internal val template: TemplateNode = requireNotNull(builder.template)
    internal val data: Any? = builder.data
    internal val eventBridge: EventBridge = builder.eventBridge

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
        @JvmSynthetic
        @JvmField
        @RestrictTo(RestrictTo.Scope.LIBRARY)
        internal val eventBridge = EventBridge()

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
        @Suppress("DEPRECATED_JAVA_ANNOTATION")
        @java.lang.Deprecated
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
            super.withRoot(Root.create(context)
                    .component((LithoBuildTool.build(
                            requireNotNull(template),
                            eventBridge,
                            PropsELContext(data),
                            context
                    ) as Component)).build().apply {
                        logger(null, simpleName)
                    })
            return TemplatePage(this)
        }
    }

    companion object {
        @JvmStatic
        fun create(context: Context): Builder {
            return Builder(ComponentContext(context))
        }
    }
}