package com.guet.flexbox.handshake.configuration

import com.guet.flexbox.handshake.configuration.options.CompileOptions
import com.guet.flexbox.handshake.ui.CompileSettingForm
import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.LocatableConfigurationBase
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project

class CompileRunConfiguration(project: Project, factory: ConfigurationFactory) :
        LocatableConfigurationBase<CompileOptions>(project, factory, "Compile this template") {
    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> = CompileSettingForm()
    override fun getState(
            executor: Executor,
            environment: ExecutionEnvironment
    ): RunProfileState? {
        TODO()
    }
}