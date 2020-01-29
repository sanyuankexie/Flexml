package com.guet.flexbox.intellij.ui

import com.guet.flexbox.intellij.configuration.MockRunConfiguration
import javax.swing.JComponent

class MockSettingForm : MockSettingFormBase() {

    override fun resetEditorFrom(s: MockRunConfiguration) {
        this.port.text = s.state?.port.toString()
        this.packageJson.text = s.state?.packageJson
    }

    override fun createEditor(): JComponent {
        return this.wrapPanel
    }

    override fun applyEditorTo(s: MockRunConfiguration) {
        s.state?.port = this.port.text.toInt()
        s.state?.packageJson = this.packageJson.text
    }
}