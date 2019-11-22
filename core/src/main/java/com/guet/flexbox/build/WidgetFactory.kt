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
import com.guet.flexbox.widget.AsyncLazyDrawable
import com.guet.flexbox.widget.BackgroundDrawable
import com.guet.flexbox.widget.NoOpDrawable
import java.util.*

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
internal abstract class WidgetFactory<T : Component.Builder<*>> : Mapper<T>(), Transform {

    override val mappings by CommonMappings.newMappings<T>()

    final override fun transform(
            c: ComponentContext,
            buildContext: BuildContext,
            nodeInfo: NodeInfo,
            upperVisibility: Int
    ): List<Component> {
        val value = create(c, buildContext, nodeInfo, upperVisibility)
        return if (value != null) {
            Collections.singletonList(value)
        } else {
            emptyList()
        }
    }

    protected abstract fun onCreateWidget(
            c: ComponentContext,
            buildContext: BuildContext,
            attrs: Map<String, String>?,
            visibility: Int
    ): T

    protected open fun onInstallChildren(
            owner: T,
            c: ComponentContext,
            dataBinding: BuildContext,
            attrs: Map<String, String>?,
            children: List<Component>?,
            visibility: Int) {
    }

    @CallSuper
    protected open fun onLoadStyles(
            owner: T,
            c: ComponentContext,
            buildContext: BuildContext,
            attrs: Map<String, String>?,
            visibility: Int
    ) {
        if (!attrs.isNullOrEmpty()) {
            owner.applyEvent(c, buildContext, attrs, visibility)
            if (visibility == View.VISIBLE) {
                owner.applyBackground(c, buildContext, attrs)
            }
        }
        val display = visibility == View.VISIBLE
        if (!attrs.isNullOrEmpty()) {
            for ((key, value) in attrs) {
                mappings[key]?.invoke(owner, buildContext, attrs, display, value)
            }
        }
    }

    private fun create(
            c: ComponentContext,
            dataBinding: BuildContext,
            nodeInfo: NodeInfo,
            upperVisibility: Int
    ): Component? {
        val attrs = nodeInfo.attrs
        val childrenNodes = nodeInfo.children
        val visibility = calculateVisibility(dataBinding, attrs, upperVisibility)
        if (visibility == View.GONE) {
            return null
        }
        val builder = onCreateWidget(c, dataBinding, attrs, visibility)
        onLoadStyles(builder, c, dataBinding, attrs, visibility)
        onInstallChildren(builder, c, dataBinding, attrs, childrenNodes?.map {
            c.createFromElement(dataBinding, it, visibility)
        }?.flatten(), visibility)
        return builder.build()
    }

    private fun T.applyBackground(
            c: ComponentContext,
            buildContext: BuildContext,
            attrs: Map<String, String>
    ) {
        val borderRadius = buildContext.tryGetValue(attrs["borderRadius"], 0).toPx()
        val borderWidth = buildContext.tryGetValue(attrs["borderWidth"], 0).toPx()
        val borderColor = buildContext.tryGetColor(attrs["borderColor"], Color.TRANSPARENT)
        var backgroundDrawable: ComparableDrawable? = null
        val background = attrs["background"]
        if (background != null) {
            try {
                backgroundDrawable = ComparableColorDrawable.create(buildContext.getColor(background))
            } catch (e: Exception) {
                val backgroundELResult = buildContext.tryGetValue(background, "")
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
            dataBinding: BuildContext,
            attrs: Map<String, String>,
            visibility: Int
    ) {
        val display = visibility == View.VISIBLE
        val clickUrl = dataBinding.tryGetValue(attrs["clickUrl"], "")
        val reportClick = dataBinding.tryGetValue(attrs["reportClick"], "")
        if (clickUrl.isNotEmpty()) {
            clipChildren(false)
            clickHandler(DynamicBox.onClick(
                    c,
                    clickUrl,
                    reportClick
            ))
        }
        val reportView = dataBinding.tryGetValue(attrs["reportView"], "")
        if (reportView.isNotEmpty() && display) {
            visibleHandler(DynamicBox.onView(
                    c,
                    reportView
            ))
        }
    }

    protected open fun calculateVisibility(
            dataBinding: BuildContext,
            attrs: Map<String, String>?,
            upperVisibility: Int
    ): Int {
        return if (upperVisibility == View.VISIBLE
                && attrs != null) {
            dataBinding.scope(visibilityValues) {
                dataBinding.tryGetValue(
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
        fun createLayout(
                c: ComponentContext,
                data: Any?,
                root: NodeInfo
        ): Component? {
            return c.createFromElement(BuildContext(data), root).singleOrNull()
        }

        internal val visibilityValues = mapOf(
                "visible" to View.VISIBLE,
                "invisible" to View.INVISIBLE,
                "gone" to View.GONE
        )
    }
}