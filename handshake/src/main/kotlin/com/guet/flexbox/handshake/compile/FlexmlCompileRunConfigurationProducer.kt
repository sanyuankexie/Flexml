package com.guet.flexbox.handshake.compile

import com.guet.flexbox.handshake.util.isOnFlexmlFile
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.RunConfigurationProducer
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlFile

class FlexmlCompileRunConfigurationProducer : RunConfigurationProducer<FlexmlCompileRunConfiguration>(
    ConfigurationTypeUtil.findConfigurationType(
        FlexmlCompileConfigurationType::class.java
    )
) {
    override fun isConfigurationFromContext(
        configuration: FlexmlCompileRunConfiguration,
        context: ConfigurationContext
    ): Boolean {
        val location = context.psiLocation
        if (location?.containingFile is XmlFile
            && context.psiLocation?.isOnFlexmlFile == true
        ) {
            val file = context.psiLocation?.containingFile as? XmlFile
            if (file != null) {
                return configuration.state?.template == file.originalFile.virtualFile.path
            }
        }
        return false
    }

    override fun setupConfigurationFromContext(
        configuration: FlexmlCompileRunConfiguration,
        context: ConfigurationContext,
        sourceElement: Ref<PsiElement>
    ): Boolean {
        val location = context.psiLocation
        if (location?.containingFile is XmlFile
            && sourceElement.get()?.isOnFlexmlFile == true
        ) {
            val file = context.psiLocation?.containingFile as? XmlFile
            if (file != null) {
                configuration.name = "Compile ${file.virtualFile.name}"
                configuration.state?.template = file.originalFile.virtualFile.path
                return true
            }
        }
        return false
    }
}