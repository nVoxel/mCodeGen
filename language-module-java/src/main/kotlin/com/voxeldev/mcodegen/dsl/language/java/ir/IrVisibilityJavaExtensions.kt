package com.voxeldev.mcodegen.dsl.language.java.ir

import com.voxeldev.mcodegen.dsl.ir.IrStringRepresentation
import com.voxeldev.mcodegen.dsl.ir.IrVisibility
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPrivate
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityProtected
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPublic
import com.voxeldev.mcodegen.dsl.language.java.JavaModule

fun javaPublicVisibility(): IrVisibilityPublic = IrVisibilityPublic(
    listOf(
        IrStringRepresentation(
            JavaModule.languageName,
            "public"
        )
    )
)

fun javaProtectedVisibility(): IrVisibilityProtected = IrVisibilityProtected(
    listOf(
        IrStringRepresentation(
            JavaModule.languageName,
            "protected"
        )
    )
)

fun javaPackagePrivateVisibility(): IrVisibilityPublic = javaPublicVisibility()

fun javaPrivateVisibility(): IrVisibilityPrivate = IrVisibilityPrivate(
    listOf(
        IrStringRepresentation(
            JavaModule.languageName,
            "private"
        )
    )
)

data class IrVisibilityPackagePrivate(
    override val stringRepresentation: List<IrStringRepresentation> = listOf(
        IrStringRepresentation(
            language = JavaModule.languageName,
            representation = "package-private",
        )
    )
) : IrVisibility