package com.voxeldev.mcodegen.dsl.language.swift.ir.serialization

import com.voxeldev.mcodegen.dsl.ir.IrVisibility
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityInternal
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPrivate
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPublic
import com.voxeldev.mcodegen.dsl.language.swift.ir.IrVisibilityFileprivate
import com.voxeldev.mcodegen.dsl.language.swift.ir.IrVisibilityOpen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

// only IrVisibility impls which can be received from Swift code
val irVisibilitySerializers = SerializersModule {
    polymorphic(IrVisibility::class) {
        subclass(IrVisibilityPublic::class, IrVisibilityPublic.serializer())
        subclass(IrVisibilityInternal::class, IrVisibilityInternal.serializer())
        subclass(IrVisibilityPrivate::class, IrVisibilityPrivate.serializer())
        subclass(IrVisibilityOpen::class, IrVisibilityOpen.serializer())
        subclass(IrVisibilityFileprivate::class, IrVisibilityFileprivate.serializer())
    }
}