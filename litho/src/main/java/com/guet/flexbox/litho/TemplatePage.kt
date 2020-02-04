package com.guet.flexbox.litho

import android.content.Context
import android.os.Looper
import androidx.annotation.RestrictTo
import androidx.annotation.WorkerThread
import com.facebook.litho.*
import com.guet.flexbox.EventBridge
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.PropsELContext

class TemplatePage @WorkerThread internal constructor(
        builder: Builder
) : ComponentTree2(builder) {
    internal val template: TemplateNode = builder.template
    internal val data: Any? = builder.data
    internal val eventBridge = builder.eventBridge

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
        @field:RestrictTo(RestrictTo.Scope.LIBRARY)
        internal lateinit var template: TemplateNode
        @field:RestrictTo(RestrictTo.Scope.LIBRARY)
        internal var data: Any? = null
        @field:RestrictTo(RestrictTo.Scope.LIBRARY)
        internal val eventBridge = EventBridge()

        @Deprecated(
                message = "use template() and data()",
                level = DeprecationLevel.HIDDEN
        )
        override fun withRoot(root: Component?): ComponentTree.Builder {
            throw IllegalStateException()
        }

        @Deprecated(
                message = "framework use default thread pool",
                level = DeprecationLevel.HIDDEN
        )
        override fun layoutThreadHandler(handler: LithoHandler?): ComponentTree.Builder {
            throw IllegalStateException()
        }

        @Deprecated(
                message = "framework use default thread pool",
                level = DeprecationLevel.HIDDEN
        )
        override fun layoutThreadLooper(looper: Looper?): ComponentTree.Builder {
            throw IllegalStateException()
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
            super.withRoot(LithoBuildTool.build(
                    template,
                    eventBridge,
                    PropsELContext(data),
                    context
            ) as Component)
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