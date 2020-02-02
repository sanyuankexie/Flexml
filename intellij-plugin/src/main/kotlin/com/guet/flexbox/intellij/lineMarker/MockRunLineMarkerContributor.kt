package com.guet.flexbox.intellij.lineMarker

import com.guet.flexbox.intellij.isOnFlexmlFile
import com.intellij.execution.lineMarker.ExecutorAction
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.json.psi.JsonFile
import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.util.containers.ContainerUtil

class MockRunLineMarkerContributor : RunLineMarkerContributor() {

    override fun getInfo(element: PsiElement): Info? {
        val file = element.containingFile?.let { it as? JsonFile }
        val obj = file?.topLevelValue?.let { it as? JsonObject }
        if (file?.name != "package.json") {
            return null
        }
        val template = obj?.findProperty("template")
                ?: return null
        if (template != element) {
            return null
        }
        if (template.value?.let { it as? JsonStringLiteral }
                        ?.value?.let { file.parent?.findFile(it) }
                        ?.isOnFlexmlFile == true) {
            val actions = ExecutorAction.getActions()
            return Info(
                    AllIcons.RunConfigurations.TestState.Run,
                    actions
            ) { e ->
                StringUtil.join(ContainerUtil.mapNotNull<AnAction, String>(actions) { action ->
                    getText(
                            action,
                            e
                    )
                }, "\n")

            }
        }
        return null
    }
}