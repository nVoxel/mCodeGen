package com.voxeldev.mcodegen.v2.dsl.utils.source.edit.step

import com.voxeldev.mcodegen.dsl.source.edit.step.EditStepBaseImpl

/**
 * @param classToInterfacesMap Class name to appending interface names.
 */
data class AppendJavaInterfacesEditStep(
    val classToInterfacesMap: Map<String, Set<String>>,
) : EditStepBaseImpl()