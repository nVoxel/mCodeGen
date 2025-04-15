package com.voxeldev.mcodegen.dsl.ir.builders

import com.voxeldev.mcodegen.dsl.ir.*

/**
 * Abstract builder class for creating [IrStatement] instances.
 * Provides common functionality for all statement builders.
 */
abstract class IrStatementBuilder : IrElementBuilder() {
    protected var stringRepresentation: MutableList<IrStringRepresentation> = mutableListOf()

    fun addStringRepresentation(representation: IrStringRepresentation) = apply {
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
    private var initializer: IrExpression? = null

    fun initializer(initializer: IrExpression?) = apply { this.initializer = initializer }

    fun build(): IrVariableDeclarationStatement {
        val properties = buildStatementProperties()
        return IrVariableDeclarationStatement(
            name = name,
            type = type,
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

    fun addStatement(statement: IrStatement) = apply { statements.add(statement) }

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
    private var thenBlock: MutableList<IrStatement> = mutableListOf()
    private var elseBlock: MutableList<IrStatement>? = null

    fun addThenStatement(statement: IrStatement) = apply { thenBlock.add(statement) }
    fun addElseStatement(statement: IrStatement) = apply {
        if (elseBlock == null) elseBlock = mutableListOf()
        elseBlock?.add(statement)
    }

    fun build(): IrIfStatement {
        val properties = buildStatementProperties()
        return IrIfStatement(
            condition = condition,
            thenBlock = thenBlock,
            elseBlock = elseBlock,
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
    private var update: IrExpression? = null
    private var body: MutableList<IrStatement> = mutableListOf()

    fun initializer(initializer: IrStatement?) = apply { this.initializer = initializer }
    fun condition(condition: IrExpression?) = apply { this.condition = condition }
    fun update(update: IrExpression?) = apply { this.update = update }
    fun addStatement(statement: IrStatement) = apply { body.add(statement) }

    fun build(): IrForStatement {
        val properties = buildStatementProperties()
        return IrForStatement(
            initializer = initializer,
            condition = condition,
            update = update,
            body = body,
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
    private var body: MutableList<IrStatement> = mutableListOf()

    fun addStatement(statement: IrStatement) = apply { body.add(statement) }

    fun build(): IrWhileStatement {
        val properties = buildStatementProperties()
        return IrWhileStatement(
            condition = condition,
            body = body,
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
    private var body: MutableList<IrStatement> = mutableListOf()

    fun addStatement(statement: IrStatement) = apply { body.add(statement) }

    fun build(): IrDoWhileStatement {
        val properties = buildStatementProperties()
        return IrDoWhileStatement(
            body = body,
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
    private var defaultBody: MutableList<IrStatement>? = null

    fun addCase(case: IrSwitchStatement.IrSwitchStatementCase) = apply { cases.add(case) }
    fun addDefaultStatement(statement: IrStatement) = apply {
        if (defaultBody == null) defaultBody = mutableListOf()
        defaultBody?.add(statement)
    }

    fun build(): IrSwitchStatement {
        val properties = buildStatementProperties()
        return IrSwitchStatement(
            expression = expression,
            cases = cases,
            defaultBody = defaultBody,
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
    private var matchExpression: IrExpression? = null
    private var body: MutableList<IrStatement> = mutableListOf()

    fun matchExpression(matchExpression: IrExpression?) = apply { this.matchExpression = matchExpression }
    fun addStatement(statement: IrStatement) = apply { body.add(statement) }

    fun build(): IrSwitchStatement.IrSwitchStatementCase {
        val properties = buildStatementProperties()
        return IrSwitchStatement.IrSwitchStatementCase(
            matchExpression = matchExpression,
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

    fun expression(expression: IrExpression?) = apply { this.expression = expression }

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
fun irTryCatchStatement(): IrTryCatchStatementBuilder = IrTryCatchStatementBuilder()

/**
 * Builder class for creating [IrTryCatchStatement] instances.
 */
class IrTryCatchStatementBuilder : IrStatementBuilder() {
    private var tryBlock: MutableList<IrStatement> = mutableListOf()
    private var catchClauses: MutableList<IrTryCatchStatement.IrTryCatchStatementClause> = mutableListOf()
    private var finallyBlock: MutableList<IrStatement>? = null

    fun addTryStatement(statement: IrStatement) = apply { tryBlock.add(statement) }
    fun addCatchClause(clause: IrTryCatchStatement.IrTryCatchStatementClause) = apply { catchClauses.add(clause) }
    fun addFinallyStatement(statement: IrStatement) = apply {
        if (finallyBlock == null) finallyBlock = mutableListOf()
        finallyBlock?.add(statement)
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
fun irTryCatchStatementClause(exceptionType: String): IrTryCatchStatementClauseBuilder =
    IrTryCatchStatementClauseBuilder(exceptionType)

/**
 * Builder class for creating [IrTryCatchStatement.IrTryCatchStatementClause] instances.
 */
class IrTryCatchStatementClauseBuilder(private val exceptionType: String) : IrStatementBuilder() {
    private var exceptionName: String? = null
    private var body: MutableList<IrStatement> = mutableListOf()

    fun exceptionName(exceptionName: String?) = apply { this.exceptionName = exceptionName }
    fun addStatement(statement: IrStatement) = apply { body.add(statement) }

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