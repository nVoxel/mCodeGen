package com.voxeldev.mcodegen.dsl.language.swift.ir.serialization

import com.voxeldev.mcodegen.dsl.ir.IrClassInitializer
import com.voxeldev.mcodegen.dsl.ir.IrClassKind
import com.voxeldev.mcodegen.dsl.language.swift.ir.IrActorClassKind
import com.voxeldev.mcodegen.dsl.language.swift.ir.IrStructClassKind
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

// only IrClassKind impls which can be received from Swift code
val irClassKindSerializers = SerializersModule {
    polymorphic(IrClassKind::class) {
        subclass(IrClassKind.IrClassClassKind::class, IrClassKind.IrClassClassKind.serializer())
        subclass(IrClassKind.IrInterfaceClassKind::class, IrClassKind.IrInterfaceClassKind.serializer())
        subclass(IrClassKind.IrEnumClassKind::class, IrClassKind.IrEnumClassKind.serializer())
        subclass(IrClassKind.IrAnnotationClassKind::class, IrClassKind.IrAnnotationClassKind.serializer())
        subclass(IrActorClassKind::class, IrActorClassKind.serializer())
        subclass(IrStructClassKind::class, IrStructClassKind.serializer())
    }
}

// only IrClassInitializerKind impls which can be received from Swift code
val irClassInitializerKindSerializers = SerializersModule {
    polymorphic(IrClassInitializer.IrClassInitializerKind::class) {
        subclass(
            IrClassInitializer.IrInstanceClassInitializerKind::class,
            IrClassInitializer.IrInstanceClassInitializerKind.serializer(),
        )
        subclass(
            IrClassInitializer.IrStaticClassInitializerKind::class,
            IrClassInitializer.IrStaticClassInitializerKind.serializer(),
        )
    }
}
