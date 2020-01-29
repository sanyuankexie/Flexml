package com.guet.flexbox.handshake.configuration

import com.guet.flexbox.handshake.BinaryLoader
import com.guet.flexbox.handshake.configuration.options.MockOptions
import com.guet.flexbox.handshake.ui.MockSettingForm
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

class MockRunConfiguration(project: Project, factory: ConfigurationFactory) :
        LocatableConfigurationBase<MockOptions>(project, factory, "Mock this package") {

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> = MockSettingForm()

    override fun getState(
            executor: Executor,
            environment: ExecutionEnvironment
    ): RunProfileState? {
        val port = state!!.port
        val focus = state!!.packageJson!!
        return JarApplicationCommandLineState(
                JarApplicationConfiguration(
                        project,
                        JarApplicationConfigurationType.getInstance(),
                        "Mock this package"
                ).apply {
                    jarPath = BinaryLoader.mockJarPath
                    programParameters = "server.port=${port} package.focus=${focus}"
                },
                environment
        )
    }

}