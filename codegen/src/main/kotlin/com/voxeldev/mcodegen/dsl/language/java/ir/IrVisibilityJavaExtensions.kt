package com.voxeldev.mcodegen.dsl.language.java.ir

import com.voxeldev.mcodegen.dsl.ir.IrStringRepresentation
import com.voxeldev.mcodegen.dsl.ir.IrVisibility
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPrivate
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityProtected
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPublic
import com.voxeldev.mcodegen.dsl.language.java.JavaModule

context(JavaModule)
internal fun publicVisibility(): IrVisibilityPublic = IrVisibilityPublic(
    listOf(
        IrStringRepresentation(
            "java",
            "public"
        )
    )
)

context(JavaModule)
internal fun protectedVisibility(): IrVisibilityProtected = IrVisibilityProtected(
    listOf(
        IrStringRepresentation(
            "java",
            "protected"
        )
    )
)

context(JavaModule)
internal fun packagePrivateVisibility(): IrVisibilityPackagePrivate = IrVisibilityPackagePrivate()

context(JavaModule)
internal fun privateVisibility(): IrVisibilityPrivate = IrVisibilityPrivate(
    listOf(
        IrStringRepresentation(
            "java",
            "private"
        )
    )
)

data class IrVisibilityPackagePrivate(
    override val stringRepresentation: List<IrStringRepresentation> = listOf(
        IrStringRepresentation(
            language = "java",
            representation = "package-private",
        )
    )
) : IrVisibility(
    stringRepresentation = stringRepresentation,
)