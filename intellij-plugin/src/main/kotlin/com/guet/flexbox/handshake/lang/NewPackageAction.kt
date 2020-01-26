package com.guet.flexbox.handshake.lang

import com.guet.flexbox.handshake.util.fileIcon
import com.intellij.CommonBundle
import com.intellij.ide.actions.CreateElementActionBase
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.json.JsonFileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.codeStyle.CodeStyleManager

class NewPackageAction : CreateElementActionBase("", "", fileIcon) {

    override fun invokeDialog(project: Project?, directory: PsiDirectory?): Array<PsiElement> {
        val inputValidator = this.MyInputValidator(project, directory)
        Messages.showInputDialog(
            project,
            "New a flexml package",
            "New a flexml package",
            null,
            "",
            inputValidator
        )
        return inputValidator.createdElements
    }

    override fun create(newName: String, directory: PsiDirectory): Array<PsiElement> {
        if (directory.findSubdirectory(newName) != null) {
            throw RuntimeException("Package already exists")
        }
        val factory = PsiFileFactory.getInstance(directory.project)
        val packageText = FileTemplateManager.getInstance(directory.project)
            .getInternalTemplate("package").text
        val packageFile = factory.createFileFromText(
            "package.json",
            JsonFileType.INSTANCE,
            packageText
        )
        val templateText = FileTemplateManager.getInstance(directory.project)
            .getInternalTemplate("flexml_file").text
        val templateFile = factory.createFileFromText(
            "template.flexml",
            FlexmlFileType, templateText)
        val dataSourceFile = factory.createFileFromText(
            "data.json",
            JsonFileType.INSTANCE,
            "{}"
        )
        CodeStyleManager.getInstance(directory.project).reformat(packageFile)
        CodeStyleManager.getInstance(directory.project).reformat(templateFile)
        CodeStyleManager.getInstance(directory.project).reformat(dataSourceFile)
        val root = directory.createSubdirectory(newName)
        root.add(packageFile)
        root.add(templateFile)
        root.add(dataSourceFile)
        return arrayOf(packageFile, templateFile, dataSourceFile)
    }

    override fun getActionName(directory: PsiDirectory?, newName: String?): String = "Flexml package"

    override fun getCommandName(): String = "Create flexml mock package"

    override fun getErrorTitle(): String = CommonBundle.getErrorTitle()
}