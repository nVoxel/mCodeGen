package com.voxeldev.mcodegen.dsl.source.edit.scenario

import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import com.voxeldev.mcodegen.dsl.source.edit.step.EditStep

context(ScenarioScope)
abstract class EditScenarioBaseImpl : EditScenario {
    abstract override fun getSteps(): List<EditStep>
}