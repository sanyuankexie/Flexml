package com.guet.flexbox.intellij.configuration.type

import com.guet.flexbox.intellij.res.Icons
import com.guet.flexbox.intellij.configuration.MockRunConfiguration
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.SimpleConfigurationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NotNullLazyValue

class MockConfigurationType : SimpleConfigurationType(
        "FlexmlMock",
        "Flexml mock",
        "begin run flexml mock task",
        NotNullLazyValue.createValue {
            Icons.typeIcon
        }
) {

    override fun createTemplateConfiguration(
            project: Project
    ): RunConfiguration {
        return MockRunConfiguration(project, this)
    }


}