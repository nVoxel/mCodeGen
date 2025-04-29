package com.voxeldev.mcodegen.dsl.language.java.ir

import com.voxeldev.mcodegen.dsl.ir.IrStringRepresentation
import com.voxeldev.mcodegen.dsl.ir.IrVisibility
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPrivate
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityProtected
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPublic
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.language.java.JavaModule.languageName

context(JavaModule)
internal fun publicVisibility(): IrVisibilityPublic = IrVisibilityPublic(
    listOf(
        IrStringRepresentation(
            languageName,
            "public"
        )
    )
)

context(JavaModule)
internal fun protectedVisibility(): IrVisibilityProtected = IrVisibilityProtected(
    listOf(
        IrStringRepresentation(
            languageName,
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
            languageName,
            "private"
        )
    )
)

data class IrVisibilityPackagePrivate(
    override val stringRepresentation: List<IrStringRepresentation> = listOf(
        IrStringRepresentation(
            language = languageName,
            representation = "package-private",
        )
    )
) : IrVisibility(
    stringRepresentation = stringRepresentation,
)