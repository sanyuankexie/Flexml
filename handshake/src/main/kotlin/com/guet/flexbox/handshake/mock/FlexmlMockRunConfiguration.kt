package com.guet.flexbox.handshake.mock

import com.guet.flexbox.handshake.util.EmbeddedCommandLineState
import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project

class FlexmlMockRunConfiguration(project: Project, factory: ConfigurationFactory) :
    LocatableConfigurationBase<FlexmlMockRunConfigurationOptions>(project, factory, "Mock this package") {

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> = FlexmlMockSettingsEditor()

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState? {
        return EmbeddedCommandLineState(environment) { MockServerHandler(this) }
    }

    override fun getDefaultOptionsClass(): Class<out LocatableRunConfigurationOptions> {
        return FlexmlMockRunConfigurationOptions::class.java
    }
}