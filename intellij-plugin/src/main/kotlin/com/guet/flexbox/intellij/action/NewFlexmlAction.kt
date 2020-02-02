package com.guet.flexbox.intellij.action

import com.guet.flexbox.intellij.res.Icons
import com.guet.flexbox.intellij.fileType.FlexmlFileType
import com.intellij.CommonBundle
import com.intellij.ide.actions.CreateElementActionBase
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.codeStyle.CodeStyleManager

class NewFlexmlAction : CreateElementActionBase("", "", Icons.typeIcon) {

    override fun invokeDialog(project: Project?, directory: PsiDirectory?): Array<PsiElement> {
        val inputValidator = this.MyInputValidator(project, directory)
        Messages.showInputDialog(
            project,
            "New a flexml dsl file",
            "New a flexml dsl file",
            null,
            "",
            inputValidator
        )
        return inputValidator.createdElements
    }

    override fun create(newName: String, directory: PsiDirectory): Array<PsiElement> {
        val ext = ".${FlexmlFileType.defaultExtension}"
        val filename = if (newName.endsWith(ext)) {
            newName
        } else {
            newName + ext
        }
        val template = FileTemplateManager.getInstance(directory.project)
            .getInternalTemplate("flexml-file")
        val text = template.text
        val factory = PsiFileFactory.getInstance(directory.project)
        val file = factory.createFileFromText(filename, FlexmlFileType, text)
        CodeStyleManager.getInstance(directory.project).reformat(file)
        directory.add(file)
        return arrayOf(file)
    }

    override fun getActionName(directory: PsiDirectory?, newName: String?): String = "Flexml File"

    override fun getCommandName(): String = "Create Flexml File"

    override fun getErrorTitle(): String = CommonBundle.getErrorTitle()

}