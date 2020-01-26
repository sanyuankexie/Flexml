package com.guet.flexbox.handshake.compile

import com.guet.flexbox.handshake.util.EmbeddedCommandLineState
import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project

class FlexmlCompileRunConfiguration(project: Project, factory: ConfigurationFactory) :
    LocatableConfigurationBase<FlexmlCompileRunConfigurationOptions>(project, factory, "Compile this template") {

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        return FlexmlCompileSettingsEditor()
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState? {
        return EmbeddedCommandLineState(environment) { CompilerHandler(this) }
    }

    override fun getOptionsClass(): Class<out RunConfigurationOptions> {
        return FlexmlCompileRunConfigurationOptions::class.java
    }
}