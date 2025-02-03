package com.voxeldev.mcodegen.scenarios.tdCommon

import com.voxeldev.mcodegen.interfaces.Scenario
import com.voxeldev.mcodegen.scenarios.tdCommon.generators.CommonInterfacesGenerator
import com.voxeldev.mcodegen.scenarios.tdCommon.generators.InstanceGetterImplsGenerator
import com.voxeldev.mcodegen.scenarios.tdCommon.generators.InstanceGettersKoinModulesGenerator
import com.voxeldev.mcodegen.scenarios.tdCommon.supplementers.TdApiSupplementer
import com.voxeldev.mcodegen.scenarios.tdCommon.utils.TdCommonScenarioPsiUtils
import com.voxeldev.mcodegen.utils.GlobalFileUtils
import com.voxeldev.mcodegen.utils.GlobalFileUtils.asString
import com.voxeldev.mcodegen.utils.GlobalTelegramApiUtils
import org.jetbrains.kotlin.com.intellij.psi.PsiClass

object TdCommonScenario : Scenario {

    override fun run() {
        val androidSourceString = GlobalTelegramApiUtils.getAndroidTelegramApiFile().asString()
        val desktopSourceString = GlobalTelegramApiUtils.getDesktopTelegramApiFile().asString()

        val androidClasses: Array<PsiClass> = GlobalFileUtils.parseJavaFile(androidSourceString, "TdApi.java")
            .classes.first().innerClasses
        val desktopClasses: Array<PsiClass> = GlobalFileUtils.parseJavaFile(desktopSourceString, "TdApi.java")
            .classes.first().innerClasses

        val commonClasses = TdCommonScenarioPsiUtils.getCommonClasses(androidClasses, desktopClasses)

        val androidInstanceGetterOutputPath = TdCommonScenarioConstants.outputPathAndroid
        val desktopInstanceGetterOutputPath = TdCommonScenarioConstants.outputPathDesktop

        CommonInterfacesGenerator.generate(commonClasses)

        InstanceGetterImplsGenerator.generate(androidInstanceGetterOutputPath, commonClasses)
        InstanceGetterImplsGenerator.generate(desktopInstanceGetterOutputPath, commonClasses)

        InstanceGettersKoinModulesGenerator.generate(androidInstanceGetterOutputPath, commonClasses)
        InstanceGettersKoinModulesGenerator.generate(desktopInstanceGetterOutputPath, commonClasses)

        TdApiSupplementer.supplement(
            outputPath = "${TdCommonScenarioConstants.outputPathAndroid}/${TdCommonScenarioConstants.TDLIB_PATH_SEGMENT}",
            sourceCode = androidSourceString,
            commonClasses = commonClasses
        )

        TdApiSupplementer.supplement(
            outputPath = "${TdCommonScenarioConstants.outputPathDesktop}/${TdCommonScenarioConstants.TDLIB_PATH_SEGMENT}",
            sourceCode = desktopSourceString,
            commonClasses = commonClasses
        )
    }
}