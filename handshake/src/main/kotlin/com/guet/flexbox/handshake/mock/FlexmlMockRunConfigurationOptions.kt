package com.guet.flexbox.handshake.mock

import com.intellij.execution.configurations.LocatableRunConfigurationOptions

class FlexmlMockRunConfigurationOptions : LocatableRunConfigurationOptions() {

    var port: Int by property(defaultValue = 8080)

    var dataSource: String? by string()

    var template: String? by string()
}