package com.voxeldev.mcodegen.scenarios.tdCommon

import com.voxeldev.mcodegen.utils.GlobalPathUtils


object TdCommonScenarioConstants {
    const val TDLIB_PATH_SEGMENT = "org/drinkless/tdlib"

    const val CONSTRUCTOR_FIELD = "CONSTRUCTOR"

    const val MAX_LINES_PER_MODULE = 250

    val outputPathCommon = "${GlobalPathUtils.outputPath}/commonScenario/common/src/commonMain/kotlin"
    val outputPathAndroid = "${GlobalPathUtils.outputPath}/commonScenario/android/src/jvmMain/java"
    val outputPathDesktop = "${GlobalPathUtils.outputPath}/commonScenario/desktop/src/jvmMain/java"

    const val outputFileName = "TdApi.java"
}