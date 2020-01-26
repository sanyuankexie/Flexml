package com.guet.flexbox.handshake.compile

import com.guet.flexbox.handshake.util.fileIcon
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project

class FlexmlCompileConfigurationType : ConfigurationTypeBase(
    "FlexmlCompile",
    "Flexml compile",
    "begin run flexml compile task",
    fileIcon
) {
    init {
        addFactory(object : ConfigurationFactory(this) {
            override fun createTemplateConfiguration(project: Project): RunConfiguration {
                return FlexmlCompileRunConfiguration(project, this)
            }
        })
    }

}