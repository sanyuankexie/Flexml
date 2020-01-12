package com.guet.flexbox.build

import android.content.Context
import android.net.Uri
import android.util.ArrayMap
import android.util.AttributeSet
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
        return data.tryGetValue(attrs[name]) ?: ""
    }

    override fun getAttributeIntValue(
            namespace: String?,
            attribute: String?,
            defaultValue: Int
    ): Int {
        return data.tryGetValue(attrs[attribute], defaultValue) ?: 0
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
                attrs[attribute],
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
        return getResourcesId(attrs[attribute], "array")
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
        return data.tryGetValue(attrs[attribute], defaultValue) ?: false
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
        return getResourcesId(attrs[attribute], "drawable")
    }

    override fun getAttributeResourceValue(
            index: Int,
            defaultValue: Int
    ): Int {
        return getResourcesId(attrs.valueAt(index), "drawable")
    }
}