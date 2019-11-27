package com.guet.flexbox.build

import android.graphics.Color
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.RestrictTo
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.drawable.ComparableColorDrawable
import com.facebook.litho.drawable.ComparableDrawable
import com.guet.flexbox.DynamicBox
import com.guet.flexbox.NodeInfo
import com.guet.flexbox.el.LambdaExpression
import com.guet.flexbox.el.PropsELContext
import com.guet.flexbox.widget.AsyncLazyDrawable
import com.guet.flexbox.widget.BackgroundDrawable
import com.guet.flexbox.widget.NoOpDrawable
import java.util.*

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
internal abstract class WidgetFactory<T : Component.Builder<*>> : Mapper<T>(), Transform {

    override val mappings by CommonMappings.newMappings<T>()

    final override fun transform(
            c: ComponentContext,
            data: PropsELContext,
            nodeInfo: NodeInfo,
            upperVisibility: Int
    ): List<Component>? {
        val component = create(c, data, nodeInfo, upperVisibility)
        if (component != null) {
            return Collections.singletonList(component)
        }
        return null
    }

    protected abstract fun onCreateWidget(
            c: ComponentContext,
            data: PropsELContext,
            attrs: Map<String, String>?,
            visibility: Int
    ): T

    protected open fun onInstallChildren(
            owner: T,
            c: ComponentContext,
            data: PropsELContext,
            attrs: Map<String, String>?,
            children: List<Component>?,
            visibility: Int) {
    }

    @CallSuper
    protected open fun onLoadStyles(
            owner: T,
            c: ComponentContext,
            pager: PropsELContext,
            attrs: Map<String, String>?,
            visibility: Int
    ) {
        val display = visibility == View.VISIBLE
        if (!attrs.isNullOrEmpty()) {
            owner.applyEvent(c, pager, attrs, visibility)
            if (display) {
                owner.applyBackground(c, pager, attrs)
            }
            for ((key, value) in attrs) {
                mappings[key]?.invoke(owner, pager, attrs, display, value)
            }
        }
    }

    private fun create(
            c: ComponentContext,
            data: PropsELContext,
            nodeInfo: NodeInfo,
            upperVisibility: Int
    ): Component? {
        val attrs = nodeInfo.attrs
        val childrenNodes = nodeInfo.children
        val visibility = calculateVisibility(c, data, attrs, upperVisibility)
        if (visibility == View.GONE) {
            return null
        }
        val builder = onCreateWidget(c, data, attrs, visibility)
        onLoadStyles(builder, c, data, attrs, visibility)
        onInstallChildren(builder, c, data, attrs, childrenNodes?.map {
            data.inflate(c, it, visibility)
        }?.flatten(), visibility)
        return builder.build()
    }

    private fun T.applyBackground(
            c: ComponentContext,
            data: PropsELContext,
            attrs: Map<String, String>
    ) {
        val borderRadius = data.tryGetValue(attrs["borderRadius"], 0).toPx()
        val borderWidth = data.tryGetValue(attrs["borderWidth"], 0).toPx()
        val borderColor = data.tryGetColor(attrs["borderColor"], Color.TRANSPARENT)
        var backgroundDrawable: ComparableDrawable? = null
        val background = attrs["background"]
        if (background != null) {
            try {
                backgroundDrawable = ComparableColorDrawable.create(data.getColor(background))
            } catch (e: Exception) {
                val backgroundELResult = data.tryGetValue(background, "")
                when (val model = parseUrl(c.androidContext, backgroundELResult)) {
                    is ComparableDrawable -> {
                        backgroundDrawable = model
                    }
                    is CharSequence, is Int -> {
                        backgroundDrawable = AsyncLazyDrawable(
                                c.androidContext,
                                model
                        )
                    }
                }
            }
        }
        if (backgroundDrawable == null) {
            backgroundDrawable = NoOpDrawable()
        }
        this.background(BackgroundDrawable(
                backgroundDrawable,
                borderRadius,
                borderWidth,
                borderColor
        ))
    }

    private fun T.applyEvent(
            c: ComponentContext,
            data: PropsELContext,
            attrs: Map<String, String>,
            visibility: Int
    ) {
        val display = visibility == View.VISIBLE
        val onClick = data.tryGetValue<Any>(attrs["onClick"], "")
        if (onClick is LambdaExpression) {
            clipChildren(false)
            clickHandler(DynamicBox.onClick(
                    c,
                    onClick
            ))
        }
        val onView = data.tryGetValue<Any>(attrs["onView"], Unit)
        if (onView is LambdaExpression && display) {
            visibleHandler(DynamicBox.onView(
                    c,
                    onView
            ))
        }
    }

    protected open fun calculateVisibility(
            c: ComponentContext,
            pager: PropsELContext,
            attrs: Map<String, String>?,
            upperVisibility: Int
    ): Int {
        return if (upperVisibility == View.VISIBLE
                && attrs != null) {
            pager.scope(visibilityValues) {
                pager.tryGetValue(
                        attrs["visibility"],
                        View.VISIBLE
                )
            }
        } else {
            upperVisibility
        }
    }

    private companion object {

        @JvmStatic
        internal val visibilityValues = mapOf(
                "visible" to View.VISIBLE,
                "invisible" to View.INVISIBLE,
                "gone" to View.GONE
        )
    }
}