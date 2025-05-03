package com.voxeldev.mcodegen.utils

import com.voxeldev.mcodegen.utils.GlobalFileUtils.asString
import com.voxeldev.mcodegen.utils.GlobalFileUtils.parseKotlinFile
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.CliBindingTrace
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.JvmPackagePartProvider
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.TopDownAnalyzerFacadeForJVM
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.psi.KtFile
import java.io.File
import java.io.IOException

object GlobalCompilerUtils {

    // Source roots allows the compiler to get additional info about the Kotlin code being examined.
    // It gives several benefits (e.g. actual IrClass will be used for superclasses instead of the IrClassStub),
    //  but increases analysis time (depending on the amount of provided source roots files).
    // Any directory with .kt source files can be added as a source root, for example, sources of a used library.
    private val ktSourceRoots = mutableListOf<File>()

    private val ktSourceRootsFiles = hashMapOf<File, List<KtFile>>()

    private val compilerConfiguration = CompilerConfiguration().apply {
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

    val project by lazy {
        val env = KotlinCoreEnvironment.createForTests(
            Disposer.newDisposable(),
            compilerConfiguration,
            EnvironmentConfigFiles.JVM_CONFIG_FILES
        )

        // bootstrap compiler infrastructure
        TopDownAnalyzerFacadeForJVM.analyzeFilesWithJavaIntegration(
            project = env.project,
            files = emptyList(),
            trace = CliBindingTrace(),
            configuration = compilerConfiguration,
            packagePartProvider = { scope ->
                JvmPackagePartProvider(
                    languageVersionSettings = compilerConfiguration.languageVersionSettings,
                    scope = scope,
                )
            }
        )

        env.project
    }

    fun getAnalysisResult(vararg files: KtFile): AnalysisResult {
        return TopDownAnalyzerFacadeForJVM.analyzeFilesWithJavaIntegration(
            project = project,
            files = getSourceRootsKtFiles() + files.toList(),
            trace = CliBindingTrace(),
            configuration = compilerConfiguration,
            packagePartProvider = { scope ->
                JvmPackagePartProvider(
                    languageVersionSettings = compilerConfiguration.languageVersionSettings,
                    scope = scope,
                )
            }
        )
    }

    fun addKtSourceRoot(ktSourceRoot: File) {
        ktSourceRoots.add(ktSourceRoot)
    }

    fun removeKtSourceRoot(ktSourceRoot: File) {
        ktSourceRoots.remove(ktSourceRoot)
    }

    private fun getSourceRootsKtFiles(): List<KtFile> {
        loadMissingKtFilesFromSourceRoot()
        return ktSourceRootsFiles.flatMap { it.value }
    }

    private fun loadMissingKtFilesFromSourceRoot() {
        ktSourceRoots
            .filter { sourceRoot -> sourceRoot !in ktSourceRootsFiles }
            .forEach { sourceRoot -> ktSourceRootsFiles[sourceRoot] = loadKtFilesFromSourceRoot(sourceRoot) }
    }

    private fun loadKtFilesFromSourceRoot(sourceRoot: File): List<KtFile> {
        require(sourceRoot.isDirectory) {
            "Path ${sourceRoot.absolutePath} is not a directory"
        }

        val result = mutableListOf<KtFile>()

        sourceRoot.walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .forEach { ktFileOnDisk ->
                try {
                    val code = ktFileOnDisk.asString()
                    val relPath = sourceRoot.toPath().relativize(ktFileOnDisk.toPath()).toString()
                    val psiFile = parseKotlinFile(code, relPath)
                    result += psiFile
                } catch (io: IOException) {
                    System.err.println("Could not read ${ktFileOnDisk.path}: ${io.message}")
                }
            }

        return result
    }
}