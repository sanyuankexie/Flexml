package com.guet.flexbox.intellij.service

import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project

interface JarStartupManager {

    fun runCompiler(
            environment: ExecutionEnvironment,
            input: String,
            output: String
    ): RunProfileState

    fun runMockServer(
            environment: ExecutionEnvironment,
            focus: String,
            port: Int
    ): RunProfileState

    companion object {
        fun getInstance(project: Project): JarStartupManager {
            return ServiceManager.getService(project, JarStartupManager::class.java)
        }
    }
}