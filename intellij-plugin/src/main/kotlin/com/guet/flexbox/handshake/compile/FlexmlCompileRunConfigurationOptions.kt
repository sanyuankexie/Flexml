package com.guet.flexbox.handshake.compile

import com.intellij.execution.configurations.LocatableRunConfigurationOptions

class FlexmlCompileRunConfigurationOptions : LocatableRunConfigurationOptions() {
    var template: String? by string()
    var output: String? by string()
}