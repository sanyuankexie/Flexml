package com.guet.flexbox.build

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import com.guet.flexbox.el.PropsELContext

class AndroidAttributeSet private constructor(
        private val c: Context,
        private val attrs: Map<String, String>,
        private val data: PropsELContext
) : AttributeSet {

    private val indices = attrs.keys.toTypedArray()

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
        return getAttributeValue("", indices[index])
    }

    override fun getAttributeValue(
            namespace: String?,
            name: String?
    ): String {
        return data.tryGetValue(attrs[name])!!
    }

    override fun getAttributeIntValue(
            namespace: String?,
            attribute: String?,
            defaultValue: Int
    ): Int {
        return data.tryGetValue(attrs[attribute], defaultValue)!!
    }

    override fun getAttributeIntValue(
            index: Int,
            defaultValue: Int
    ): Int {
        return getAttributeIntValue(
                "",
                indices[index],
                defaultValue
        )
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
        )!!
    }

    override fun getAttributeFloatValue(
            index: Int,
            defaultValue: Float
    ): Float {
        return getAttributeFloatValue(
                "",
                indices[index],
                defaultValue
        )
    }

    override fun getStyleAttribute(): Int = 0

    override fun getAttributeName(index: Int): String {
        return indices[index]
    }

    override fun getAttributeListValue(
            namespace: String?,
            attribute: String?,
            options: Array<out String>?,
            defaultValue: Int
    ): Int {
        return getResourcesId(attribute, "array")
    }

    override fun getAttributeListValue(
            index: Int,
            options: Array<out String>?,
            defaultValue: Int
    ): Int {
        return getAttributeListValue(
                "",
                indices[index],
                options,
                defaultValue
        )
    }

    override fun getClassAttribute(): String = ""

    override fun getAttributeBooleanValue(
            namespace: String?,
            attribute: String?,
            defaultValue: Boolean
    ): Boolean {
        return data.tryGetValue(attrs[attribute], defaultValue)!!
    }

    override fun getAttributeBooleanValue(
            index: Int,
            defaultValue: Boolean
    ): Boolean {
        return getAttributeBooleanValue("", indices[index], defaultValue)
    }

    private fun getResourcesId(attribute: String?, type: String): Int {
        val text = data.tryGetValue<String>(attrs[attribute])
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
        return getResourcesId(attribute, "drawable")
    }

    override fun getAttributeResourceValue(
            index: Int,
            defaultValue: Int
    ): Int {
        return getAttributeResourceValue("", indices[index], defaultValue)
    }

    companion object {

        internal val OTHER_ATTRS_KEY: String = ViewCompat::class.java.name

        @Suppress("UNCHECKED_CAST")
        fun from(
                c: Context,
                map: Map<String, Any>
        ): AndroidAttributeSet {
            val (attrs, data) = map
                    .getValue(OTHER_ATTRS_KEY)
                    as Pair<HashMap<String, String>, PropsELContext>
            return AndroidAttributeSet(c, attrs, data)
        }
    }
}