package com.voxeldev.mcodegen.dsl.language.kotlin.ir

import com.voxeldev.mcodegen.dsl.ir.IrStringRepresentation
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityInternal
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPrivate
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityProtected
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPublic
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule

fun kotlinPublicVisibility(): IrVisibilityPublic = IrVisibilityPublic(
    listOf(
        IrStringRepresentation(
            KotlinModule.languageName,
            "public"
        )
    )
)

fun kotlinProtectedVisibility(): IrVisibilityProtected = IrVisibilityProtected(
    listOf(
        IrStringRepresentation(
            KotlinModule.languageName,
            "protected"
        )
    )
)

fun kotlinInternalVisibility(): IrVisibilityInternal = IrVisibilityInternal(
    listOf(
        IrStringRepresentation(
            KotlinModule.languageName,
            "internal"
        )
    )
)


fun kotlinPrivateVisibility(): IrVisibilityPrivate = IrVisibilityPrivate(
    listOf(
        IrStringRepresentation(
            KotlinModule.languageName,
            "private"
        )
    )
)