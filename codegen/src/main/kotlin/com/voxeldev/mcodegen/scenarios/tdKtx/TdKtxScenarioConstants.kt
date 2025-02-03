package com.voxeldev.mcodegen.scenarios.tdKtx

import com.voxeldev.mcodegen.utils.GlobalPathUtils

object TdKtxScenarioConstants {
    const val FUNCTIONS_PACKAGE = "kotlinx.telegram.coroutines"
    const val FLOW_PACKAGE = "kotlinx.telegram.flows"
    const val CORE_PACKAGE = "kotlinx.telegram.core"
    const val API_NAME = "TelegramFlow"

    val ktxOutputPath = "${GlobalPathUtils.outputPath}/ktxScenario/src/main/java"
}