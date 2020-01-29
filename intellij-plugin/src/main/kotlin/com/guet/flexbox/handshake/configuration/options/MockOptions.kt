package com.guet.flexbox.handshake.configuration.options

import com.intellij.execution.configurations.LocatableRunConfigurationOptions

class MockOptions : LocatableRunConfigurationOptions() {

    var port: Int by property(defaultValue = 8080)

    var packageJson: String? by string()
}