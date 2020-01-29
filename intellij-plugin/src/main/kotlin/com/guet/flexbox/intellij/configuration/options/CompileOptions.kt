package com.guet.flexbox.intellij.configuration.options

import com.intellij.execution.configurations.LocatableRunConfigurationOptions

class CompileOptions : LocatableRunConfigurationOptions() {
    var template: String? by string()
    var output: String? by string()
}