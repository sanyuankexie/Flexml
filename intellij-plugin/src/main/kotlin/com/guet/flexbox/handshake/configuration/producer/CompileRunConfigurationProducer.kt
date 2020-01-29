package com.guet.flexbox.handshake.configuration.producer

import com.guet.flexbox.handshake.configuration.CompileRunConfiguration
import com.guet.flexbox.handshake.configuration.type.CompileConfigurationType
import com.guet.flexbox.handshake.isOnFlexmlFile
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.LazyRunConfigurationProducer
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlFile
import com.intellij.util.lang.UrlClassLoader

class CompileRunConfigurationProducer :
        LazyRunConfigurationProducer<CompileRunConfiguration>() {

    override fun isConfigurationFromContext(
            configuration: CompileRunConfiguration,
            context: ConfigurationContext
    ): Boolean {
        UrlClassLoader(javaClass.classLoader).urls
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
            configuration: CompileRunConfiguration,
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

    override fun getConfigurationFactory(): ConfigurationFactory {
        return ConfigurationTypeUtil.findConfigurationType(
                CompileConfigurationType::class.java
        )
    }
}