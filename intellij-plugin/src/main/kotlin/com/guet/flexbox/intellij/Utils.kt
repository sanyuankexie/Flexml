package com.guet.flexbox.intellij

import com.guet.flexbox.intellij.fileType.FlexmlFileType
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.jar.JarApplicationCommandLineState
import com.intellij.execution.jar.JarApplicationConfiguration
import com.intellij.execution.jar.JarApplicationConfigurationType
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import java.util.regex.Pattern

val PsiElement.isOnFlexmlFile: Boolean
    get() {
        return if (this.containingFile != null) {
            this.containingFile.name
                    .endsWith("." + FlexmlFileType.defaultExtension,
                            ignoreCase = true
                    )
        } else {
            false
        }
    }

private val pattern = Pattern.compile(
        "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?"
)

fun runJar(
        project: Project,
        environment: ExecutionEnvironment,
        jar: String,
        args: String
): RunProfileState {
    return JarApplicationCommandLineState(
            JarApplicationConfiguration(
                    project,
                    JarApplicationConfigurationType.getInstance(),
                    "Mock this package"
            ).apply {
                jarPath = jar
                programParameters = args
                vmParameters = "-Xmx64m -Xss256k -XX:ParallelGCThreads=1"
            },
            environment
    )
}

val String.isUrl: Boolean
    get() {
        return pattern.matcher(this).matches()
    }

