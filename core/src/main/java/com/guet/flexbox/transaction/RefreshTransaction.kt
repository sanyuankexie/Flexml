package com.guet.flexbox.transaction

import com.guet.flexbox.PageContext
import com.guet.flexbox.event.ActionKey
import org.apache.commons.jexl3.JexlScript
import org.apache.commons.jexl3.annotations.NoJexl
import java.util.*

class RefreshTransaction(
        context: PageContext
) : SendTransaction(context) {

    private lateinit var paddingModify: LinkedList<JexlScript>

    fun with(l: JexlScript): RefreshTransaction {
        if (!this::paddingModify.isInitialized) {
            paddingModify = LinkedList()
        }
        paddingModify.add(l)
        return this
    }

    @NoJexl
    override fun execute(executor: ActionExecutor) {
        super.execute(executor)
        if (this::paddingModify.isInitialized) {
            executor.execute(
                    ActionKey.ExecuteActions,
                    paddingModify.toTypedArray()
            )
            executor.execute(
                    ActionKey.RefreshPage
            )
        }
    }
}