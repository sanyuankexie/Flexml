package com.guet.flexbox.handshake.configuration.type

import com.guet.flexbox.handshake.configuration.MockRunConfiguration
import com.guet.flexbox.handshake.configuration.options.MockOptions
import com.guet.flexbox.handshake.util.fileIcon
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.SimpleConfigurationType
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NotNullLazyValue

class MockConfigurationType : SimpleConfigurationType(
        "FlexmlMock",
        "Flexml mock",
        "begin run flexml mock task",
        NotNullLazyValue.createValue {
            fileIcon
        }
) {

    override fun createTemplateConfiguration(
            project: Project
    ): RunConfiguration {
        return MockRunConfiguration(project, this)
    }

    override fun getOptionsClass(): Class<out BaseState>? {
        return MockOptions::class.java
    }
}