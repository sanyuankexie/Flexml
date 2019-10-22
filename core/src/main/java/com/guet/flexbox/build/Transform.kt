package com.guet.flexbox.build

import com.facebook.litho.Component
import org.dom4j.Attribute
import org.dom4j.Element

internal interface Transform {
    fun transform(c: BuildContext,
                  element: Element,
                  attrs: List<Attribute>,
                  children: List<Component.Builder<*>>)
            : List<Component.Builder<*>>
}