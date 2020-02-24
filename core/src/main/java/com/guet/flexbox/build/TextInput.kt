package com.guet.flexbox.build

import android.view.View
import android.widget.EditText
import com.guet.flexbox.eventsystem.EventFactory
import com.guet.flexbox.eventsystem.event.TextChangedEvent
import com.guet.flexbox.eventsystem.event.TemplateEvent
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlScript


object TextInput : Declaration() {
    override val dataBinding by DataBinding
            .create(CommonProps.dataBinding) {
                event("onTextChanged", object : EventFactory {
                    override fun create(
                            source: View?,
                            args: Array<out Any?>?,
                            dataContext: JexlContext,
                            script: JexlScript
                    ): TemplateEvent<*, *> {
                        return TextChangedEvent(
                                source as EditText,
                                args?.get(0) as? String,
                                dataContext,
                                script
                        )
                    }
                })
            }
}