package com.guet.flexbox.intellij.configuration.producer

import com.guet.flexbox.intellij.configuration.CompileRunConfiguration
import com.guet.flexbox.intellij.configuration.type.CompileConfigurationType
import com.guet.flexbox.intellij.isOnFlexmlFile
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.RunConfigurationProducer
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlFile
import java.io.File

class CompileRunConfigurationProducer
    : RunConfigurationProducer<CompileRunConfiguration>(
        ConfigurationTypeUtil.findConfigurationType(
                CompileConfigurationType::class.java
        ) as ConfigurationType
) {

    override fun isConfigurationFromContext(
            configuration: CompileRunConfiguration,
            context: ConfigurationContext
    ): Boolean {
        val location = context.psiLocation
        if (location?.containingFile is XmlFile
                && context.psiLocation
                        ?.isOnFlexmlFile == true
        ) {
            val file = context.psiLocation
                    ?.containingFile
                    as? XmlFile
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
                && sourceElement.get()
                        ?.isOnFlexmlFile == true
        ) {
            val file = context
                    .psiLocation
                    ?.containingFile
                    as? XmlFile
            if (file != null) {
                configuration.name = "Compile ${file.virtualFile.name}"
                val sourcePath = file.originalFile
                        .virtualFile
                        .path
                configuration.state?.template = sourcePath
                configuration.state?.output = File(
                        File(sourcePath).parent,
                        "output.json"
                ).absolutePath
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