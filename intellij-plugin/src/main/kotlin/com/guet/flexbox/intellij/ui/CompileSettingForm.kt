package com.guet.flexbox.intellij.ui

import com.guet.flexbox.intellij.configuration.CompileRunConfiguration
import javax.swing.JComponent

class CompileSettingForm : CompileSettingFormBase() {
    override fun resetEditorFrom(s: CompileRunConfiguration) {
        template.text = s.state?.template
        output.text = s.state?.output
    }

    override fun createEditor(): JComponent {
        return wrapPanel
    }

    override fun applyEditorTo(s: CompileRunConfiguration) {
        s.state?.template = template.text
        s.state?.output = output.text
    }
}