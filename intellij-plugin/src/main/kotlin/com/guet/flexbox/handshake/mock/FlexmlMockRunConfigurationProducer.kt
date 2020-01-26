package com.guet.flexbox.handshake.mock

import com.guet.flexbox.handshake.util.isOnFlexmlFile
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.RunConfigurationProducer
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.json.JsonUtil
import com.intellij.json.psi.JsonFile
import com.intellij.json.psi.JsonNumberLiteral
import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement

class FlexmlMockRunConfigurationProducer : RunConfigurationProducer<FlexmlMockRunConfiguration>(
    ConfigurationTypeUtil.findConfigurationType(
        FlexmlMockConfigurationType::class.java
    )
) {
    override fun isConfigurationFromContext(
        configuration: FlexmlMockRunConfiguration,
        context: ConfigurationContext
    ): Boolean {
        val file = context.psiLocation?.containingFile as? JsonFile ?: return false
        if (file.name != "package.json") {
            return false
        }
        val obj = file.topLevelValue?.let { it as? JsonObject }
        val template = obj?.findProperty("template") ?: return false
        val xmlName = template.value?.let { it as? JsonStringLiteral }?.value
        if (xmlName != null && file.parent?.findFile(xmlName)?.isOnFlexmlFile == true) {
            return (configuration.state?.port ?: 8080 == getPort(obj))
                    || (configuration.state?.template == getFilePath(obj, "template"))
                    || (configuration.state?.dataSource == getFilePath(obj, "data"))
        }
        return false
    }


    override fun setupConfigurationFromContext(
        configuration: FlexmlMockRunConfiguration,
        context: ConfigurationContext,
        sourceElement: Ref<PsiElement>
    ): Boolean {
        val file = context.psiLocation?.containingFile as? JsonFile ?: return false
        if (file.name != "package.json") {
            return false
        }
        val obj = file.topLevelValue?.let { it as? JsonObject }
        val template = obj?.findProperty("template") ?: return false
        val xmlName = template.value?.let { it as? JsonStringLiteral }?.value
        if (xmlName != null && file.parent?.findFile(xmlName)?.isOnFlexmlFile == true) {
            configuration.name = "Mock package ${context.psiLocation?.containingFile?.parent?.name}"
            sourceElement.set(template)
            configuration.state?.port = getPort(obj)
            configuration.state?.template = getFilePath(obj, "template")
            configuration.state?.dataSource = getFilePath(obj, "data")
            return true
        }
        return false
    }

    companion object {

        private fun getPort(obj: JsonObject): Int {
            return JsonUtil.getPropertyValueOfType(
                obj,
                "port",
                JsonNumberLiteral::class.java
            )?.value?.toInt() ?: 8080
        }

        private fun getFilePath(obj: JsonObject, name: String): String? {
            val localName = JsonUtil.getPropertyValueOfType(
                obj,
                name,
                JsonStringLiteral::class.java
            )?.value
            return localName?.let { obj.containingFile?.parent?.findFile(it) }
                ?.virtualFile
                ?.path
        }
    }
}