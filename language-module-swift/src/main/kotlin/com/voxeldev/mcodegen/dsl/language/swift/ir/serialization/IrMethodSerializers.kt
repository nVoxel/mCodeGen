package com.voxeldev.mcodegen.dsl.language.swift.ir.serialization

import com.voxeldev.mcodegen.dsl.ir.IrCallable
import com.voxeldev.mcodegen.dsl.ir.IrConstructor
import com.voxeldev.mcodegen.dsl.ir.IrMethod
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

// only IrMethod impls which can be received from Swift code
val irMethodSerializers = SerializersModule {
    polymorphic(IrCallable::class) {
        subclass(
            IrMethod::class,
            IrMethod.serializer(),
        )
        subclass(
            IrConstructor::class,
            IrConstructor.serializer(),
        )
    }
}