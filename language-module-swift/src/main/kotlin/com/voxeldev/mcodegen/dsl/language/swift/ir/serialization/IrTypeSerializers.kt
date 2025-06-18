package com.voxeldev.mcodegen.dsl.language.swift.ir.serialization

import com.voxeldev.mcodegen.dsl.ir.IrType
import com.voxeldev.mcodegen.dsl.ir.IrTypeArray
import com.voxeldev.mcodegen.dsl.ir.IrTypeFunction
import com.voxeldev.mcodegen.dsl.ir.IrTypeGeneric
import com.voxeldev.mcodegen.dsl.ir.IrTypePrimitive
import com.voxeldev.mcodegen.dsl.ir.IrTypeReference
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

// only IrType impls which can be received from Swift code
val irTypeSerializers = SerializersModule {
    polymorphic(IrType::class) {
        subclass(IrTypeReference::class, IrTypeReference.serializer())
        subclass(IrTypePrimitive::class, IrTypePrimitive.serializer())
        subclass(IrTypeFunction::class, IrTypeFunction.serializer())
        subclass(IrTypeArray::class, IrTypeArray.serializer())
        subclass(IrTypeGeneric::class, IrTypeGeneric.serializer())
    }
}

// only PrimitiveType impls which can be received from Swift code
val primitiveTypeSerializers = SerializersModule {
    polymorphic(IrTypePrimitive.PrimitiveType::class) {
        subclass(IrTypePrimitive.PrimitiveType.Void::class, IrTypePrimitive.PrimitiveType.Void.serializer())
        subclass(IrTypePrimitive.PrimitiveType.Boolean::class, IrTypePrimitive.PrimitiveType.Boolean.serializer())
        subclass(IrTypePrimitive.PrimitiveType.Byte::class, IrTypePrimitive.PrimitiveType.Byte.serializer())
        subclass(IrTypePrimitive.PrimitiveType.Short::class, IrTypePrimitive.PrimitiveType.Short.serializer())
        subclass(IrTypePrimitive.PrimitiveType.Int::class, IrTypePrimitive.PrimitiveType.Int.serializer())
        subclass(IrTypePrimitive.PrimitiveType.Long::class, IrTypePrimitive.PrimitiveType.Long.serializer())
        subclass(IrTypePrimitive.PrimitiveType.Char::class, IrTypePrimitive.PrimitiveType.Char.serializer())
        subclass(IrTypePrimitive.PrimitiveType.Float::class, IrTypePrimitive.PrimitiveType.Float.serializer())
        subclass(IrTypePrimitive.PrimitiveType.Double::class, IrTypePrimitive.PrimitiveType.Double.serializer())
    }
}
