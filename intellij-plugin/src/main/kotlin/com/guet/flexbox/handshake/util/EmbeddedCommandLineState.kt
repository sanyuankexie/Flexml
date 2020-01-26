package com.guet.flexbox.handshake.util

import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment

class EmbeddedCommandLineState(
    environment: ExecutionEnvironment,
    private val action: () -> EmbeddedHandler
) : CommandLineState(environment) {

    override fun startProcess(): ProcessHandler = action()
}
