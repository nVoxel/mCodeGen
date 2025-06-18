package com.voxeldev.mcodegen.dsl.language.swift.ir.serialization

import kotlinx.serialization.modules.SerializersModule

val allSerializers = SerializersModule {
    include(irClassKindSerializers)
    include(irClassInitializerKindSerializers)
    include(irElementSerializers)
    include(irExpressionSerializers)
    include(irStatementSerializers)
    include(irMethodSerializers)
    include(irMethodCallKindSerializers)
    include(irBinaryOperatorSerializers)
    include(irUnaryOperatorSerializers)
    include(irAssignmentOperatorSerializers)
    include(irTypeSerializers)
    include(primitiveTypeSerializers)
    include(irVisibilitySerializers)
}