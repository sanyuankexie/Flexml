package com.guet.flexbox.handshake.mock

import com.intellij.openapi.options.SettingsEditor
import javax.swing.JComponent

class FlexmlMockSettingsEditor : SettingsEditor<FlexmlMockRunConfiguration>() {

    private val form = FlexmlMockSettingForm()

    override fun resetEditorFrom(s: FlexmlMockRunConfiguration) {
        form.port.text = s.state?.port.toString()
        form.dataSource.text = s.state?.dataSource
        form.template.text = s.state?.template
    }

    override fun createEditor(): JComponent {
        return form.wrapPanel
    }

    override fun applyEditorTo(s: FlexmlMockRunConfiguration) {
        s.state?.port = form.port.text.toInt()
        s.state?.dataSource = form.dataSource.text
        s.state?.template = form.template.text
    }
}