package com.voxeldev.mcodegen.dsl.language.swift.ir.serialization

import com.voxeldev.mcodegen.dsl.ir.IrAssignmentExpression
import com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression
import com.voxeldev.mcodegen.dsl.ir.IrBlockStatement
import com.voxeldev.mcodegen.dsl.ir.IrBreakStatement
import com.voxeldev.mcodegen.dsl.ir.IrCastExpression
import com.voxeldev.mcodegen.dsl.ir.IrClass
import com.voxeldev.mcodegen.dsl.ir.IrClassInitializer
import com.voxeldev.mcodegen.dsl.ir.IrConstructor
import com.voxeldev.mcodegen.dsl.ir.IrContinueStatement
import com.voxeldev.mcodegen.dsl.ir.IrDoWhileStatement
import com.voxeldev.mcodegen.dsl.ir.IrElement
import com.voxeldev.mcodegen.dsl.ir.IrEmptyExpression
import com.voxeldev.mcodegen.dsl.ir.IrEmptyStatement
import com.voxeldev.mcodegen.dsl.ir.IrExpressionStatement
import com.voxeldev.mcodegen.dsl.ir.IrExpressionUnknown
import com.voxeldev.mcodegen.dsl.ir.IrField
import com.voxeldev.mcodegen.dsl.ir.IrForStatement
import com.voxeldev.mcodegen.dsl.ir.IrIdentifierExpression
import com.voxeldev.mcodegen.dsl.ir.IrIfStatement
import com.voxeldev.mcodegen.dsl.ir.IrImport
import com.voxeldev.mcodegen.dsl.ir.IrLiteralExpression
import com.voxeldev.mcodegen.dsl.ir.IrMethod
import com.voxeldev.mcodegen.dsl.ir.IrMethodBody
import com.voxeldev.mcodegen.dsl.ir.IrMethodCallExpression
import com.voxeldev.mcodegen.dsl.ir.IrObjectCreationExpression
import com.voxeldev.mcodegen.dsl.ir.IrParameter
import com.voxeldev.mcodegen.dsl.ir.IrPropertyAccessExpression
import com.voxeldev.mcodegen.dsl.ir.IrReturnStatement
import com.voxeldev.mcodegen.dsl.ir.IrStatementUnknown
import com.voxeldev.mcodegen.dsl.ir.IrSuperClass
import com.voxeldev.mcodegen.dsl.ir.IrSwitchStatement
import com.voxeldev.mcodegen.dsl.ir.IrSwitchStatement.IrSwitchStatementCase
import com.voxeldev.mcodegen.dsl.ir.IrTernaryExpression
import com.voxeldev.mcodegen.dsl.ir.IrThrowStatement
import com.voxeldev.mcodegen.dsl.ir.IrTryCatchStatement
import com.voxeldev.mcodegen.dsl.ir.IrTryCatchStatement.IrTryCatchStatementClause
import com.voxeldev.mcodegen.dsl.ir.IrTypeArray
import com.voxeldev.mcodegen.dsl.ir.IrTypeCheckExpression
import com.voxeldev.mcodegen.dsl.ir.IrTypeFunction
import com.voxeldev.mcodegen.dsl.ir.IrTypeGeneric
import com.voxeldev.mcodegen.dsl.ir.IrTypeParameter
import com.voxeldev.mcodegen.dsl.ir.IrTypePrimitive
import com.voxeldev.mcodegen.dsl.ir.IrTypeReference
import com.voxeldev.mcodegen.dsl.ir.IrTypeReferenceIdentifierExpression
import com.voxeldev.mcodegen.dsl.ir.IrUnaryExpression
import com.voxeldev.mcodegen.dsl.ir.IrVariableDeclarationStatement
import com.voxeldev.mcodegen.dsl.ir.IrWhileStatement
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

// only IrElement impls which can be received from Swift code
val irElementSerializers = SerializersModule {
    polymorphic(IrElement::class) {
        subclass(
            IrClass::class,
            IrClass.serializer(),
        )
        subclass(
            IrSuperClass::class,
            IrSuperClass.serializer(),
        )
        subclass(
            IrField::class,
            IrField.serializer(),
        )
        subclass(
            IrTypeParameter::class,
            IrTypeParameter.serializer(),
        )
        subclass(
            IrClassInitializer::class,
            IrClassInitializer.serializer(),
        )
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
        subclass(
            IrImport::class,
            IrImport.serializer(),
        )
        subclass(
            IrMethod::class,
            IrMethod.serializer(),
        )
        subclass(
            IrConstructor::class,
            IrConstructor.serializer(),
        )
        subclass(
            IrParameter::class,
            IrParameter.serializer(),
        )
        subclass(
            IrMethodBody::class,
            IrMethodBody.serializer(),
        )
        subclass(
            IrSwitchStatementCase::class,
            IrSwitchStatementCase.serializer(),
        )
        subclass(
            IrTryCatchStatementClause::class,
            IrTryCatchStatementClause.serializer(),
        )
        subclass(
            IrEmptyStatement::class,
            IrEmptyStatement.serializer(),
        )
        subclass(
            IrExpressionStatement::class,
            IrExpressionStatement.serializer(),
        )
        subclass(
            IrVariableDeclarationStatement::class,
            IrVariableDeclarationStatement.serializer(),
        )
        subclass(
            IrBlockStatement::class,
            IrBlockStatement.serializer(),
        )
        subclass(
            IrIfStatement::class,
            IrIfStatement.serializer(),
        )
        subclass(
            IrForStatement::class,
            IrForStatement.serializer(),
        )
        subclass(
            IrWhileStatement::class,
            IrWhileStatement.serializer(),
        )
        subclass(
            IrDoWhileStatement::class,
            IrDoWhileStatement.serializer(),
        )
        subclass(
            IrSwitchStatement::class,
            IrSwitchStatement.serializer(),
        )
        subclass(
            IrReturnStatement::class,
            IrReturnStatement.serializer(),
        )
        subclass(
            IrBreakStatement::class,
            IrBreakStatement.serializer(),
        )
        subclass(
            IrContinueStatement::class,
            IrContinueStatement.serializer(),
        )
        subclass(
            IrThrowStatement::class,
            IrThrowStatement.serializer(),
        )
        subclass(
            IrTryCatchStatement::class,
            IrTryCatchStatement.serializer(),
        )
        subclass(
            IrStatementUnknown::class,
            IrStatementUnknown.serializer(),
        )
        subclass(
            IrTypeReference::class,
            IrTypeReference.serializer(),
        )
        subclass(
            IrTypePrimitive::class,
            IrTypePrimitive.serializer(),
        )
        subclass(
            IrTypeFunction::class,
            IrTypeFunction.serializer(),
        )
        subclass(
            IrTypeArray::class,
            IrTypeArray.serializer(),
        )
        subclass(
            IrTypeGeneric::class,
            IrTypeGeneric.serializer(),
        )
    }
}