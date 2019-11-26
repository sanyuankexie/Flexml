package com.guet.flexbox.build

import android.graphics.Color
import android.net.Uri
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.RestrictTo
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.drawable.ComparableColorDrawable
import com.facebook.litho.drawable.ComparableDrawable
import com.facebook.litho.drawable.ComparableGradientDrawable
import com.guet.flexbox.DynamicBox
import com.guet.flexbox.NodeInfo
import com.guet.flexbox.el.LambdaExpression
import com.guet.flexbox.widget.AsyncLazyDrawable
import com.guet.flexbox.widget.BackgroundDrawable
import com.guet.flexbox.widget.NoOpDrawable
import java.util.*

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
internal abstract class WidgetFactory<T : Component.Builder<*>> : Mapper<T>(), Transform {

    override val mappings by CommonMappings.newMappings<T>()

    final override fun transform(
            c: ComponentContext,
            pager: PagerContext,
            nodeInfo: NodeInfo,
            upperVisibility: Int
    ): List<Component>? {
        val component = create(c, pager, nodeInfo, upperVisibility)
        if (component != null) {
            return Collections.singletonList(component)
        }
        return null
    }

    protected abstract fun onCreateWidget(
            c: ComponentContext,
            pager: PagerContext,
            attrs: Map<String, String>?,
            visibility: Int
    ): T

    protected open fun onInstallChildren(
            owner: T,
            c: ComponentContext,
            pager: PagerContext,
            attrs: Map<String, String>?,
            children: List<Component>?,
            visibility: Int) {
    }

    @CallSuper
    protected open fun onLoadStyles(
            owner: T,
            c: ComponentContext,
            pager: PagerContext,
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
            pager: PagerContext,
            nodeInfo: NodeInfo,
            upperVisibility: Int
    ): Component? {
        val attrs = nodeInfo.attrs
        val childrenNodes = nodeInfo.children
        val visibility = calculateVisibility(c, pager, attrs, upperVisibility)
        if (visibility == View.GONE) {
            return null
        }
        val builder = onCreateWidget(c, pager, attrs, visibility)
        onLoadStyles(builder, c, pager, attrs, visibility)
        onInstallChildren(builder, c, pager, attrs, childrenNodes?.map {
            pager.inflate(c, it, visibility)
        }?.flatten(), visibility)
        return builder.build()
    }

    private fun T.applyBackground(
            c: ComponentContext,
            pager: PagerContext,
            attrs: Map<String, String>
    ) {
        val borderRadius = pager.tryGetValue(attrs["borderRadius"], 0).toPx()
        val borderWidth = pager.tryGetValue(attrs["borderWidth"], 0).toPx()
        val borderColor = pager.tryGetColor(attrs["borderColor"], Color.TRANSPARENT)
        var backgroundDrawable: ComparableDrawable? = null
        val background = attrs["background"]
        if (background != null) {
            try {
                backgroundDrawable = ComparableColorDrawable.create(pager.getColor(background))
            } catch (e: Exception) {
                val backgroundELResult = pager.tryGetValue(background, "")
                if (backgroundELResult.startsWith("res://")) {
                    val uri = Uri.parse(backgroundELResult)
                    if (uri.host == "gradient") {
                        val type = uri.getQueryParameter(
                                "orientation"
                        )?.toOrientation()
                        val colors = uri.getQueryParameters("color")?.map {
                            Color.parseColor(it)
                        }?.toIntArray()
                        if (type != null && colors != null && colors.isNotEmpty()) {
                            backgroundDrawable = ComparableGradientDrawable(type, colors)
                        }
                    } else if (uri.host == "load") {
                        val name = uri.getQueryParameter("name")
                        if (name != null) {
                            val id = c.resources.getIdentifier(
                                    name,
                                    "drawable",
                                    c.androidContext.packageName
                            )
                            if (id != 0) {
                                backgroundDrawable = AsyncLazyDrawable(
                                        c.androidContext,
                                        id
                                )
                            }
                        }
                    }
                } else if (backgroundELResult.isNotEmpty()) {
                    backgroundDrawable = AsyncLazyDrawable(
                            c.androidContext,
                            backgroundELResult
                    )
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
            pager: PagerContext,
            attrs: Map<String, String>,
            visibility: Int
    ) {
        val display = visibility == View.VISIBLE
        val onClick = pager.tryGetValue<Any>(attrs["onClick"], "")
        val reportClick = pager.tryGetValue<Any>(attrs["onReportClick"], Unit).let {
            if (it is LambdaExpression) {
                it
            } else {
                null
            }
        }
        if (onClick is LambdaExpression) {
            clipChildren(false)
            clickHandler(DynamicBox.onClick(
                    c,
                    onClick,
                    reportClick
            ))
        }
        val reportView = pager.tryGetValue<Any>(attrs["onReportView"], Unit)
        if (reportView is LambdaExpression && display) {
            visibleHandler(DynamicBox.onView(
                    c,
                    reportView
            ))
        }
    }

    protected open fun calculateVisibility(
            c: ComponentContext,
            pager: PagerContext,
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

    companion object {

        @JvmStatic
        internal val visibilityValues = mapOf(
                "visible" to View.VISIBLE,
                "invisible" to View.INVISIBLE,
                "gone" to View.GONE
        )
    }
}