package com.voxeldev.mcodegen.dsl.ir.builders

import com.voxeldev.mcodegen.dsl.ir.IrAnnotation
import com.voxeldev.mcodegen.dsl.ir.IrBlockStatement
import com.voxeldev.mcodegen.dsl.ir.IrBreakStatement
import com.voxeldev.mcodegen.dsl.ir.IrContinueStatement
import com.voxeldev.mcodegen.dsl.ir.IrDoWhileStatement
import com.voxeldev.mcodegen.dsl.ir.IrEmptyStatement
import com.voxeldev.mcodegen.dsl.ir.IrExpression
import com.voxeldev.mcodegen.dsl.ir.IrExpressionStatement
import com.voxeldev.mcodegen.dsl.ir.IrForStatement
import com.voxeldev.mcodegen.dsl.ir.IrIfStatement
import com.voxeldev.mcodegen.dsl.ir.IrLocation
import com.voxeldev.mcodegen.dsl.ir.IrReturnStatement
import com.voxeldev.mcodegen.dsl.ir.IrStatement
import com.voxeldev.mcodegen.dsl.ir.IrStatementUnknown
import com.voxeldev.mcodegen.dsl.ir.IrStringRepresentation
import com.voxeldev.mcodegen.dsl.ir.IrSwitchStatement
import com.voxeldev.mcodegen.dsl.ir.IrThrowStatement
import com.voxeldev.mcodegen.dsl.ir.IrTryCatchStatement
import com.voxeldev.mcodegen.dsl.ir.IrType
import com.voxeldev.mcodegen.dsl.ir.IrVariableDeclarationStatement
import com.voxeldev.mcodegen.dsl.ir.IrWhileStatement

/**
 * Abstract builder class for creating [IrStatement] instances.
 * Provides common functionality for all statement builders.
 */
abstract class IrStatementBuilder : IrElementBuilder() {
    protected var stringRepresentation: MutableList<IrStringRepresentation> = mutableListOf()

    fun addStringRepresentation(representation: IrStringRepresentation) {
        stringRepresentation.add(representation)
    }

    protected fun buildStatementProperties(): IrStatementProperties {
        return IrStatementProperties(
            stringRepresentation = stringRepresentation,
            location = location,
            annotations = annotations,
            languageProperties = languageProperties,
        )
    }

    protected data class IrStatementProperties(
        val stringRepresentation: List<IrStringRepresentation>,
        val location: IrLocation?,
        val annotations: List<IrAnnotation>,
        val languageProperties: Map<String, Any>,
    )
}

/**
 * Creates a new [IrEmptyStatementBuilder] instance with the given expression.
 */
fun irEmptyStatement(): IrEmptyStatementBuilder =
    IrEmptyStatementBuilder()

/**
 * Builder class for creating [IrEmptyStatement] instances.
 */
