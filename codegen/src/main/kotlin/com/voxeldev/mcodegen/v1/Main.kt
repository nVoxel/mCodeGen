package com.voxeldev.mcodegen.v1

import com.voxeldev.mcodegen.v1.interfaces.Scenario
import com.voxeldev.mcodegen.v1.scenarios.tdKtx.TdKtxScenario

fun main() {
    val scenarios = listOf<Scenario>(
        TdKtxScenario,
    )

    scenarios.forEach { scenario ->
        scenario.run()
    }
}