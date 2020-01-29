package com.guet.flexbox.handshake.ui

import com.guet.flexbox.handshake.configuration.MockRunConfiguration
import javax.swing.JComponent

class MockSettingForm : MockSettingFormBase() {

    override fun resetEditorFrom(s: MockRunConfiguration) {
        this.port.text = s.state?.port.toString()
        this.dataSource.text = s.state?.dataSource
        this.template.text = s.state?.template
    }

    override fun createEditor(): JComponent {
        return this.wrapPanel
    }

    override fun applyEditorTo(s: MockRunConfiguration) {
        s.state?.port = this.port.text.toInt()
        s.state?.dataSource = this.dataSource.text
        s.state?.template = this.template.text
    }
}