package com.voxeldev.mcodegen.dsl.language.kotlin.ir

import com.voxeldev.mcodegen.dsl.ir.IrStringRepresentation
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityInternal
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPrivate
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityProtected
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPublic
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule

context(KotlinModule)
internal fun publicVisibility(): IrVisibilityPublic = IrVisibilityPublic(
    listOf(
        IrStringRepresentation(
            languageName,
            "public"
        )
    )
)

context(KotlinModule)
internal fun protectedVisibility(): IrVisibilityProtected = IrVisibilityProtected(
    listOf(
        IrStringRepresentation(
            languageName,
            "protected"
        )
    )
)

context(KotlinModule)
internal fun internalVisibility(): IrVisibilityInternal = IrVisibilityInternal(
    listOf(
        IrStringRepresentation(
            languageName,
            "internal"
        )
    )
)


context(KotlinModule)
internal fun privateVisibility(): IrVisibilityPrivate = IrVisibilityPrivate(
    listOf(
        IrStringRepresentation(
            languageName,
            "private"
        )
    )
)