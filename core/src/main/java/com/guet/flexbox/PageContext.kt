package com.guet.flexbox

import android.view.View
import com.guet.flexbox.transaction.HttpTransaction
import com.guet.flexbox.transaction.RefreshTransaction

class PageContext(
        private val view: View?,
        private val event: EventContext
) {

    fun send(vararg values: Any?) {
        event.dispatchEvent(
                EventContext.ActionKey.SendObjects,
                mutableListOf<Any?>(view).apply {
                    addAll(values)
                }
        )
    }

    fun http(): HttpTransaction? {
        return event.dispatchEvent(
                EventContext.ActionKey.HttpRequest,
                listOf(view)
        ) as? HttpTransaction
    }

    fun refresh(): RefreshTransaction? {
        return event.dispatchEvent(
                EventContext.ActionKey.RefreshPage,
                listOf(view)
        ) as? RefreshTransaction
    }
}
