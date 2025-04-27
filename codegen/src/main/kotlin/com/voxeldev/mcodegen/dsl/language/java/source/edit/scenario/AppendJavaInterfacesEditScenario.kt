package com.voxeldev.mcodegen.dsl.language.java.source.edit.scenario

import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import com.voxeldev.mcodegen.dsl.source.edit.scenario.EditScenarioBaseImpl
import com.voxeldev.mcodegen.dsl.source.edit.step.EditStep

context(ScenarioScope)
fun appendJavaInterfacesEditScenario(): AppendJavaInterfacesEditScenario {
    return AppendJavaInterfacesEditScenario()
}

context(ScenarioScope)
class AppendJavaInterfacesEditScenario internal constructor(): EditScenarioBaseImpl() {

    override fun getSteps(): List<EditStep> {
        TODO("Not yet implemented")
    }
}