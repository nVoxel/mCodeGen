package com.voxeldev.mcodegen.dsl.utils

import com.voxeldev.mcodegen.dsl.utils.GlobalCompilerUtils.project
import org.jetbrains.kotlin.com.intellij.ide.highlighter.JavaFileType
import org.jetbrains.kotlin.com.intellij.psi.PsiJavaFile
import org.jetbrains.kotlin.com.intellij.psi.PsiManager
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtFile
import java.io.File
import java.io.IOException
import java.util.*

object GlobalFileUtils {

    fun parseJavaFile(codeString: String, fileName: String) =
        PsiManager.getInstance(project).findFile(
            LightVirtualFile(fileName, JavaFileType.INSTANCE, codeString)
        ) as PsiJavaFile

    fun parseKotlinFile(codeString: String, fileName: String) =
        PsiManager.getInstance(project).findFile(
            LightVirtualFile(fileName, KotlinFileType.INSTANCE, codeString)
        ) as KtFile

    @Throws(IOException::class)
    fun File.asString(): String {
        val fileContents = StringBuilder(this.length().toInt())
        Scanner(this).use { scanner ->
            while (scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine() + System.lineSeparator())
            }
            return fileContents.toString()
        }
    }
}