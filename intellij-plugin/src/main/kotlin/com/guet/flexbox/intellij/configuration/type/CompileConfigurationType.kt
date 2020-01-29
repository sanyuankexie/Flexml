package com.guet.flexbox.intellij.configuration.type

import com.guet.flexbox.intellij.bundle.Icons
import com.guet.flexbox.intellij.configuration.CompileRunConfiguration
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.SimpleConfigurationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NotNullLazyValue

class CompileConfigurationType : SimpleConfigurationType(
    "FlexmlCompile",
    "Flexml compile",
    "begin run flexml compile task",
    NotNullLazyValue.createValue {
        Icons.fileIcon
    }
) {
    override fun createTemplateConfiguration(
            project: Project
    ): RunConfiguration {
        return CompileRunConfiguration(project, this)
    }
}