package com.voxeldev.mcodegen.dsl.language.swift.ir.serialization

import com.voxeldev.mcodegen.dsl.ir.IrAssignmentExpression
import com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression
import com.voxeldev.mcodegen.dsl.ir.IrCastExpression
import com.voxeldev.mcodegen.dsl.ir.IrEmptyExpression
import com.voxeldev.mcodegen.dsl.ir.IrExpression
import com.voxeldev.mcodegen.dsl.ir.IrExpressionUnknown
import com.voxeldev.mcodegen.dsl.ir.IrIdentifierExpression
import com.voxeldev.mcodegen.dsl.ir.IrLiteralExpression
import com.voxeldev.mcodegen.dsl.ir.IrMethodCallExpression
import com.voxeldev.mcodegen.dsl.ir.IrObjectCreationExpression
import com.voxeldev.mcodegen.dsl.ir.IrPropertyAccessExpression
import com.voxeldev.mcodegen.dsl.ir.IrTernaryExpression
import com.voxeldev.mcodegen.dsl.ir.IrTypeCheckExpression
import com.voxeldev.mcodegen.dsl.ir.IrTypeReferenceIdentifierExpression
import com.voxeldev.mcodegen.dsl.ir.IrUnaryExpression
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

// only IrExpression impls which can be received from Swift code
val irExpressionSerializers = SerializersModule {
    polymorphic(IrExpression::class) {
        subclass(
            IrEmptyExpression::class,
            IrEmptyExpression.serializer(),
        )
        subclass(
            IrLiteralExpression::class,
            IrLiteralExpression.serializer(),
        )
        subclass(
            IrIdentifierExpression::class,
            IrIdentifierExpression.serializer(),
        )
        subclass(
            IrTypeReferenceIdentifierExpression::class,
            IrTypeReferenceIdentifierExpression.serializer(),
        )
        subclass(
            IrPropertyAccessExpression::class,
            IrPropertyAccessExpression.serializer(),
        )
        subclass(
            IrMethodCallExpression::class,
            IrMethodCallExpression.serializer(),
        )
        subclass(
            IrObjectCreationExpression::class,
            IrObjectCreationExpression.serializer(),
        )
        subclass(
            IrBinaryExpression::class,
            IrBinaryExpression.serializer(),
        )
        subclass(
            IrUnaryExpression::class,
            IrUnaryExpression.serializer(),
        )
        subclass(
            IrAssignmentExpression::class,
            IrAssignmentExpression.serializer(),
        )
        subclass(
            IrTernaryExpression::class,
            IrTernaryExpression.serializer(),
        )
        subclass(
            IrCastExpression::class,
            IrCastExpression.serializer(),
        )
        subclass(
            IrTypeCheckExpression::class,
            IrTypeCheckExpression.serializer(),
        )
        subclass(
            IrExpressionUnknown::class,
            IrExpressionUnknown.serializer(),
        )
    }
}

// only IrMethodCallKind impls which can be received from Swift code
val irMethodCallKindSerializers = SerializersModule {
    polymorphic(IrMethodCallExpression.IrMethodCallKind::class) {
        subclass(
            IrMethodCallExpression.IrDefaultMethodCallKind::class,
            IrMethodCallExpression.IrDefaultMethodCallKind.serializer(),
        )
        subclass(
            IrMethodCallExpression.IrThisMethodCallKind::class,
            IrMethodCallExpression.IrThisMethodCallKind.serializer(),
        )
        subclass(
            IrMethodCallExpression.IrSuperMethodCallKind::class,
            IrMethodCallExpression.IrSuperMethodCallKind.serializer(),
        )
    }
}

