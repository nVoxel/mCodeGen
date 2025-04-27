package com.voxeldev.mcodegen.utils

import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.CliBindingTrace
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.JvmPackagePartProvider
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.TopDownAnalyzerFacadeForJVM
import org.jetbrains.kotlin.com.intellij.ide.highlighter.JavaFileType
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.psi.PsiJavaFile
import org.jetbrains.kotlin.com.intellij.psi.PsiManager
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.languageVersionSettings
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

    val project by lazy {
        val configuration = CompilerConfiguration()
        configuration.apply {
            put(
                CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY,
                PrintingMessageCollector(
                    System.err,
                    MessageRenderer.PLAIN_RELATIVE_PATHS,
                    false
                )
            )

            put(CommonConfigurationKeys.MODULE_NAME, "rootModule")
        }

        val env = KotlinCoreEnvironment.createForTests(
            Disposer.newDisposable(),
            configuration,
            EnvironmentConfigFiles.JVM_CONFIG_FILES
        )

        TopDownAnalyzerFacadeForJVM.analyzeFilesWithJavaIntegration(
            project = env.project,
            files = emptyList(),
            trace = CliBindingTrace(),
            configuration = configuration,
            packagePartProvider = { scope -> JvmPackagePartProvider(configuration.languageVersionSettings, scope)}
        )

        env.project
    }
}