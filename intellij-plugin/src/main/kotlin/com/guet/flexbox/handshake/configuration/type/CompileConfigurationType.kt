package com.guet.flexbox.handshake.configuration.type

import com.guet.flexbox.handshake.configuration.CompileRunConfiguration
import com.guet.flexbox.handshake.configuration.options.CompileOptions
import com.guet.flexbox.handshake.util.fileIcon
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.SimpleConfigurationType
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NotNullLazyValue

class CompileConfigurationType : SimpleConfigurationType(
    "FlexmlCompile",
    "Flexml compile",
    "begin run flexml compile task",
    NotNullLazyValue.createValue {
        fileIcon
    }
) {
    override fun createTemplateConfiguration(
            project: Project
    ): RunConfiguration {
        return CompileRunConfiguration(project, this)
    }

    override fun getOptionsClass(): Class<out BaseState>? {
        return CompileOptions::class.java
    }
}