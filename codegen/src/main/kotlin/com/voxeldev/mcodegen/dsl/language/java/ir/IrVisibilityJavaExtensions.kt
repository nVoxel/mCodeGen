package com.voxeldev.mcodegen.dsl.language.java.ir

import com.voxeldev.mcodegen.dsl.ir.IrStringRepresentation
import com.voxeldev.mcodegen.dsl.ir.IrVisibility

data class IrVisibilityPackagePrivate(
    override val stringRepresentation: IrStringRepresentation = IrStringRepresentation(
        language = "java",
        representation = "package-private",
    )
) : IrVisibility(
    stringRepresentation = stringRepresentation,
)