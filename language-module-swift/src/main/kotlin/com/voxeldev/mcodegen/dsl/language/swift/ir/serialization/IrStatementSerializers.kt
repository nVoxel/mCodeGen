package com.voxeldev.mcodegen.dsl.language.swift.ir.serialization

import com.voxeldev.mcodegen.dsl.ir.IrBlockStatement
import com.voxeldev.mcodegen.dsl.ir.IrBreakStatement
import com.voxeldev.mcodegen.dsl.ir.IrContinueStatement
import com.voxeldev.mcodegen.dsl.ir.IrDoWhileStatement
import com.voxeldev.mcodegen.dsl.ir.IrEmptyStatement
import com.voxeldev.mcodegen.dsl.ir.IrExpressionStatement
import com.voxeldev.mcodegen.dsl.ir.IrForStatement
import com.voxeldev.mcodegen.dsl.ir.IrIfStatement
import com.voxeldev.mcodegen.dsl.ir.IrReturnStatement
import com.voxeldev.mcodegen.dsl.ir.IrStatement
import com.voxeldev.mcodegen.dsl.ir.IrStatementUnknown
import com.voxeldev.mcodegen.dsl.ir.IrSwitchStatement
import com.voxeldev.mcodegen.dsl.ir.IrThrowStatement
import com.voxeldev.mcodegen.dsl.ir.IrTryCatchStatement
import com.voxeldev.mcodegen.dsl.ir.IrVariableDeclarationStatement
import com.voxeldev.mcodegen.dsl.ir.IrWhileStatement
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

// only IrStatement impls which can be received from Swift code
val irStatementSerializers = SerializersModule {
    polymorphic(IrStatement::class) {
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
    }
}