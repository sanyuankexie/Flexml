package com.guet.flexbox.build

import com.guet.flexbox.PageContext
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlEngine


internal interface TextToAttribute<T : Any>{
    fun cast(
            engine: JexlEngine,
            dataContext: JexlContext,
            pageContext: PageContext,
            raw: String
    ): T?
}
