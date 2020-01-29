package com.guet.flexbox.handshake.configuration

import com.guet.flexbox.handshake.BinaryLoader
import com.guet.flexbox.handshake.configuration.options.CompileOptions
import com.guet.flexbox.handshake.ui.CompileSettingForm
import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.LocatableConfigurationBase
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.jar.JarApplicationCommandLineState
import com.intellij.execution.jar.JarApplicationConfiguration
import com.intellij.execution.jar.JarApplicationConfigurationType
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project

class CompileRunConfiguration(
        project: Project,
        factory: ConfigurationFactory
) : LocatableConfigurationBase<CompileOptions>(project, factory, "Compile this template") {
    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> = CompileSettingForm()
    override fun getState(
            executor: Executor,
            environment: ExecutionEnvironment
    ): RunProfileState? {
        val input = state!!.template!!
        val output = state!!.output!!
        return JarApplicationCommandLineState(
                JarApplicationConfiguration(
                        project,
                        JarApplicationConfigurationType.getInstance(),
                        "Compile this template"
                ).apply {
                    jarPath = BinaryLoader.compilerJarPath
                    programParameters = "input=${input} output=${output}"
                },
                environment
        )
    }
}