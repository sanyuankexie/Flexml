package com.guet.flexbox.litho

import android.content.Context
import android.os.Looper
import androidx.annotation.RestrictTo
import androidx.annotation.WorkerThread
import com.facebook.litho.*
import com.guet.flexbox.EventBridge
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.PropsELContext
import com.guet.flexbox.litho.widget.ThreadChecker

class TemplatePage @WorkerThread internal constructor(
        builder: Builder
) : TreeManager(builder) {
    internal val template: TemplateNode = requireNotNull(builder.template)
    internal val eventBridge: EventBridge = builder.eventBridge
    internal val size: Size = builder.size

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
        internal val size = Size()
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
            val page = TemplatePage(this)
            page.setSizeSpec(
                    SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED),
                    SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED),
                    size
            )
            return page
        }
    }

    companion object {
        @JvmStatic
        fun create(context: Context): Builder {
            return Builder(ComponentContext(context))
        }
    }
}