// only IrBinaryOperator impls which can be received from Swift code
val irBinaryOperatorSerializers = SerializersModule {
    polymorphic(IrBinaryExpression.IrBinaryOperator::class) {
        subclass(
            IrBinaryExpression.IrBinaryOperator.Plus::class,
            IrBinaryExpression.IrBinaryOperator.Plus.serializer(),
        )
        subclass(
            IrBinaryExpression.IrBinaryOperator.Minus::class,
            IrBinaryExpression.IrBinaryOperator.Minus.serializer(),
        )
        subclass(
            IrBinaryExpression.IrBinaryOperator.Multiply::class,
            IrBinaryExpression.IrBinaryOperator.Multiply.serializer(),
        )
        subclass(
            IrBinaryExpression.IrBinaryOperator.Divide::class,
            IrBinaryExpression.IrBinaryOperator.Divide.serializer(),
        )
        subclass(
            IrBinaryExpression.IrBinaryOperator.Modulo::class,
            IrBinaryExpression.IrBinaryOperator.Modulo.serializer(),
        )
        subclass(
            IrBinaryExpression.IrBinaryOperator.Equals::class,
            IrBinaryExpression.IrBinaryOperator.Equals.serializer(),
        )
        subclass(
            IrBinaryExpression.IrBinaryOperator.NotEquals::class,
            IrBinaryExpression.IrBinaryOperator.NotEquals.serializer(),
        )
        subclass(
            IrBinaryExpression.IrBinaryOperator.Greater::class,
            IrBinaryExpression.IrBinaryOperator.Greater.serializer(),
        )
        subclass(
            IrBinaryExpression.IrBinaryOperator.GreaterOrEqual::class,
            IrBinaryExpression.IrBinaryOperator.GreaterOrEqual.serializer(),
        )
        subclass(
            IrBinaryExpression.IrBinaryOperator.Less::class,
            IrBinaryExpression.IrBinaryOperator.Less.serializer(),
        )
        subclass(
            IrBinaryExpression.IrBinaryOperator.LessOrEqual::class,
            IrBinaryExpression.IrBinaryOperator.LessOrEqual.serializer(),
        )
        subclass(
            IrBinaryExpression.IrBinaryOperator.And::class,
            IrBinaryExpression.IrBinaryOperator.And.serializer(),
        )
        subclass(
            IrBinaryExpression.IrBinaryOperator.Or::class,
            IrBinaryExpression.IrBinaryOperator.Or.serializer(),
        )
        subclass(
            IrBinaryExpression.IrBinaryOperator.BitwiseAnd::class,
            IrBinaryExpression.IrBinaryOperator.BitwiseAnd.serializer(),
        )
        subclass(
            IrBinaryExpression.IrBinaryOperator.BitwiseOr::class,
            IrBinaryExpression.IrBinaryOperator.BitwiseOr.serializer(),
        )
        subclass(
            IrBinaryExpression.IrBinaryOperator.BitwiseXor::class,
            IrBinaryExpression.IrBinaryOperator.BitwiseXor.serializer(),
        )
        subclass(
            IrBinaryExpression.IrBinaryOperator.ShiftLeft::class,
            IrBinaryExpression.IrBinaryOperator.ShiftLeft.serializer(),
        )
        subclass(
            IrBinaryExpression.IrBinaryOperator.ShiftRight::class,
            IrBinaryExpression.IrBinaryOperator.ShiftRight.serializer(),
        )
    }
}

// only IrUnaryOperator impls which can be received from Swift code
val irUnaryOperatorSerializers = SerializersModule {
    polymorphic(IrUnaryExpression.IrUnaryOperator::class) {
        subclass(
            IrUnaryExpression.IrUnaryOperator.Not::class,
            IrUnaryExpression.IrUnaryOperator.Not.serializer(),
        )
        subclass(
            IrUnaryExpression.IrUnaryOperator.Plus::class,
            IrUnaryExpression.IrUnaryOperator.Plus.serializer(),
        )
        subclass(
            IrUnaryExpression.IrUnaryOperator.Minus::class,
            IrUnaryExpression.IrUnaryOperator.Minus.serializer(),
        )
        subclass(
            IrUnaryExpression.IrUnaryOperator.Increment::class,
            IrUnaryExpression.IrUnaryOperator.Increment.serializer(),
        )
        subclass(
            IrUnaryExpression.IrUnaryOperator.Decrement::class,
            IrUnaryExpression.IrUnaryOperator.Decrement.serializer(),
        )
    }
}

// only IrAssignmentOperator impls which can be received from Swift code
val irAssignmentOperatorSerializers = SerializersModule {
    polymorphic(IrAssignmentExpression.IrAssignmentOperator::class) {
        subclass(
            IrAssignmentExpression.IrAssignmentOperator.Assign::class,
            IrAssignmentExpression.IrAssignmentOperator.Assign.serializer(),
        )
        subclass(
            IrAssignmentExpression.IrAssignmentOperator.PlusAssign::class,
            IrAssignmentExpression.IrAssignmentOperator.PlusAssign.serializer(),
        )
        subclass(
            IrAssignmentExpression.IrAssignmentOperator.MinusAssign::class,
            IrAssignmentExpression.IrAssignmentOperator.MinusAssign.serializer(),
        )
        subclass(
            IrAssignmentExpression.IrAssignmentOperator.MultiplyAssign::class,
            IrAssignmentExpression.IrAssignmentOperator.MultiplyAssign.serializer(),
        )
        subclass(
            IrAssignmentExpression.IrAssignmentOperator.DivideAssign::class,
            IrAssignmentExpression.IrAssignmentOperator.DivideAssign.serializer(),
        )
        subclass(
            IrAssignmentExpression.IrAssignmentOperator.ModuloAssign::class,
            IrAssignmentExpression.IrAssignmentOperator.ModuloAssign.serializer(),
        )
    }
}
