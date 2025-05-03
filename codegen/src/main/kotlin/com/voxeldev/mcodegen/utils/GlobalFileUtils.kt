package com.voxeldev.mcodegen.utils

import com.voxeldev.mcodegen.utils.GlobalCompilerUtils.project
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

    fun loadKtFilesFromSourceRoot(sourceRoot: File): List<KtFile> {
        require(sourceRoot.isDirectory) {
            "Path ${sourceRoot.absolutePath} is not a directory"
        }

        val result = mutableListOf<KtFile>()

        sourceRoot.walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .forEach { ktFileOnDisk ->
                try {
                    val code     = ktFileOnDisk.asString()
                    val relPath  = sourceRoot.toPath().relativize(ktFileOnDisk.toPath()).toString()
                    val psiFile  = parseKotlinFile(code, relPath)
                    result += psiFile
                } catch (io: IOException) {
                    System.err.println("Could not read ${ktFileOnDisk.path}: ${io.message}")
                }
            }

        return result
    }
}