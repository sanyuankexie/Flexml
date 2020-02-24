package com.guet.flexbox

import com.guet.flexbox.el.Functions
import com.guet.flexbox.eventsystem.EventTarget
import com.guet.flexbox.transaction.HttpTransaction
import com.guet.flexbox.transaction.RefreshTransaction
import com.guet.flexbox.transaction.SendTransaction
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.annotations.NoJexl

class PageContext(
        private val data: JexlContext,
        private val target: EventTarget
) : JexlContext, JexlContext.NamespaceResolver {

    fun send(vararg values: Any) {
        SendTransaction(this, target)
                .send(*values)
                .commit()
    }

    fun http(): HttpTransaction {
        return HttpTransaction(this, target)
    }

    fun refresh(): RefreshTransaction {
        return RefreshTransaction(this, target)
    }

    @NoJexl
    override fun has(name: String?): Boolean {
        return name == "pageContext" || data.has(name)
    }

    @NoJexl
    override fun get(name: String?): Any? {
        return if (name != "pageContext") {
            data.get(name)
        } else {
            this
        }
    }

    @NoJexl
    override fun set(name: String?, value: Any?) {
        if (name != "pageContext") {
            data.set(name, value)
        }
    }

    override fun resolveNamespace(name: String?): Any? {
        val obj = Functions[name]
        if (obj != null) {
            return obj
        }
        if (data is JexlContext.NamespaceResolver) {
            return data.resolveNamespace(name)
        }
        return null
    }
}