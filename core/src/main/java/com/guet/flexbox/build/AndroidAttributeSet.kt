package com.guet.flexbox.build

import android.content.Context
import android.net.Uri
import android.util.ArrayMap
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams
import com.guet.flexbox.el.PropsELContext

internal class AndroidAttributeSet(
        private val c: Context,
        attrs: Map<String, String>,
        private val data: PropsELContext
) : AttributeSet {

    private val attrs: ArrayMap<String, String> = if (
            attrs is ArrayMap<String, String>
    ) {
        attrs
    } else {
        ArrayMap(attrs)
    }

    override fun getPositionDescription(): String = ""

    override fun getAttributeCount(): Int = attrs.size

    override fun getAttributeNameResource(index: Int): Int = 0

    override fun getAttributeUnsignedIntValue(
            namespace: String?,
            attribute: String?,
            defaultValue: Int
    ): Int {
        return getAttributeIntValue(
                namespace,
                attribute,
                defaultValue
        )
    }

    override fun getAttributeUnsignedIntValue(
            index: Int,
            defaultValue: Int
    ): Int {
        return getAttributeIntValue(
                index,
                defaultValue
        )
    }

    override fun getAttributeValue(index: Int): String {
        return data.tryGetValue(attrs.valueAt(index)) ?: ""
    }

    override fun getAttributeValue(
            namespace: String?,
            name: String?
    ): String {
        return data.tryGetValue(attrs.check(name)) ?: ""
    }

    override fun getAttributeIntValue(
            namespace: String?,
            attribute: String?,
            defaultValue: Int
    ): Int {
        return data.tryGetValue(attrs.check(attribute), defaultValue) ?: 0
    }

    override fun getAttributeIntValue(
            index: Int,
            defaultValue: Int
    ): Int {
        return data.tryGetValue(attrs.valueAt(index), defaultValue) ?: 0
    }

    override fun getIdAttribute(): String = ""

    override fun getIdAttributeResourceValue(defaultValue: Int): Int = 0

    override fun getAttributeFloatValue(
            namespace: String?,
            attribute: String?,
            defaultValue: Float
    ): Float {
        return data.tryGetValue(
                attrs.check(attribute),
                defaultValue
        ) ?: 0f
    }

    override fun getAttributeFloatValue(
            index: Int,
            defaultValue: Float
    ): Float {
        return data.tryGetValue(
                attrs.valueAt(index),
                defaultValue
        ) ?: 0f
    }

    override fun getStyleAttribute(): Int = 0

    override fun getAttributeName(index: Int): String {
        return attrs.valueAt(index)
    }

    override fun getAttributeListValue(
            namespace: String?,
            attribute: String?,
            options: Array<out String>?,
            defaultValue: Int
    ): Int {
        return getResourcesId(attrs.check(attribute), "array")
    }

    override fun getAttributeListValue(
            index: Int,
            options: Array<out String>?,
            defaultValue: Int
    ): Int {
        return getResourcesId(attrs.valueAt(index), "array")
    }

    override fun getClassAttribute(): String = ""

    override fun getAttributeBooleanValue(
            namespace: String?,
            attribute: String?,
            defaultValue: Boolean
    ): Boolean {
        return data.tryGetValue(attrs.check(attribute), defaultValue) ?: false
    }

    override fun getAttributeBooleanValue(
            index: Int,
            defaultValue: Boolean
    ): Boolean {
        return data.tryGetValue(attrs.valueAt(index), defaultValue) ?: false
    }

    private fun getResourcesId(raw: String?, type: String): Int {
        val text = data.tryGetValue<String>(raw)
        if (!text.isNullOrEmpty()) {
            val uri = Uri.parse(text)
            if (uri.host == type) {
                val name = uri.getQueryParameter("name")
                if (name != null) {
                    return c.resources.getIdentifier(
                            name,
                            type,
                            c.packageName
                    )
                }
            }
        }
        return 0
    }

    override fun getAttributeResourceValue(
            namespace: String?,
            attribute: String?,
            defaultValue: Int
    ): Int {
        return getResourcesId(attrs.check(attribute), "drawable")
    }

    override fun getAttributeResourceValue(
            index: Int,
            defaultValue: Int
    ): Int {
        return getResourcesId(attrs.valueAt(index), "drawable")
    }

    companion object {

        private fun ArrayMap<String, String>.check(key: String?): String? {
            key ?: return null
            val value = this[key]
            if (value == null) {
                val newKeyWithDefault = androidRedirect[key]
                if (newKeyWithDefault != null) {
                    val (newKey, default) = newKeyWithDefault
                    return this[newKey] ?: default
                }
            }
            return null
        }

        private val androidRedirect: Map<String, Pair<String, String?>> = mapOf(
                "layout_height" to ("height" to LayoutParams.MATCH_PARENT.toString()),
                "layout_width" to ("width" to LayoutParams.MATCH_PARENT.toString()),
                "layout_marginTop" to ("marginTop" to null),
                "layout_marginLeft" to ("marginLeft" to null),
                "layout_marginRight" to ("marginRight" to null),
                "layout_marginBottom" to ("marginBottom" to null)
        )
    }
}