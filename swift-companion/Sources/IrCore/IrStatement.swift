//
//  IrStatement.swift
//  swift-companion
//
//  Created by nVoxel on 18.06.2025.
//

import Foundation
import AnyCodable

public protocol IrStatementProtocol: IrElementProtocol {
    var type: String? { get }
    var stringRepresentation: [IrStringRepresentation] { get }
}

/**
 * Base interface for all IR statements.
 */
public indirect enum IrStatement: IrStatementProtocol {
    case EmptyStatement(IrEmptyStatement)
    case ExpressionStatement(IrExpressionStatement)
    case VariableDeclarationStatement(IrVariableDeclarationStatement)
    case BlockStatement(IrBlockStatement)
    case IfStatement(IrIfStatement)
    case ForStatement(IrForStatement)
    case WhileStatement(IrWhileStatement)
    case DoWhileStatement(IrDoWhileStatement)
    case SwitchStatement(IrSwitchStatement)
    case SwitchStatementCase(IrSwitchStatement.IrSwitchStatementCase)
    case ReturnStatement(IrReturnStatement)
    case BreakStatement(IrBreakStatement)
    case ContinueStatement(IrContinueStatement)
    case ThrowStatement(IrThrowStatement)
    case TryCatchStatement(IrTryCatchStatement)
    case TryCatchStatementClause(IrTryCatchStatement.IrTryCatchStatementClause)
    case StatementUnknown(IrStatementUnknown)
    
    private enum CodingKeys: String, CodingKey {
        case type
    }
    
    public init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        let type = try container.decode(String.self, forKey: .type)
        
        switch type {
        case "com.voxeldev.mcodegen.dsl.ir.IrEmptyStatement": self = .EmptyStatement(try IrEmptyStatement(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrExpressionStatement": self = .ExpressionStatement(try IrExpressionStatement(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrVariableDeclarationStatement": self = .VariableDeclarationStatement(try IrVariableDeclarationStatement(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrBlockStatement": self = .BlockStatement(try IrBlockStatement(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrIfStatement": self = .IfStatement(try IrIfStatement(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrForStatement": self = .ForStatement(try IrForStatement(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrWhileStatement": self = .WhileStatement(try IrWhileStatement(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrDoWhileStatement": self = .DoWhileStatement(try IrDoWhileStatement(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrSwitchStatement": self = .SwitchStatement(try IrSwitchStatement(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrSwitchStatementCase": self = .SwitchStatementCase(try IrSwitchStatement.IrSwitchStatementCase(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrReturnStatement": self = .ReturnStatement(try IrReturnStatement(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrBreakStatement": self = .BreakStatement(try IrBreakStatement(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrContinueStatement": self = .ContinueStatement(try IrContinueStatement(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrThrowStatement": self = .ThrowStatement(try IrThrowStatement(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrTryCatchStatement": self = .TryCatchStatement(try IrTryCatchStatement(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrTryCatchStatementClause": self = .TryCatchStatementClause(try IrTryCatchStatement.IrTryCatchStatementClause(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrStatementUnknown": self = .StatementUnknown(try IrStatementUnknown(from: decoder))
        default:
            throw DecodingError.dataCorruptedError(
                forKey: .type,
                in: container,
                debugDescription: "Unknown IrStatement kind: \(type)"
            )
        }
    }
    
    public func encode(to encoder: any Encoder) throws {
        switch self {
        case .EmptyStatement(let e): try e.encode(to: encoder)
        case .ExpressionStatement(let e): try e.encode(to: encoder)
        case .VariableDeclarationStatement(let e): try e.encode(to: encoder)
        case .BlockStatement(let e): try e.encode(to: encoder)
        case .IfStatement(let e): try e.encode(to: encoder)
        case .ForStatement(let e): try e.encode(to: encoder)
        case .WhileStatement(let e): try e.encode(to: encoder)
        case .DoWhileStatement(let e): try e.encode(to: encoder)
        case .SwitchStatement(let e): try e.encode(to: encoder)
        case .SwitchStatementCase(let e): try e.encode(to: encoder)
        case .ReturnStatement(let e): try e.encode(to: encoder)
        case .BreakStatement(let e): try e.encode(to: encoder)
        case .ContinueStatement(let e): try e.encode(to: encoder)
        case .ThrowStatement(let e): try e.encode(to: encoder)
        case .TryCatchStatement(let e): try e.encode(to: encoder)
        case .TryCatchStatementClause(let e): try e.encode(to: encoder)
        case .StatementUnknown(let e): try e.encode(to: encoder)
        }
    }
    
    public var type: String? {
        switch self {
        case .EmptyStatement(let e): return e.type
        case .ExpressionStatement(let e): return e.type
        case .VariableDeclarationStatement(let e): return e.type
        case .BlockStatement(let e): return e.type
        case .IfStatement(let e): return e.type
        case .ForStatement(let e): return e.type
        case .WhileStatement(let e): return e.type
        case .DoWhileStatement(let e): return e.type
        case .SwitchStatement(let e): return e.type
        case .SwitchStatementCase(let e): return e.type
        case .ReturnStatement(let e): return e.type
        case .BreakStatement(let e): return e.type
        case .ContinueStatement(let e): return e.type
        case .ThrowStatement(let e): return e.type
        case .TryCatchStatement(let e): return e.type
        case .TryCatchStatementClause(let e): return e.type
        case .StatementUnknown(let e): return e.type
        }
    }
    
    public var stringRepresentation: [IrStringRepresentation] {
        switch self {
        case .EmptyStatement(let e): return e.stringRepresentation
        case .ExpressionStatement(let e): return e.stringRepresentation
        case .VariableDeclarationStatement(let e): return e.stringRepresentation
        case .BlockStatement(let e): return e.stringRepresentation
        case .IfStatement(let e): return e.stringRepresentation
        case .ForStatement(let e): return e.stringRepresentation
        case .WhileStatement(let e): return e.stringRepresentation
        case .DoWhileStatement(let e): return e.stringRepresentation
        case .SwitchStatement(let e): return e.stringRepresentation
        case .SwitchStatementCase(let e): return e.stringRepresentation
        case .ReturnStatement(let e): return e.stringRepresentation
        case .BreakStatement(let e): return e.stringRepresentation
        case .ContinueStatement(let e): return e.stringRepresentation
        case .ThrowStatement(let e): return e.stringRepresentation
        case .TryCatchStatement(let e): return e.stringRepresentation
        case .TryCatchStatementClause(let e): return e.stringRepresentation
        case .StatementUnknown(let e): return e.stringRepresentation
        }
    }
    
    public var location: IrLocation? {
        switch self {
        case .EmptyStatement(let e): return e.location
        case .ExpressionStatement(let e): return e.location
        case .VariableDeclarationStatement(let e): return e.location
        case .BlockStatement(let e): return e.location
        case .IfStatement(let e): return e.location
        case .ForStatement(let e): return e.location
        case .WhileStatement(let e): return e.location
        case .DoWhileStatement(let e): return e.location
        case .SwitchStatement(let e): return e.location
        case .SwitchStatementCase(let e): return e.location
        case .ReturnStatement(let e): return e.location
        case .BreakStatement(let e): return e.location
        case .ContinueStatement(let e): return e.location
        case .ThrowStatement(let e): return e.location
        case .TryCatchStatement(let e): return e.location
        case .TryCatchStatementClause(let e): return e.location
        case .StatementUnknown(let e): return e.location
        }
    }
    
    public var annotations: [IrAnnotation] {
        switch self {
        case .EmptyStatement(let e): return e.annotations
        case .ExpressionStatement(let e): return e.annotations
        case .VariableDeclarationStatement(let e): return e.annotations
        case .BlockStatement(let e): return e.annotations
        case .IfStatement(let e): return e.annotations
        case .ForStatement(let e): return e.annotations
        case .WhileStatement(let e): return e.annotations
        case .DoWhileStatement(let e): return e.annotations
        case .SwitchStatement(let e): return e.annotations
        case .SwitchStatementCase(let e): return e.annotations
        case .ReturnStatement(let e): return e.annotations
        case .BreakStatement(let e): return e.annotations
        case .ContinueStatement(let e): return e.annotations
        case .ThrowStatement(let e): return e.annotations
        case .TryCatchStatement(let e): return e.annotations
        case .TryCatchStatementClause(let e): return e.annotations
        case .StatementUnknown(let e): return e.annotations
        }
    }
    
    public var languageProperties: [String : AnyCodable] {
        switch self {
        case .EmptyStatement(let e): return e.languageProperties
        case .ExpressionStatement(let e): return e.languageProperties
        case .VariableDeclarationStatement(let e): return e.languageProperties
        case .BlockStatement(let e): return e.languageProperties
        case .IfStatement(let e): return e.languageProperties
        case .ForStatement(let e): return e.languageProperties
        case .WhileStatement(let e): return e.languageProperties
        case .DoWhileStatement(let e): return e.languageProperties
        case .SwitchStatement(let e): return e.languageProperties
        case .SwitchStatementCase(let e): return e.languageProperties
        case .ReturnStatement(let e): return e.languageProperties
        case .BreakStatement(let e): return e.languageProperties
        case .ContinueStatement(let e): return e.languageProperties
        case .ThrowStatement(let e): return e.languageProperties
        case .TryCatchStatement(let e): return e.languageProperties
        case .TryCatchStatementClause(let e): return e.languageProperties
        case .StatementUnknown(let e): return e.languageProperties
        }
    }
}

/**
 * Represents an empty expression, nothing. Can be used as a fallback.
 */
public struct IrEmptyStatement: IrStatementProtocol {
    public let type: String?
    public let stringRepresentation: [IrStringRepresentation]
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]

    public init(
        stringRepresentation: [IrStringRepresentation] = [],
        location: IrLocation? = nil,
        annotations: [IrAnnotation] = [],
        languageProperties: [String: AnyCodable] = [:]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrEmptyStatement"
        self.stringRepresentation = stringRepresentation
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

/**
 * A statement consisting solely of an expression, e.g. `myFunctionCall();`
 */
public struct IrExpressionStatement: IrStatementProtocol {
    public let type: String?
    public let expression: IrExpression
    public let stringRepresentation: [IrStringRepresentation]
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]

    public init(
        expression: IrExpression,
        stringRepresentation: [IrStringRepresentation] = [],
        location: IrLocation? = nil,
        annotations: [IrAnnotation] = [],
        languageProperties: [String: AnyCodable] = [:]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrExpressionStatement"
        self.expression = expression
        self.stringRepresentation = stringRepresentation
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

/**
 * A variable declaration, e.g. `int x = 10;` or `val x = 10`.
 */
public struct IrVariableDeclarationStatement: IrStatementProtocol {
    public let type: String?
    public let name: String
    public let irType: IrType
    public let additionalNames: [String]
    public let isMutable: Bool
    public let initializer: IrStatement?
    public let stringRepresentation: [IrStringRepresentation]
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]
    
    init(
        name: String,
        irType: IrType,
        additionalNames: [String],
        isMutable: Bool,
        initializer: IrStatement?,
        stringRepresentation: [IrStringRepresentation],
        location: IrLocation?,
        annotations: [IrAnnotation],
        languageProperties: [String : AnyCodable]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrVariableDeclarationStatement"
        self.name = name
        self.irType = irType
        self.additionalNames = additionalNames
        self.isMutable = isMutable
        self.initializer = initializer
        self.stringRepresentation = stringRepresentation
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

/**
 * A block of statements, e.g. `{ ... }`.
 */
public struct IrBlockStatement: IrStatementProtocol {
    public let type: String?
    public let statements: [IrStatement]
    public let stringRepresentation: [IrStringRepresentation]
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]
    
    init(
        statements: [IrStatement],
        stringRepresentation: [IrStringRepresentation],
        location: IrLocation?, annotations: [IrAnnotation],
        languageProperties: [String : AnyCodable]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrBlockStatement"
        self.statements = statements
        self.stringRepresentation = stringRepresentation
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

/**
 * If/else statement, e.g.
 * `if (condition) { ... } else { ... }`
 */
public struct IrIfStatement: IrStatementProtocol {
    public let type: String?
    public let condition: IrExpression
    public let thenStatement: IrStatement
    public let elseStatement: IrStatement?
    public let stringRepresentation: [IrStringRepresentation]
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]
    
    init(
        condition: IrExpression,
        thenStatement: IrStatement,
        elseStatement: IrStatement?,
        stringRepresentation: [IrStringRepresentation],
        location: IrLocation?,
        annotations: [IrAnnotation],
        languageProperties: [String : AnyCodable]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrIfStatement"
        self.condition = condition
        self.thenStatement = thenStatement
        self.elseStatement = elseStatement
        self.stringRepresentation = stringRepresentation
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

/**
 * For loop, e.g. `for (int i = 0; i < 10; i++) { ... }`.
 */
public struct IrForStatement: IrStatementProtocol {
    public let type: String?
    public let initializer: IrStatement?
    public let condition: IrExpression?
    public let update: IrStatement?
    public let body: IrStatement
    public let stringRepresentation: [IrStringRepresentation]
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]
    
    init(
        initializer: IrStatement?,
        condition: IrExpression?,
        update: IrStatement?,
        body: IrStatement,
        stringRepresentation: [IrStringRepresentation],
        location: IrLocation?,
        annotations: [IrAnnotation],
        languageProperties: [String : AnyCodable]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrForStatement"
        self.initializer = initializer
        self.condition = condition
        self.update = update
        self.body = body
        self.stringRepresentation = stringRepresentation
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

/**
 * While loop, e.g. `while (condition) { ... }`
 */
public struct IrWhileStatement: IrStatementProtocol {
    public let type: String?
    public let condition: IrExpression
    public let body: IrStatement
    public let stringRepresentation: [IrStringRepresentation]
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]
    
    init(
        condition: IrExpression,
        body: IrStatement,
        stringRepresentation: [IrStringRepresentation],
        location: IrLocation?,
        annotations: [IrAnnotation],
        languageProperties: [String : AnyCodable]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrWhileStatement"
        self.condition = condition
        self.body = body
        self.stringRepresentation = stringRepresentation
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

/**
 * Do/while loop, e.g. `do { ... } while (condition)`
 */
public struct IrDoWhileStatement: IrStatementProtocol {
    public let type: String?
    public let body: IrStatement
    public let condition: IrExpression
    public let stringRepresentation: [IrStringRepresentation]
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]
    
    init(
        body: IrStatement,
        condition: IrExpression,
        stringRepresentation: [IrStringRepresentation],
        location: IrLocation?,
        annotations: [IrAnnotation],
        languageProperties: [String : AnyCodable]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrDoWhileStatement"
        self.body = body
        self.condition = condition
        self.stringRepresentation = stringRepresentation
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

/**
 * Switch/when statement: language details vary. Typically a set of cases
 * that match an expression. Example usage: Java's `switch(expr) { case X: ... }`
 * or Kotlin's `when(expr) { X -> ... }`.
 */
public struct IrSwitchStatement: IrStatementProtocol {
    
    /**
     * A single case in a switch/when statement.
     */
    public struct IrSwitchStatementCase: IrStatementProtocol {
        public let type: String?
        public let matchExpressions: [IrExpression]
        public let body: IrStatement?
        public let stringRepresentation: [IrStringRepresentation]
        public let location: IrLocation?
        public let annotations: [IrAnnotation]
        public let languageProperties: [String: AnyCodable]
        
        init(
            matchExpressions: [IrExpression],
            body: IrStatement?,
            stringRepresentation: [IrStringRepresentation],
            location: IrLocation?,
            annotations: [IrAnnotation],
            languageProperties: [String : AnyCodable]
        ) {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrSwitchStatement.IrSwitchStatementCase"
            self.matchExpressions = matchExpressions
            self.body = body
            self.stringRepresentation = stringRepresentation
            self.location = location
            self.annotations = annotations
            self.languageProperties = languageProperties
        }
    }
    
    public let type: String?
    public let expression: IrExpression
    public let cases: [IrSwitchStatementCase]
    public let stringRepresentation: [IrStringRepresentation]
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]
    
    init(
        expression: IrExpression,
        cases: [IrSwitchStatementCase],
        stringRepresentation: [IrStringRepresentation],
        location: IrLocation?,
        annotations: [IrAnnotation],
        languageProperties: [String : AnyCodable]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrSwitchStatement"
        self.expression = expression
        self.cases = cases
        self.stringRepresentation = stringRepresentation
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

/**
 * Return statement, e.g. `return expr;`.
 */
public struct IrReturnStatement: IrStatementProtocol {
    public let type: String?
    public let expression: IrExpression?            // default = null in Kotlin
    public let stringRepresentation: [IrStringRepresentation]
    public let location: IrLocation?                // default = null in Kotlin
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]
    
    init(
        expression: IrExpression?,
        stringRepresentation: [IrStringRepresentation],
        location: IrLocation?,
        annotations: [IrAnnotation],
        languageProperties: [String : AnyCodable]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrReturnStatement"
        self.expression = expression
        self.stringRepresentation = stringRepresentation
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

/**
 * Break statement, e.g. `break;`
 */
public struct IrBreakStatement: IrStatementProtocol {
    public let type: String?
    public let stringRepresentation: [IrStringRepresentation]
    public let location: IrLocation?                // default = null in Kotlin
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]
    
    init(
        stringRepresentation: [IrStringRepresentation],
        location: IrLocation?,
        annotations: [IrAnnotation],
        languageProperties: [String : AnyCodable]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrBreakStatement"
        self.stringRepresentation = stringRepresentation
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

/**
 * Continue statement, e.g. `continue;`
 */
public struct IrContinueStatement: IrStatementProtocol {
    public let type: String?
    public let stringRepresentation: [IrStringRepresentation]
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]
    
    init(
        stringRepresentation: [IrStringRepresentation],
        location: IrLocation?,
        annotations: [IrAnnotation],
        languageProperties: [String : AnyCodable]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrContinueStatement"
        self.stringRepresentation = stringRepresentation
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

/**
 * Throw statement, e.g. `throw new MyException("Error");`
 */
public struct IrThrowStatement: IrStatementProtocol {
    public let type: String?
    public let expression: IrExpression
    public let stringRepresentation: [IrStringRepresentation]
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]
    
    init(
        expression: IrExpression,
        stringRepresentation: [IrStringRepresentation],
        location: IrLocation?,
        annotations: [IrAnnotation],
        languageProperties: [String : AnyCodable]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrThrowStatement"
        self.expression = expression
        self.stringRepresentation = stringRepresentation
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

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
public struct IrTryCatchStatement: IrStatementProtocol {
    
    /**
     * Represents a single catch clause (exception type + name + body).
     */
    public struct IrTryCatchStatementClause: IrStatementProtocol {
        public let type: String?
        public let exceptionType: IrType
        public let exceptionName: String?
        public let body: IrStatement?
        public let stringRepresentation: [IrStringRepresentation]
        public let location: IrLocation?
        public let annotations: [IrAnnotation]
        public let languageProperties: [String: AnyCodable]
        
        init(
            exceptionType: IrType,
            exceptionName: String?,
            body: IrStatement?,
            stringRepresentation: [IrStringRepresentation],
            location: IrLocation?,
            annotations: [IrAnnotation],
            languageProperties: [String : AnyCodable]
        ) {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrTryCatchStatement.IrTryCatchStatementClause"
            self.exceptionType = exceptionType
            self.exceptionName = exceptionName
            self.body = body
            self.stringRepresentation = stringRepresentation
            self.location = location
            self.annotations = annotations
            self.languageProperties = languageProperties
        }
    }
    
    public let type: String?
    public let tryBlock: IrStatement
    public let catchClauses: [IrTryCatchStatementClause]
    public let finallyBlock: IrStatement?
    public let stringRepresentation: [IrStringRepresentation]
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]
    
    init(
        tryBlock: IrStatement,
        catchClauses: [IrTryCatchStatementClause],
        finallyBlock: IrStatement?,
        stringRepresentation: [IrStringRepresentation],
        location: IrLocation?,
        annotations: [IrAnnotation],
        languageProperties: [String : AnyCodable]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrTryCatchStatement"
        self.tryBlock = tryBlock
        self.catchClauses = catchClauses
        self.finallyBlock = finallyBlock
        self.stringRepresentation = stringRepresentation
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

/**
 * Fallback when the frontend was unable to convert a statement.
 */
public struct IrStatementUnknown: IrStatementProtocol {
    public let type: String?
    public let stringRepresentation: [IrStringRepresentation]
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]
    
    init(
        stringRepresentation: [IrStringRepresentation],
        location: IrLocation?,
        annotations: [IrAnnotation],
        languageProperties: [String : AnyCodable]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrStatementUnknown"
        self.stringRepresentation = stringRepresentation
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}
