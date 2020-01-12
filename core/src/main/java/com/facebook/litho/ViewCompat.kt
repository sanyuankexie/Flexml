package com.facebook.litho

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.guet.flexbox.litho.ChildComponent

class ViewCompat private constructor(
        private val view: View
) : Component(
        view.javaClass.simpleName,
        System.identityHashCode(view)
) {

    override fun isEquivalentTo(other: Component): Boolean = this === other

    override fun canMeasure(): Boolean = true

    override fun hasChildLithoViews(): Boolean {
        return view is ViewGroup && (0 until view.childCount).any {
            view.getChildAt(it) is LithoView
        }
    }

    override fun onMeasure(
            c: ComponentContext,
            layout: ComponentLayout,
            widthSpec: Int,
            heightSpec: Int,
            size: Size
    ) {
        val layoutParams = ViewGroup.LayoutParams(
                size.width,
                size.height
        )
        view.layoutParams = layoutParams
        if (view.visibility == View.GONE) {
            size.width = 0
            size.height = 0
        } else {
            view.measure(widthSpec, heightSpec)
            size.width = view.measuredWidth
            size.height = view.measuredHeight
        }
    }

    override fun getMountType(): MountType = MountType.VIEW

    override fun createMountContent(c: Context): View = view

    class Builder : Component.Builder<Builder>() {

        private lateinit var viewCompat: ViewCompat

        fun init(context: ComponentContext, component: ViewCompat) {
            super.init(context, 0, 0, component)
            viewCompat = component
        }

        override fun setComponent(component: Component) {
            viewCompat = component as ViewCompat
        }

        override fun getThis(): Builder {
            return this
        }

        fun children(children: List<ChildComponent>) {
            val view = viewCompat.view
            val c = context
            if (view is ViewGroup && c != null && children.isNotEmpty()) {
                children.map {
                    val attributeSet = it.attrs
                            .createAndroidAttributeSet(c.androidContext)
                    val prams = view
                            .generateLayoutParams(attributeSet)
                    val widget = it.widget
                    val childView = if (widget is ViewCompat) {
                        widget.view
                    } else {
                        LithoView.create(c, widget)
                    }
                    childView.apply {
                        layoutParams = prams
                    }
                }.forEach {
                    view.addView(it)
                }
            }
        }

        override fun build(): ViewCompat {
            return viewCompat
        }

    }

    override fun poolSize(): Int {
        return 0
    }

    companion object {

        fun create(
                componentContext: ComponentContext,
                viewType: Class<out View>,
                attrs: AttributeSet
        ): Builder {
            val builder = Builder()
            val constructor = viewType
                    .getConstructor(
                            Context::class.java,
                            AttributeSet::class.java
                    )
            val view = constructor.newInstance(
                    componentContext.androidContext,
                    attrs
            )
            val viewCompat = ViewCompat(view)
            builder.init(componentContext, viewCompat)
            return builder
        }
    }
}