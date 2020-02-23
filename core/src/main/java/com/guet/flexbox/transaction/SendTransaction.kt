package com.guet.flexbox.transaction

import androidx.annotation.CallSuper
import com.guet.flexbox.PageContext
import com.guet.flexbox.event.ActionKey
import org.apache.commons.jexl3.annotations.NoJexl
import java.util.*

open class SendTransaction(
        context: PageContext
) : PageTransaction(context) {

    private lateinit var pendingEvents: LinkedList<Array<out Any?>>

    fun send(vararg values: Any?): PageTransaction {
        if (!this::pendingEvents.isInitialized) {
            pendingEvents = LinkedList()
        }
        pendingEvents.add(values)
        return this
    }

    @NoJexl
    @CallSuper
    override fun execute(executor: ActionExecutor) {
        super.execute(executor)
        if (this::pendingEvents.isInitialized) {
            pendingEvents.forEach {
                executor.execute(
                        ActionKey.SendObjects,
                        it
                )
            }
        }
    }

}