class IrEmptyStatementBuilder() : IrStatementBuilder() {
    fun build(): IrEmptyStatement {
        val properties = buildStatementProperties()
        return IrEmptyStatement(
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}

/**
 * Creates a new [IrExpressionStatementBuilder] instance with the given expression.
 */
fun irExpressionStatement(expression: IrExpression): IrExpressionStatementBuilder =
    IrExpressionStatementBuilder(expression)

/**
 * Builder class for creating [IrExpressionStatement] instances.
 */
class IrExpressionStatementBuilder(private val expression: IrExpression) : IrStatementBuilder() {
    fun build(): IrExpressionStatement {
        val properties = buildStatementProperties()
        return IrExpressionStatement(
            expression = expression,
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}

/**
 * Creates a new [IrVariableDeclarationStatementBuilder] instance with the given name and type.
 */
fun irVariableDeclarationStatement(
    name: String,
    type: IrType,
): IrVariableDeclarationStatementBuilder = IrVariableDeclarationStatementBuilder(
    name = name,
    type = type,
)

/**
 * Builder class for creating [IrVariableDeclarationStatement] instances.
 */
class IrVariableDeclarationStatementBuilder(
    private val name: String,
    private val type: IrType,
) : IrStatementBuilder() {
    private var additionalNames: MutableList<String> = mutableListOf()
    private var initializer: IrStatement? = null

    fun addName(additionalName: String) {
        additionalNames.add(additionalName)
    }

    fun initializer(initializer: IrStatement) {
        this.initializer = initializer
    }

    fun build(): IrVariableDeclarationStatement {
        val properties = buildStatementProperties()
        return IrVariableDeclarationStatement(
            name = name,
            type = type,
            additionalNames = additionalNames,
            initializer = initializer,
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}

/**
 * Creates a new [IrBlockStatementBuilder] instance.
 */
fun irBlockStatement(): IrBlockStatementBuilder = IrBlockStatementBuilder()

/**
 * Builder class for creating [IrBlockStatement] instances.
 */
class IrBlockStatementBuilder : IrStatementBuilder() {
    private var statements: MutableList<IrStatement> = mutableListOf()

    fun addStatement(statement: IrStatement) {
        statements.add(statement)
    }

    fun build(): IrBlockStatement {
        val properties = buildStatementProperties()
        return IrBlockStatement(
            statements = statements,
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}

/**
 * Creates a new [IrIfStatementBuilder] instance with the given condition.
 */
fun irIfStatement(condition: IrExpression): IrIfStatementBuilder =
    IrIfStatementBuilder(condition)

/**
 * Builder class for creating [IrIfStatement] instances.
 */
class IrIfStatementBuilder(private val condition: IrExpression) : IrStatementBuilder() {
    private var thenStatement: IrStatement? = null
    private var elseStatement: IrStatement? = null

    fun thenStatement(statement: IrStatement) {
        thenStatement = statement
    }

    fun elseStatement(statement: IrStatement) {
        elseStatement = statement
    }

    fun build(): IrIfStatement {
        val properties = buildStatementProperties()
        return IrIfStatement(
            condition = condition,
            thenStatement = requireNotNull(thenStatement),
            elseStatement = elseStatement,
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}

/**
 * Creates a new [IrForStatementBuilder] instance.
 */
fun irForStatement(): IrForStatementBuilder = IrForStatementBuilder()

/**
 * Builder class for creating [IrForStatement] instances.
 */
class IrForStatementBuilder : IrStatementBuilder() {
    private var initializer: IrStatement? = null
    private var condition: IrExpression? = null
    private var update: IrStatement? = null
    private var body: IrStatement? = null

    fun initializer(initializer: IrStatement?) {
        this.initializer = initializer
    }

    fun condition(condition: IrExpression?) {
        this.condition = condition
    }

    fun update(update: IrStatement?) {
        this.update = update
    }

    fun body(statement: IrStatement) {
        this.body = statement
    }

    fun build(): IrForStatement {
        val properties = buildStatementProperties()
        return IrForStatement(
            initializer = initializer,
            condition = condition,
            update = update,
            body = requireNotNull(body),
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}

/**
 * Creates a new [IrWhileStatementBuilder] instance with the given condition.
 */
fun irWhileStatement(condition: IrExpression): IrWhileStatementBuilder =
    IrWhileStatementBuilder(condition)

/**
 * Builder class for creating [IrWhileStatement] instances.
 */
class IrWhileStatementBuilder(private val condition: IrExpression) : IrStatementBuilder() {
    private var body: IrStatement? = null

    fun body(statement: IrStatement) {
        body = statement
    }

    fun build(): IrWhileStatement {
        val properties = buildStatementProperties()
        return IrWhileStatement(
            condition = condition,
            body = requireNotNull(body),
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}

/**
 * Creates a new [IrDoWhileStatementBuilder] instance with the given condition.
 */
fun irDoWhileStatement(condition: IrExpression): IrDoWhileStatementBuilder =
    IrDoWhileStatementBuilder(condition)

/**
 * Builder class for creating [IrDoWhileStatement] instances.
 */
class IrDoWhileStatementBuilder(private val condition: IrExpression) : IrStatementBuilder() {
    private var body: IrStatement? = null

    fun body(statement: IrStatement) {
        body = statement
    }

    fun build(): IrDoWhileStatement {
        val properties = buildStatementProperties()
        return IrDoWhileStatement(
            body = requireNotNull(body),
            condition = condition,
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}

/**
 * Creates a new [IrSwitchStatementBuilder] instance with the given expression.
 */
fun irSwitchStatement(expression: IrExpression): IrSwitchStatementBuilder =
    IrSwitchStatementBuilder(expression)

/**
 * Builder class for creating [IrSwitchStatement] instances.
 */
class IrSwitchStatementBuilder(private val expression: IrExpression) : IrStatementBuilder() {
    private var cases: MutableList<IrSwitchStatement.IrSwitchStatementCase> = mutableListOf()

    fun addCase(case: IrSwitchStatement.IrSwitchStatementCase) {
        cases.add(case)
    }

    fun build(): IrSwitchStatement {
        val properties = buildStatementProperties()
        return IrSwitchStatement(
            expression = expression,
            cases = cases,
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}

/**
 * Creates a new [IrSwitchStatementCaseBuilder] instance.
 */
fun irSwitchStatementCase(): IrSwitchStatementCaseBuilder = IrSwitchStatementCaseBuilder()

/**
 * Builder class for creating [IrSwitchStatement.IrSwitchStatementCase] instances.
 */
class IrSwitchStatementCaseBuilder : IrStatementBuilder() {
    private var matchExpressions: MutableList<IrExpression> = mutableListOf()
    private var body: IrStatement? = null

    fun addMatchExpression(matchExpression: IrExpression) {
        matchExpressions.add(matchExpression)
    }

    fun body(statement: IrStatement) {
        body = statement
    }

    fun build(): IrSwitchStatement.IrSwitchStatementCase {
        val properties = buildStatementProperties()
        return IrSwitchStatement.IrSwitchStatementCase(
            matchExpressions = matchExpressions,
            body = body,
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}

/**
 * Creates a new [IrReturnStatementBuilder] instance.
 */
fun irReturnStatement(): IrReturnStatementBuilder = IrReturnStatementBuilder()

/**
 * Builder class for creating [IrReturnStatement] instances.
 */
class IrReturnStatementBuilder : IrStatementBuilder() {
    private var expression: IrExpression? = null

    fun expression(expression: IrExpression?) {
        this.expression = expression
    }

    fun build(): IrReturnStatement {
        val properties = buildStatementProperties()
        return IrReturnStatement(
            expression = expression,
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}

/**
 * Creates a new [IrBreakStatementBuilder] instance.
 */
fun irBreakStatement(): IrBreakStatementBuilder = IrBreakStatementBuilder()

/**
 * Builder class for creating [IrBreakStatement] instances.
 */
class IrBreakStatementBuilder : IrStatementBuilder() {
    fun build(): IrBreakStatement {
        val properties = buildStatementProperties()
        return IrBreakStatement(
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}

/**
 * Creates a new [IrContinueStatementBuilder] instance.
 */
fun irContinueStatement(): IrContinueStatementBuilder = IrContinueStatementBuilder()

/**
 * Builder class for creating [IrContinueStatement] instances.
 */
class IrContinueStatementBuilder : IrStatementBuilder() {
    fun build(): IrContinueStatement {
        val properties = buildStatementProperties()
        return IrContinueStatement(
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}

/**
 * Creates a new [IrThrowStatementBuilder] instance with the given expression.
 */
fun irThrowStatement(expression: IrExpression): IrThrowStatementBuilder =
    IrThrowStatementBuilder(expression)

/**
 * Builder class for creating [IrThrowStatement] instances.
 */
class IrThrowStatementBuilder(private val expression: IrExpression) : IrStatementBuilder() {
    fun build(): IrThrowStatement {
        val properties = buildStatementProperties()
        return IrThrowStatement(
            expression = expression,
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}

/**
 * Creates a new [IrTryCatchStatementBuilder] instance.
 */
fun irTryCatchStatement(tryBlock: IrStatement): IrTryCatchStatementBuilder =
    IrTryCatchStatementBuilder(tryBlock)

/**
 * Builder class for creating [IrTryCatchStatement] instances.
 */
class IrTryCatchStatementBuilder(
    private val tryBlock: IrStatement,
) : IrStatementBuilder() {
    private var catchClauses: MutableList<IrTryCatchStatement.IrTryCatchStatementClause> = mutableListOf()
    private var finallyBlock: IrStatement? = null

    fun addCatchClause(clause: IrTryCatchStatement.IrTryCatchStatementClause) {
        catchClauses.add(clause)
    }

    fun finallyBlock(statement: IrStatement) {
        finallyBlock = statement
    }

    fun build(): IrTryCatchStatement {
        val properties = buildStatementProperties()
        return IrTryCatchStatement(
            tryBlock = tryBlock,
            catchClauses = catchClauses,
            finallyBlock = finallyBlock,
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}

/**
 * Creates a new [IrTryCatchStatementClauseBuilder] instance with the given exception type.
 */
fun irTryCatchStatementClause(exceptionType: IrType): IrTryCatchStatementClauseBuilder =
    IrTryCatchStatementClauseBuilder(exceptionType)

/**
 * Builder class for creating [IrTryCatchStatement.IrTryCatchStatementClause] instances.
 */
class IrTryCatchStatementClauseBuilder(private val exceptionType: IrType) : IrStatementBuilder() {
    private var exceptionName: String? = null
    private var body: IrStatement? = null

    fun exceptionName(exceptionName: String?) {
        this.exceptionName = exceptionName
    }

    fun body(statement: IrStatement) {
        body = statement
    }

    fun build(): IrTryCatchStatement.IrTryCatchStatementClause {
        val properties = buildStatementProperties()
        return IrTryCatchStatement.IrTryCatchStatementClause(
            exceptionType = exceptionType,
            exceptionName = exceptionName,
            body = body,
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}

/**
 * Creates a new [IrStatementUnknownBuilder] instance.
 */
fun irStatementUnknown(): IrStatementUnknownBuilder = IrStatementUnknownBuilder()

/**
 * Builder class for creating [IrStatementUnknown] instances.
 */
class IrStatementUnknownBuilder : IrStatementBuilder() {
    fun build(): IrStatementUnknown {
        val properties = buildStatementProperties()
        return IrStatementUnknown(
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}