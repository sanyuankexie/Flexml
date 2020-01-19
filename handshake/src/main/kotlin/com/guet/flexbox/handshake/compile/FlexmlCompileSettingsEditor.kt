package com.guet.flexbox.handshake.compile

import com.intellij.openapi.options.SettingsEditor
import javax.swing.JComponent

class FlexmlCompileSettingsEditor : SettingsEditor<FlexmlCompileRunConfiguration>() {

    private val form = FlexmlCompileSettingForm()

    override fun resetEditorFrom(s: FlexmlCompileRunConfiguration) {
        form.template.text = s.state?.template
        form.output.text = s.state?.output
    }

    override fun createEditor(): JComponent {
        return form.wrapPanel
    }

    override fun applyEditorTo(s: FlexmlCompileRunConfiguration) {
        s.state?.template = form.template.text
        s.state?.output = form.output.text
    }
}