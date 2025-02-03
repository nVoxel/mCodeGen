package com.voxeldev.mcodegen

import com.voxeldev.mcodegen.interfaces.Scenario
import com.voxeldev.mcodegen.scenarios.tdCommon.TdCommonScenario
import com.voxeldev.mcodegen.scenarios.tdKtx.TdKtxScenario

fun main() {
    val scenarios = listOf<Scenario>(
        TdCommonScenario,
        TdKtxScenario,
    )

    scenarios.forEach { scenario ->
        scenario.run()
    }
}