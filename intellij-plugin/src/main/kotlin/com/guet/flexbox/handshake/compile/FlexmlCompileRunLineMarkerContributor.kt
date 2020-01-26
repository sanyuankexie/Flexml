package com.guet.flexbox.handshake.compile

import com.guet.flexbox.handshake.util.isOnFlexmlFile
import com.intellij.execution.lineMarker.ExecutorAction
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlFile
import com.intellij.util.containers.ContainerUtil

class FlexmlCompileRunLineMarkerContributor : RunLineMarkerContributor() {
    override fun getInfo(element: PsiElement): Info? {
        if (!element.isOnFlexmlFile) {
            return null
        }
        val file = element.containingFile
        if (file is XmlFile) {
            if (file.document == element) {
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
        }
        return null
    }
}