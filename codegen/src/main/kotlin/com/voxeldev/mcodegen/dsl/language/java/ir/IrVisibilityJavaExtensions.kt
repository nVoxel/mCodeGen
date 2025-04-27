package com.voxeldev.mcodegen.dsl.language.java.ir

import com.voxeldev.mcodegen.dsl.ir.IrStringRepresentation
import com.voxeldev.mcodegen.dsl.ir.IrVisibility
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPrivate
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityProtected
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPublic
import com.voxeldev.mcodegen.dsl.language.java.JavaModule

context(JavaModule)
internal fun publicVisibility(): IrVisibilityPublic = IrVisibilityPublic(
    IrStringRepresentation(
        "java",
        "public"
    )
)

context(JavaModule)
internal fun protectedVisibility(): IrVisibilityProtected = IrVisibilityProtected(
    IrStringRepresentation(
        "java",
        "protected"
    )
)

context(JavaModule)
internal fun packagePrivateVisibility(): IrVisibilityPackagePrivate = IrVisibilityPackagePrivate()

context(JavaModule)
internal fun privateVisibility(): IrVisibilityPrivate = IrVisibilityPrivate(
    IrStringRepresentation(
        "java",
        "private"
    )
)

data class IrVisibilityPackagePrivate(
    override val stringRepresentation: IrStringRepresentation = IrStringRepresentation(
        language = "java",
        representation = "package-private",
    )
) : IrVisibility(
    stringRepresentation = stringRepresentation,
)