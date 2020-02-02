package com.guet.flexbox.intellij.configuration.producer

import com.guet.flexbox.intellij.configuration.MockRunConfiguration
import com.guet.flexbox.intellij.configuration.type.MockConfigurationType
import com.guet.flexbox.intellij.isOnFlexmlFile
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.RunConfigurationProducer
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.json.psi.JsonFile
import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement

class MockRunConfigurationProducer
    : RunConfigurationProducer<MockRunConfiguration>(
        ConfigurationTypeUtil.findConfigurationType(
                MockConfigurationType::class.java
        ) as ConfigurationType
) {

    override fun isConfigurationFromContext(
            configuration: MockRunConfiguration,
            context: ConfigurationContext
    ): Boolean {
        val file = context
                .psiLocation
                ?.containingFile
                as? JsonFile
                ?: return false
        if (file.name != "package.json") {
            return false
        }
        val obj = file
                .topLevelValue
                ?.let { it as? JsonObject }
        val template = obj
                ?.findProperty("template")
                ?: return false
        val xmlName = template
                .value
                ?.let { it as? JsonStringLiteral }
                ?.value
        if (xmlName != null && file.parent?.findFile(xmlName)
                        ?.isOnFlexmlFile == true) {
            return configuration.state?.packageJson == file.virtualFile.path
        }
        return false
    }


    override fun setupConfigurationFromContext(
            configuration: MockRunConfiguration,
            context: ConfigurationContext,
            sourceElement: Ref<PsiElement>
    ): Boolean {
        val file = context
                .psiLocation
                ?.containingFile
                as? JsonFile
                ?: return false
        if (file.name != "package.json") {
            return false
        }
        val obj = file
                .topLevelValue
                ?.let { it as? JsonObject }
        val template = obj
                ?.findProperty("template")
                ?: return false
        val xmlName = template
                .value
                ?.let { it as? JsonStringLiteral }
                ?.value
        if (xmlName != null && file.parent?.findFile(xmlName)
                        ?.isOnFlexmlFile == true) {
            configuration.name = "Mock package ${context.psiLocation
                    ?.containingFile
                    ?.parent
                    ?.name}"
            sourceElement.set(template)
            configuration.state?.packageJson = file
                    .virtualFile
                    .path
            return true
        }
        return false
    }

    override fun getConfigurationFactory(): ConfigurationFactory {
        return ConfigurationTypeUtil.findConfigurationType(
                MockConfigurationType::class.java
        )
    }
}