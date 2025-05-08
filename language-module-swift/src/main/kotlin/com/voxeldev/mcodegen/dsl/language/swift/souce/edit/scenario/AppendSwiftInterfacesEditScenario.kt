package com.voxeldev.mcodegen.dsl.language.swift.souce.edit.scenario

import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import com.voxeldev.mcodegen.dsl.source.edit.scenario.EditScenarioBaseImpl
import com.voxeldev.mcodegen.dsl.source.edit.step.EditStep

context(ScenarioScope)
fun appendSwiftInterfacesScenario(): AppendSwiftInterfacesEditScenario {
    return AppendSwiftInterfacesEditScenario()
}

context(ScenarioScope)
class AppendSwiftInterfacesEditScenario internal constructor(): EditScenarioBaseImpl() {

    override fun getSteps(): List<EditStep> {
        TODO("Not yet implemented")
    }
}