package com.guet.flexbox.handshake.configuration.options

import com.intellij.execution.configurations.LocatableRunConfigurationOptions

class CompileOptions : LocatableRunConfigurationOptions() {
    var template: String? by string()
    var output: String? by string()
}