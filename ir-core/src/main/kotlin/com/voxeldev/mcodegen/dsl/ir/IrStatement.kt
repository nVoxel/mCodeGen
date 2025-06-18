package com.voxeldev.mcodegen.dsl.ir

import com.voxeldev.mcodegen.dsl.ir.utils.MapStringAnySerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Base interface for all IR statements.
 */
@Serializable
abstract class IrStatement: IrElement {
    abstract val stringRepresentation: List<IrStringRepresentation>
    abstract override val location: IrLocation?
    abstract override val annotations: List<IrAnnotation>
    @Serializable(with = MapStringAnySerializer::class)
    abstract override val languageProperties: Map<String, Any>
}

/**
 * Represents an empty expression, nothing. Can be used as a fallback.
 */
@Serializable
data class IrEmptyStatement(
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation>,
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any>,
) : IrStatement()

/**
 * A statement consisting solely of an expression, e.g. `myFunctionCall();`
 */
@Serializable
data class IrExpressionStatement(
    val expression: IrExpression,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation>,
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any>,
) : IrStatement()

/**
 * A variable declaration, e.g. `int x = 10;` or `val x = 10`.
 */
@Serializable
data class IrVariableDeclarationStatement(
    val name: String,
    @SerialName("irType")
    val type: IrType,
    val additionalNames: List<String>,
    val isMutable: Boolean,
    val initializer: IrStatement? = null,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation>,
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any>,
) : IrStatement()

/**
 * A block of statements, e.g. `{ ... }`.
 */
@Serializable
data class IrBlockStatement(
    val statements: List<IrStatement>,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation>,
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any>,
) : IrStatement()

/**
 * If/else statement, e.g.
 * `if (condition) { ... } else { ... }`
 */
@Serializable
data class IrIfStatement(
    val condition: IrExpression,
    val thenStatement: IrStatement,
    val elseStatement: IrStatement? = null,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation>,
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any>,
) : IrStatement()

/**
 * For loop, e.g. `for (int i = 0; i < 10; i++) { ... }`.
 */
@Serializable
data class IrForStatement(
    val initializer: IrStatement? = null, // Often a VariableDeclarationStatement
    val condition: IrExpression? = null,
    val update: IrStatement? = null, // e.g. i++
    val body: IrStatement,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation>,
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any>,
) : IrStatement()

/**
 * While loop, e.g. `while (condition) { ... }`
 */
@Serializable
data class IrWhileStatement(
    val condition: IrExpression,
    val body: IrStatement,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation>,
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any>,
) : IrStatement()

/**
 * Do/while loop, e.g. `do { ... } while (condition)`
 */
@Serializable
data class IrDoWhileStatement(
    val body: IrStatement,
    val condition: IrExpression,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation>,
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any>,
) : IrStatement()

/**
 * Switch/when statement: language details vary. Typically a set of cases
 * that match an expression. Example usage: Java's `switch(expr) { case X: ... }`
 * or Kotlin's `when(expr) { X -> ... }`.
 */
@Serializable
data class IrSwitchStatement(
    val expression: IrExpression,
    val cases: List<IrSwitchStatementCase>,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation>,
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any>,
) : IrStatement() {
    /**
     * A single case in a switch/when statement.
     */
    @Serializable
    data class IrSwitchStatementCase(
        val matchExpressions: List<IrExpression>,
        val body: IrStatement? = null,
        override val stringRepresentation: List<IrStringRepresentation>,
        override val location: IrLocation? = null,
        override val annotations: List<IrAnnotation>,
        @Serializable(with = MapStringAnySerializer::class)
        override val languageProperties: Map<String, Any>,
    ) : IrStatement()
}

/**
 * Return statement, e.g. `return expr;`.
 */
@Serializable
data class IrReturnStatement(
    val expression: IrExpression? = null,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation>,
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any>,
) : IrStatement()

/**
 * Break statement, e.g. `break;`
 */
@Serializable
data class IrBreakStatement(
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation>,
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any>,
) : IrStatement()

/**
 * Continue statement, e.g. `continue;`
 */
@Serializable
data class IrContinueStatement(
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation>,
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any>,
) : IrStatement()

/**
 * Throw statement, e.g. `throw new MyException("Error");`
 */
@Serializable
data class IrThrowStatement(
    val expression: IrExpression,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation>,
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any>,
) : IrStatement()

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
@Serializable
data class IrTryCatchStatement(
    val tryBlock: IrStatement,
    val catchClauses: List<IrTryCatchStatementClause>,
    val finallyBlock: IrStatement? = null,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation>,
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any>,
) : IrStatement() {
    /**
     * Represents a single catch clause (exception type + name + body).
     */
    @Serializable
    data class IrTryCatchStatementClause(
        val exceptionType: IrType,
        val exceptionName: String? = null,
        val body: IrStatement? = null,
        override val stringRepresentation: List<IrStringRepresentation>,
        override val location: IrLocation? = null,
        override val annotations: List<IrAnnotation>,
        @Serializable(with = MapStringAnySerializer::class)
        override val languageProperties: Map<String, Any>,
    ) : IrStatement()
}

/**
 * Fallback when the frontend was unable to convert a statement.
 */
@Serializable
data class IrStatementUnknown(
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation>,
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any>,
) : IrStatement()
