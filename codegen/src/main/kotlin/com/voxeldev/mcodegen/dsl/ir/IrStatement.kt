package com.voxeldev.mcodegen.dsl.ir

/**
 * Base interface for all IR statements.
 */
open class IrStatement(
    open val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation?,
    override val annotations: List<IrAnnotation>,
    override val languageProperties: Map<String, Any>,
) : IrElement

/**
 * A statement consisting solely of an expression, e.g. `myFunctionCall();`
 */
data class IrExpressionStatement(
    val expression: IrExpression,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation?,
    override val annotations: List<IrAnnotation>,
    override val languageProperties: Map<String, Any>,
) : IrStatement(
    stringRepresentation = stringRepresentation,
    location = location,
    annotations = annotations,
    languageProperties = languageProperties,
)

/**
 * A variable declaration, e.g. `int x = 10;` or `val x = 10`.
 */
data class IrVariableDeclarationStatement(
    val name: String,
    val type: IrType,
    val additionalNames: List<String>,
    val initializer: IrExpression?,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation?,
    override val annotations: List<IrAnnotation>,
    override val languageProperties: Map<String, Any>,
) : IrStatement(
    stringRepresentation = stringRepresentation,
    location = location,
    annotations = annotations,
    languageProperties = languageProperties,
)

/**
 * A block of statements, e.g. `{ ... }`.
 */
data class IrBlockStatement(
    val statements: List<IrStatement>,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation?,
    override val annotations: List<IrAnnotation>,
    override val languageProperties: Map<String, Any>,
) : IrStatement(
    stringRepresentation = stringRepresentation,
    location = location,
    annotations = annotations,
    languageProperties = languageProperties,
)

/**
 * If/else statement, e.g.
 * `if (condition) { ... } else { ... }`
 */
data class IrIfStatement(
    val condition: IrExpression,
    val thenStatement: IrStatement,
    val elseStatement: IrStatement? = null,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation?,
    override val annotations: List<IrAnnotation>,
    override val languageProperties: Map<String, Any>,
) : IrStatement(
    stringRepresentation = stringRepresentation,
    location = location,
    annotations = annotations,
    languageProperties = languageProperties,
)

/**
 * For loop, e.g. `for (int i = 0; i < 10; i++) { ... }`.
 */
data class IrForStatement(
    val initializer: IrStatement?, // Often a VariableDeclarationStatement
    val condition: IrExpression?,
    val update: IrStatement?, // e.g. i++
    val body: IrStatement,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation?,
    override val annotations: List<IrAnnotation>,
    override val languageProperties: Map<String, Any>,
) : IrStatement(
    stringRepresentation = stringRepresentation,
    location = location,
    annotations = annotations,
    languageProperties = languageProperties,
)

/**
 * While loop, e.g. `while (condition) { ... }`
 */
data class IrWhileStatement(
    val condition: IrExpression,
    val body: IrStatement,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation?,
    override val annotations: List<IrAnnotation>,
    override val languageProperties: Map<String, Any>,
) : IrStatement(
    stringRepresentation = stringRepresentation,
    location = location,
    annotations = annotations,
    languageProperties = languageProperties,
)

/**
 * Do/while loop, e.g. `do { ... } while (condition)`
 */
data class IrDoWhileStatement(
    val body: IrStatement,
    val condition: IrExpression,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation?,
    override val annotations: List<IrAnnotation>,
    override val languageProperties: Map<String, Any>,
) : IrStatement(
    stringRepresentation = stringRepresentation,
    location = location,
    annotations = annotations,
    languageProperties = languageProperties,
)

/**
 * Switch/when statement: language details vary. Typically a set of cases
 * that match an expression. Example usage: Java's `switch(expr) { case X: ... }`
 * or Kotlin's `when(expr) { X -> ... }`.
 */
data class IrSwitchStatement(
    val expression: IrExpression,
    val cases: List<IrSwitchStatementCase>,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation?,
    override val annotations: List<IrAnnotation>,
    override val languageProperties: Map<String, Any>,
) : IrStatement(
    stringRepresentation = stringRepresentation,
    location = location,
    annotations = annotations,
    languageProperties = languageProperties,
) {
    /**
     * A single case in a switch/when statement.
     */
    data class IrSwitchStatementCase(
        val matchExpression: IrExpression?,
        val body: IrStatement?,
        override val stringRepresentation: List<IrStringRepresentation>,
        override val location: IrLocation?,
        override val annotations: List<IrAnnotation>,
        override val languageProperties: Map<String, Any>,
    ) : IrStatement(
        stringRepresentation = stringRepresentation,
        location = location,
        annotations = annotations,
        languageProperties = languageProperties,
    )
}

/**
 * Return statement, e.g. `return expr;`.
 */
data class IrReturnStatement(
    val expression: IrExpression?,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation?,
    override val annotations: List<IrAnnotation>,
    override val languageProperties: Map<String, Any>,
) : IrStatement(
    stringRepresentation = stringRepresentation,
    location = location,
    annotations = annotations,
    languageProperties = languageProperties,
)

/**
 * Break statement, e.g. `break;`
 */
data class IrBreakStatement(
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation?,
    override val annotations: List<IrAnnotation>,
    override val languageProperties: Map<String, Any>,
) : IrStatement(
    stringRepresentation = stringRepresentation,
    location = location,
    annotations = annotations,
    languageProperties = languageProperties,
)

/**
 * Continue statement, e.g. `continue;`
 */
data class IrContinueStatement(
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation?,
    override val annotations: List<IrAnnotation>,
    override val languageProperties: Map<String, Any>,
) : IrStatement(
    stringRepresentation = stringRepresentation,
    location = location,
    annotations = annotations,
    languageProperties = languageProperties,
)

/**
 * Throw statement, e.g. `throw new MyException("Error");`
 */
data class IrThrowStatement(
    val expression: IrExpression,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation?,
    override val annotations: List<IrAnnotation>,
    override val languageProperties: Map<String, Any>,
) : IrStatement(
    stringRepresentation = stringRepresentation,
    location = location,
    annotations = annotations,
    languageProperties = languageProperties,
)

/**
 * Try/Catch/Finally statement, e.g.
 * ```
 * try {
 *   ...
 * } catch (e: Exception) {
 *   ...
 * } finally {
 *   ...
 * }
 * ```
 */
data class IrTryCatchStatement(
    val tryBlock: IrStatement,
    val catchClauses: List<IrTryCatchStatementClause>,
    val finallyBlock: IrStatement?,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation?,
    override val annotations: List<IrAnnotation>,
    override val languageProperties: Map<String, Any>,
) : IrStatement(
    stringRepresentation = stringRepresentation,
    location = location,
    annotations = annotations,
    languageProperties = languageProperties,
) {
    /**
     * Represents a single catch clause (exception type + name + body).
     */
    data class IrTryCatchStatementClause(
        val exceptionType: IrType,
        val exceptionName: String?,
        val body: IrStatement?,
        override val stringRepresentation: List<IrStringRepresentation>,
        override val location: IrLocation?,
        override val annotations: List<IrAnnotation>,
        override val languageProperties: Map<String, Any>,
    ) : IrStatement(
        stringRepresentation = stringRepresentation,
        location = location,
        annotations = annotations,
        languageProperties = languageProperties,
    )
}
