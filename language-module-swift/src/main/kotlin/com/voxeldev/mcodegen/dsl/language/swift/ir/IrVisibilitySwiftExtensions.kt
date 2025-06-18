package com.voxeldev.mcodegen.dsl.language.swift.ir

import com.voxeldev.mcodegen.dsl.ir.IrStringRepresentation
import com.voxeldev.mcodegen.dsl.ir.IrVisibility
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityInternal
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPrivate
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPublic
import com.voxeldev.mcodegen.dsl.language.swift.SwiftModule
import kotlinx.serialization.Serializable

fun swiftOpenVisibility(): IrVisibilityOpen = IrVisibilityOpen()

fun swiftPublicVisibility(): IrVisibilityPublic = IrVisibilityPublic(
    listOf(
        IrStringRepresentation(
            SwiftModule.languageName,
            "public"
        )
    )
)

fun swiftInternalVisibility(): IrVisibilityInternal = IrVisibilityInternal(
    listOf(
        IrStringRepresentation(
            SwiftModule.languageName,
            "internal"
        )
    )
)

fun swiftFileprivateVisibility(): IrVisibilityFileprivate = IrVisibilityFileprivate()

fun swiftPrivateVisibility(): IrVisibilityPrivate = IrVisibilityPrivate(
    listOf(
        IrStringRepresentation(
            SwiftModule.languageName,
            "private"
        )
    )
)

@Serializable
data class IrVisibilityOpen(
    override val stringRepresentation: List<IrStringRepresentation> = listOf(
        IrStringRepresentation(
            language = SwiftModule.languageName,
            representation = "open",
        )
    )
) : IrVisibility

@Serializable
data class IrVisibilityFileprivate(
    override val stringRepresentation: List<IrStringRepresentation> = listOf(
        IrStringRepresentation(
            language = SwiftModule.languageName,
            representation = "fileprivate",
        )
    )
) : IrVisibility
