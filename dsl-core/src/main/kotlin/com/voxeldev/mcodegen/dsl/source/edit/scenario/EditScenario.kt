package com.voxeldev.mcodegen.dsl.source.edit.scenario

import com.voxeldev.mcodegen.dsl.source.edit.step.EditStep

interface EditScenario {
    fun getSteps(): List<EditStep>
}