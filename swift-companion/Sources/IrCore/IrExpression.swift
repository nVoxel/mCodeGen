//
//  IrExpression.swift
//  swift-companion
//
//  Created by nVoxel on 18.06.2025.
//

import Foundation
import AnyCodable

public protocol IrExpressionProtocol: IrElementProtocol {
    var type: String? { get }
    var stringRepresentation: [IrStringRepresentation] { get }
}

/**
 * Base interface for all IR expressions.
 */
public indirect enum IrExpression: IrExpressionProtocol {
    case EmptyExpression(IrEmptyExpression)
    case LiteralExpression(IrLiteralExpression)
    case IdentifierExpression(IrIdentifierExpression)
    case TypeReferenceIdentifierExpression(IrTypeReferenceIdentifierExpression)
    case PropertyAccessExpression(IrPropertyAccessExpression)
    case MethodCallExpression(IrMethodCallExpression)
    case ObjectCreationExpression(IrObjectCreationExpression)
    case BinaryExpression(IrBinaryExpression)
    case UnaryExpression(IrUnaryExpression)
    case AssignmentExpression(IrAssignmentExpression)
    case TernaryExpression(IrTernaryExpression)
    case CastExpression(IrCastExpression)
    case TypeCheckExpression(IrTypeCheckExpression)
    case ExpressionUnknown(IrExpressionUnknown)
    
    private enum CodingKeys: String, CodingKey {
        case type
    }
    
    public init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        let type = try container.decode(String.self, forKey: .type)
        
        switch type {
        case "com.voxeldev.mcodegen.dsl.ir.IrEmptyExpression": self = .EmptyExpression(try IrEmptyExpression(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrLiteralExpression": self = .LiteralExpression(try IrLiteralExpression(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrIdentifierExpression": self = .IdentifierExpression(try IrIdentifierExpression(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrTypeReferenceIdentifierExpression": self = .TypeReferenceIdentifierExpression(try IrTypeReferenceIdentifierExpression(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrPropertyAccessExpression": self = .PropertyAccessExpression(try IrPropertyAccessExpression(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrMethodCallExpression": self = .MethodCallExpression(try IrMethodCallExpression(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrObjectCreationExpression": self = .ObjectCreationExpression(try IrObjectCreationExpression(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression": self = .BinaryExpression(try IrBinaryExpression(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrUnaryExpression": self = .UnaryExpression(try IrUnaryExpression(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrAssignmentExpression": self = .AssignmentExpression(try IrAssignmentExpression(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrTernaryExpression": self = .TernaryExpression(try IrTernaryExpression(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrCastExpression": self = .CastExpression(try IrCastExpression(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrTypeCheckExpression": self = .TypeCheckExpression(try IrTypeCheckExpression(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrExpressionUnknown": self = .ExpressionUnknown(try IrExpressionUnknown(from: decoder))
        default:
            throw DecodingError.dataCorruptedError(
                forKey: .type,
                in: container,
                debugDescription: "Unknown IrExpression kind: \(type)"
            )
        }
    }
    
    public func encode(to encoder: any Encoder) throws {
        switch self {
        case .EmptyExpression(let e): try e.encode(to: encoder)
        case .LiteralExpression(let e): try e.encode(to: encoder)
        case .IdentifierExpression(let e): try e.encode(to: encoder)
        case .TypeReferenceIdentifierExpression(let e): try e.encode(to: encoder)
        case .PropertyAccessExpression(let e): try e.encode(to: encoder)
        case .MethodCallExpression(let e): try e.encode(to: encoder)
        case .ObjectCreationExpression(let e): try e.encode(to: encoder)
        case .BinaryExpression(let e): try e.encode(to: encoder)
        case .UnaryExpression(let e): try e.encode(to: encoder)
        case .AssignmentExpression(let e): try e.encode(to: encoder)
        case .TernaryExpression(let e): try e.encode(to: encoder)
        case .CastExpression(let e): try e.encode(to: encoder)
        case .TypeCheckExpression(let e): try e.encode(to: encoder)
        case .ExpressionUnknown(let e): try e.encode(to: encoder)
        }
    }
    
    public var type: String? {
        switch self {
        case .EmptyExpression(let e): return e.type
        case .LiteralExpression(let e): return e.type
        case .IdentifierExpression(let e): return e.type
        case .TypeReferenceIdentifierExpression(let e): return e.type
        case .PropertyAccessExpression(let e): return e.type
        case .MethodCallExpression(let e): return e.type
        case .ObjectCreationExpression(let e): return e.type
        case .BinaryExpression(let e): return e.type
        case .UnaryExpression(let e): return e.type
        case .AssignmentExpression(let e): return e.type
        case .TernaryExpression(let e): return e.type
        case .CastExpression(let e): return e.type
        case .TypeCheckExpression(let e): return e.type
        case .ExpressionUnknown(let e): return e.type
        }
    }
    
    public var stringRepresentation: [IrStringRepresentation] {
        switch self {
        case .EmptyExpression(let e): return e.stringRepresentation
        case .LiteralExpression(let e): return e.stringRepresentation
        case .IdentifierExpression(let e): return e.stringRepresentation
        case .TypeReferenceIdentifierExpression(let e): return e.stringRepresentation
        case .PropertyAccessExpression(let e): return e.stringRepresentation
        case .MethodCallExpression(let e): return e.stringRepresentation
        case .ObjectCreationExpression(let e): return e.stringRepresentation
        case .BinaryExpression(let e): return e.stringRepresentation
        case .UnaryExpression(let e): return e.stringRepresentation
        case .AssignmentExpression(let e): return e.stringRepresentation
        case .TernaryExpression(let e): return e.stringRepresentation
        case .CastExpression(let e): return e.stringRepresentation
        case .TypeCheckExpression(let e): return e.stringRepresentation
        case .ExpressionUnknown(let e): return e.stringRepresentation
        }
    }
    
    public var location: IrLocation? {
        switch self {
        case .EmptyExpression(let e): return e.location
        case .LiteralExpression(let e): return e.location
        case .IdentifierExpression(let e): return e.location
        case .TypeReferenceIdentifierExpression(let e): return e.location
        case .PropertyAccessExpression(let e): return e.location
        case .MethodCallExpression(let e): return e.location
        case .ObjectCreationExpression(let e): return e.location
        case .BinaryExpression(let e): return e.location
        case .UnaryExpression(let e): return e.location
        case .AssignmentExpression(let e): return e.location
        case .TernaryExpression(let e): return e.location
        case .CastExpression(let e): return e.location
        case .TypeCheckExpression(let e): return e.location
        case .ExpressionUnknown(let e): return e.location
        }
    }
    
    public var annotations: [IrAnnotation] {
        switch self {
        case .EmptyExpression(let e): return e.annotations
        case .LiteralExpression(let e): return e.annotations
        case .IdentifierExpression(let e): return e.annotations
        case .TypeReferenceIdentifierExpression(let e): return e.annotations
        case .PropertyAccessExpression(let e): return e.annotations
        case .MethodCallExpression(let e): return e.annotations
        case .ObjectCreationExpression(let e): return e.annotations
        case .BinaryExpression(let e): return e.annotations
        case .UnaryExpression(let e): return e.annotations
        case .AssignmentExpression(let e): return e.annotations
        case .TernaryExpression(let e): return e.annotations
        case .CastExpression(let e): return e.annotations
        case .TypeCheckExpression(let e): return e.annotations
        case .ExpressionUnknown(let e): return e.annotations
        }
    }
    
    public var languageProperties: [String : AnyCodable] {
        switch self {
        case .EmptyExpression(let e): return e.languageProperties
        case .LiteralExpression(let e): return e.languageProperties
        case .IdentifierExpression(let e): return e.languageProperties
        case .TypeReferenceIdentifierExpression(let e): return e.languageProperties
        case .PropertyAccessExpression(let e): return e.languageProperties
        case .MethodCallExpression(let e): return e.languageProperties
        case .ObjectCreationExpression(let e): return e.languageProperties
        case .BinaryExpression(let e): return e.languageProperties
        case .UnaryExpression(let e): return e.languageProperties
        case .AssignmentExpression(let e): return e.languageProperties
        case .TernaryExpression(let e): return e.languageProperties
        case .CastExpression(let e): return e.languageProperties
        case .TypeCheckExpression(let e): return e.languageProperties
        case .ExpressionUnknown(let e): return e.languageProperties
        }
    }
}

/**
 * Represents an empty expression, nothing. Can be used as a fallback.
 * If met during statement construction, the statement becomes `IrEmptyStatement` as well.
 * @see IrExpressionUnknown
 */
public struct IrEmptyExpression: IrExpressionProtocol {
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
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrEmptyExpression"
        self.stringRepresentation = stringRepresentation
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

/**
 * Represents an integer, float, string, boolean, char, etc.
 */
public struct IrLiteralExpression: IrExpressionProtocol {
    public let type: String?
    public let value: String
    public let stringRepresentation: [IrStringRepresentation]
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]
    
    public init(
        value: String,
        stringRepresentation: [IrStringRepresentation] = [],
        location: IrLocation? = nil,
        annotations: [IrAnnotation] = [],
        languageProperties: [String: AnyCodable] = [:]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrLiteralExpression"
        self.value = value
        self.stringRepresentation = stringRepresentation
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

/**
 * An identifier referencing a variable, parameter, constant, etc.
 * Must reference any type that should NOT be dynamically imported.
 * e.g., `myVariable` or `System` or `MyClass`
 */
public struct IrIdentifierExpression: IrExpressionProtocol {
    public let type: String?
    /// right part
    public let qualifier: IrExpression?
    /// left part
    public let selector: IrExpression
    
    public let stringRepresentation: [IrStringRepresentation]
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]
    
    public init(
        qualifier: IrExpression?,
        selector: IrExpression,
        stringRepresentation: [IrStringRepresentation],
        location: IrLocation?,
        annotations: [IrAnnotation],
        languageProperties: [String: AnyCodable]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrIdentifierExpression"
        self.qualifier = qualifier
        self.selector = selector
        self.stringRepresentation = stringRepresentation
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

/**
 * An identifier referencing a variable, parameter, constant, class name, etc.
 * Can reference a type that should be dynamically imported.
 * e.g., `myVariable` or `System` or `MyClass`
 */
public struct IrTypeReferenceIdentifierExpression: IrExpressionProtocol {
    public let type: String?
    /// selector, left part
    public let referencedType: IrType
    
    public let stringRepresentation: [IrStringRepresentation]
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]
    
    public init(
        referencedType: IrType,
        stringRepresentation: [IrStringRepresentation],
        location: IrLocation?,
        annotations: [IrAnnotation],
        languageProperties: [String: AnyCodable]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrTypeReferenceIdentifierExpression"
        self.referencedType = referencedType
        self.stringRepresentation = stringRepresentation
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

/**
 * A property/field or nested reference (object.property, or object.property.inner).
 */
public struct IrPropertyAccessExpression: IrExpressionProtocol {
    public let type: String?
    public let receiver: IrExpression?
    public let propertyName: String
    
    public let stringRepresentation: [IrStringRepresentation]
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]
    
    public init(
        receiver: IrExpression?,
        propertyName: String,
        stringRepresentation: [IrStringRepresentation],
        location: IrLocation?,
        annotations: [IrAnnotation],
        languageProperties: [String: AnyCodable]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrPropertyAccessExpression"
        self.receiver = receiver
        self.propertyName = propertyName
        self.stringRepresentation = stringRepresentation
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

/**
 * A method or function call, optionally with a receiver (e.g. `obj.foo(arg1, arg2)`, or `foo()`)
 */
public struct IrMethodCallExpression: IrExpressionProtocol {
    
    public protocol IrMethodCallKindProtocol : Codable {
        var type: String? { get }
    }
    
    public enum IrMethodCallKind : IrMethodCallKindProtocol {
        case DefaultMethodCallKind(IrDefaultMethodCallKind)
        case ThisMethodCallKind(IrThisMethodCallKind)
        case SuperMethodCallKind(IrSuperMethodCallKind)
        
        private enum CodingKeys: String, CodingKey {
            case type
        }
        
        public init(from decoder: Decoder) throws {
            let container = try decoder.container(keyedBy: CodingKeys.self)
            let type = try container.decode(String.self, forKey: .type)
            
            switch type {
            case "com.voxeldev.mcodegen.dsl.ir.IrMethodCallExpression.IrDefaultMethodCallKind": self = .DefaultMethodCallKind(try IrDefaultMethodCallKind(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrMethodCallExpression.IrThisMethodCallKind": self = .ThisMethodCallKind(try IrThisMethodCallKind(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrMethodCallExpression.IrSuperMethodCallKind": self = .SuperMethodCallKind(try IrSuperMethodCallKind(from: decoder))
            default:
                throw DecodingError.dataCorruptedError(
                    forKey: .type,
                    in: container,
                    debugDescription: "Unknown IrMethodCallKind kind: \(type)"
                )
            }
        }
        
        public func encode(to encoder: any Encoder) throws {
            switch self {
            case .DefaultMethodCallKind(let e): try e.encode(to: encoder)
            case .ThisMethodCallKind(let e): try e.encode(to: encoder)
            case .SuperMethodCallKind(let e): try e.encode(to: encoder)
            }
        }
        
        public var type: String? {
            switch self {
            case .DefaultMethodCallKind(let e): return e.type
            case .ThisMethodCallKind(let e): return e.type
            case .SuperMethodCallKind(let e): return e.type
            }
        }
    }
    
    public struct IrDefaultMethodCallKind : IrMethodCallKindProtocol {
        public let type: String?
        
        init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrMethodCallExpression.IrDefaultMethodCallKind"
        }
    }
    
    public struct IrThisMethodCallKind : IrMethodCallKindProtocol {
        public let type: String?
        
        init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrMethodCallExpression.IrThisMethodCallKind"
        }
    }
    
    public struct IrSuperMethodCallKind : IrMethodCallKindProtocol {
        public let type: String?
        
        init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrMethodCallExpression.IrSuperMethodCallKind"
        }
    }
    
    public let type: String?
    /// `nil` if it's a top-level function call
    public let receiver: IrExpression?
    public let methodName: String
    public let valueArguments: [IrExpression]
    /// Contains only explicit (written in the code) arguments
    public let typeArguments: [IrType]
    public let irMethodCallKind: IrMethodCallKind
    
    public let stringRepresentation: [IrStringRepresentation]
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]
    
    public init(
        receiver: IrExpression?,
        methodName: String,
        valueArguments: [IrExpression],
        typeArguments: [IrType],
        irMethodCallKind: IrMethodCallKind,
        stringRepresentation: [IrStringRepresentation],
        location: IrLocation?,
        annotations: [IrAnnotation],
        languageProperties: [String: AnyCodable]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrMethodCallExpression"
        self.receiver = receiver
        self.methodName = methodName
        self.valueArguments = valueArguments
        self.typeArguments = typeArguments
        self.irMethodCallKind = irMethodCallKind
        self.stringRepresentation = stringRepresentation
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

/**
 * An object creation expression (like `new MyClass(...)` in Java or `MyClass(...)` in Kotlin).
 */
public struct IrObjectCreationExpression: IrExpressionProtocol {
    public let type: String?
    public let className: String
    public let constructorArgs: [IrExpression]
    public let stringRepresentation: [IrStringRepresentation]
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]
    
    init(
        className: String,
        constructorArgs: [IrExpression],
        stringRepresentation: [IrStringRepresentation],
        location: IrLocation?,
        annotations: [IrAnnotation],
        languageProperties: [String : AnyCodable]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrObjectCreationExpression"
        self.className = className
        self.constructorArgs = constructorArgs
        self.stringRepresentation = stringRepresentation
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

/**
 * A binary operation (left op right), e.g. `a + b`, `x == y`, etc.
 */
public struct IrBinaryExpression: IrExpressionProtocol {
    
    public protocol IrBinaryOperatorProtocol : Codable {
        var type: String? { get }
    }
    
    public enum IrBinaryOperator : IrBinaryOperatorProtocol {
        case PlusOperator(Plus)
        case MinusOperator(Minus)
        case MultiplyOperator(Multiply)
        case DivideOperator(Divide)
        case ModuloOperator(Modulo)
        case EqualsOperator(Equals)
        case NotEqualsOperator(NotEquals)
        case GreaterOperator(Greater)
        case GreaterOrEqualOperator(GreaterOrEqual)
        case LessOperator(Less)
        case LessOrEqualOperator(LessOrEqual)
        case AndOperator(And)
        case OrOperator(Or)
        case BitwiseAndOperator(BitwiseAnd)
        case BitwiseOrOperator(BitwiseOr)
        case BitwiseXorOperator(BitwiseXor)
        case ShiftLeftOperator(ShiftLeft)
        case ShiftRightOperator(ShiftRight)
        
        private enum CodingKeys: String, CodingKey {
            case type
        }
        
        public init(from decoder: Decoder) throws {
            let container = try decoder.container(keyedBy: CodingKeys.self)
            let type = try container.decode(String.self, forKey: .type)
            
            switch type {
            case "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.Plus": self = .PlusOperator(try Plus(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.Minus": self = .MinusOperator(try Minus(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.Multiply": self = .MultiplyOperator(try Multiply(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.Divide": self = .DivideOperator(try Divide(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.Modulo": self = .ModuloOperator(try Modulo(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.Equals": self = .EqualsOperator(try Equals(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.NotEquals": self = .NotEqualsOperator(try NotEquals(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.Greater": self = .GreaterOperator(try Greater(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.GreaterOrEqual": self = .GreaterOrEqualOperator(try GreaterOrEqual(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.Less": self = .LessOperator(try Less(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.LessOrEqual": self = .LessOrEqualOperator(try LessOrEqual(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.And": self = .AndOperator(try And(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.Or": self = .OrOperator(try Or(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.BitwiseAnd": self = .BitwiseAndOperator(try BitwiseAnd(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.BitwiseOr": self = .BitwiseOrOperator(try BitwiseOr(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.BitwiseXor": self = .BitwiseXorOperator(try BitwiseXor(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.ShiftLeft": self = .ShiftLeftOperator(try ShiftLeft(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.ShiftRight": self = .ShiftRightOperator(try ShiftRight(from: decoder))
            default:
                throw DecodingError.dataCorruptedError(
                    forKey: .type,
                    in: container,
                    debugDescription: "Unknown IrBinaryExpression kind: \(type)"
                )
            }
        }
        
        public func encode(to encoder: any Encoder) throws {
            switch self {
            case .PlusOperator(let e): try e.encode(to: encoder)
            case .MinusOperator(let e): try e.encode(to: encoder)
            case .MultiplyOperator(let e): try e.encode(to: encoder)
            case .DivideOperator(let e): try e.encode(to: encoder)
            case .ModuloOperator(let e): try e.encode(to: encoder)
            case .EqualsOperator(let e): try e.encode(to: encoder)
            case .NotEqualsOperator(let e): try e.encode(to: encoder)
            case .GreaterOperator(let e): try e.encode(to: encoder)
            case .GreaterOrEqualOperator(let e): try e.encode(to: encoder)
            case .LessOperator(let e): try e.encode(to: encoder)
            case .LessOrEqualOperator(let e): try e.encode(to: encoder)
            case .AndOperator(let e): try e.encode(to: encoder)
            case .OrOperator(let e): try e.encode(to: encoder)
            case .BitwiseAndOperator(let e): try e.encode(to: encoder)
            case .BitwiseOrOperator(let e): try e.encode(to: encoder)
            case .BitwiseXorOperator(let e): try e.encode(to: encoder)
            case .ShiftLeftOperator(let e): try e.encode(to: encoder)
            case .ShiftRightOperator(let e): try e.encode(to: encoder)
            }
        }
        
        public var type: String? {
            switch self {
            case .PlusOperator(let e): e.type
            case .MinusOperator(let e): e.type
            case .MultiplyOperator(let e): e.type
            case .DivideOperator(let e): e.type
            case .ModuloOperator(let e): e.type
            case .EqualsOperator(let e): e.type
            case .NotEqualsOperator(let e): e.type
            case .GreaterOperator(let e): e.type
            case .GreaterOrEqualOperator(let e): e.type
            case .LessOperator(let e): e.type
            case .LessOrEqualOperator(let e): e.type
            case .AndOperator(let e): e.type
            case .OrOperator(let e): e.type
            case .BitwiseAndOperator(let e): e.type
            case .BitwiseOrOperator(let e): e.type
            case .BitwiseXorOperator(let e): e.type
            case .ShiftLeftOperator(let e): e.type
            case .ShiftRightOperator(let e): e.type
            }
        }
    }
    
    public struct Plus : IrBinaryOperatorProtocol {
        public let type: String?
        
        init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.Plus"
        }
    }
    
    public struct Minus : IrBinaryOperatorProtocol {
        public let type: String?
        
        init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.Minus"
        }
    }
    
    public struct Multiply : IrBinaryOperatorProtocol {
        public let type: String?
        
        init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.Multiply"
        }
    }
    
    public struct Divide : IrBinaryOperatorProtocol {
        public let type: String?
        
        init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.Divide"
        }
    }
    
    public struct Modulo : IrBinaryOperatorProtocol {
        public let type: String?
        
        init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.Modulo"
        }
    }
    
    public struct Equals : IrBinaryOperatorProtocol {
        public let type: String?
        
        init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.Equals"
        }
    }
    
    public struct NotEquals : IrBinaryOperatorProtocol {
        public let type: String?
        
        init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.NotEquals"
        }
    }
    
    public struct Greater : IrBinaryOperatorProtocol {
        public let type: String?
        
        init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.Greater"
        }
    }
    
    public struct GreaterOrEqual : IrBinaryOperatorProtocol {
        public let type: String?
        
        init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.GreaterOrEqual"
        }
    }
    
    public struct Less : IrBinaryOperatorProtocol {
        public let type: String?
        
        init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.Less"
        }
    }
    
    public struct LessOrEqual : IrBinaryOperatorProtocol {
        public let type: String?
        
        init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.LessOrEqual"
        }
    }
    
    public struct And : IrBinaryOperatorProtocol {
        public let type: String?
        
        init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.And"
        }
    }
    
    public struct Or : IrBinaryOperatorProtocol {
        public let type: String?
        
        init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.Or"
        }
    }
    
    public struct BitwiseAnd : IrBinaryOperatorProtocol {
        public let type: String?
        
        init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.BitwiseAnd"
        }
    }
    
    public struct BitwiseOr : IrBinaryOperatorProtocol {
        public let type: String?
        
        init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.BitwiseOr"
        }
    }
    
    public struct BitwiseXor : IrBinaryOperatorProtocol {
        public let type: String?
        
        init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.BitwiseXor"
        }
    }
    
    public struct ShiftLeft : IrBinaryOperatorProtocol {
        public let type: String?
        
        init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.ShiftLeft"
        }
    }
    
    public struct ShiftRight : IrBinaryOperatorProtocol {
        public let type: String?
        
        init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator.ShiftRight"
        }
    }
    
    public let type: String?
    public let left: IrExpression
    public let `operator`: IrBinaryOperator
    public let right: IrExpression
    public let stringRepresentation: [IrStringRepresentation]
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]
    
    init(
        left: IrExpression,
        `operator`: IrBinaryOperator,
        right: IrExpression,
        stringRepresentation: [IrStringRepresentation],
        location: IrLocation?,
        annotations: [IrAnnotation],
        languageProperties: [String : AnyCodable]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression"
        self.left = left
        self.`operator` = `operator`
        self.right = right
        self.stringRepresentation = stringRepresentation
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

/**
 * A unary operation, e.g. `-x`, `!flag`, etc.
 */
public struct IrUnaryExpression: IrExpressionProtocol {
    
    public protocol IrUnaryOperatorProtocol: Codable {
        var type: String? { get }
    }
    
    public enum IrUnaryOperator: IrUnaryOperatorProtocol {
        
        case NotOperator(Not)
        case PlusOperator(Plus)
        case MinusOperator(Minus)
        case IncrementOperator(Increment)
        case DecrementOperator(Decrement)
        
        // ——— Codable plumbing ———
        private enum CodingKeys: String, CodingKey { case type }
        
        public init(from decoder: Decoder) throws {
            let container = try decoder.container(keyedBy: CodingKeys.self)
            let type = try container.decode(String.self, forKey: .type)
            
            switch type {
            case "com.voxeldev.mcodegen.dsl.ir.IrUnaryExpression.IrUnaryOperator.Not":
                self = .NotOperator(try Not(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrUnaryExpression.IrUnaryOperator.Plus":
                self = .PlusOperator(try Plus(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrUnaryExpression.IrUnaryOperator.Minus":
                self = .MinusOperator(try Minus(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrUnaryExpression.IrUnaryOperator.Increment":
                self = .IncrementOperator(try Increment(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrUnaryExpression.IrUnaryOperator.Decrement":
                self = .DecrementOperator(try Decrement(from: decoder))
            default:
                throw DecodingError.dataCorruptedError(
                    forKey: .type,
                    in: container,
                    debugDescription: "Unknown IrUnaryOperator kind: \(type)"
                )
            }
        }
        
        public func encode(to encoder: Encoder) throws {
            switch self {
            case .NotOperator(let op): try op.encode(to: encoder)
            case .PlusOperator(let op): try op.encode(to: encoder)
            case .MinusOperator(let op): try op.encode(to: encoder)
            case .IncrementOperator(let op): try op.encode(to: encoder)
            case .DecrementOperator(let op): try op.encode(to: encoder)
            }
        }
        
        public var type: String? {
            switch self {
            case .NotOperator(let op): return op.type
            case .PlusOperator(let op): return op.type
            case .MinusOperator(let op): return op.type
            case .IncrementOperator(let op): return op.type
            case .DecrementOperator(let op): return op.type
            }
        }
    }
    
    public struct Not: IrUnaryOperatorProtocol {
        public let type: String?
        public init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrUnaryExpression.IrUnaryOperator.Not"
        }
    }
    
    public struct Plus: IrUnaryOperatorProtocol {
        public let type: String?
        public init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrUnaryExpression.IrUnaryOperator.Plus"
        }
    }
    
    public struct Minus: IrUnaryOperatorProtocol {
        public let type: String?
        public init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrUnaryExpression.IrUnaryOperator.Minus"
        }
    }
    
    public struct Increment: IrUnaryOperatorProtocol {
        public let type: String?
        public init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrUnaryExpression.IrUnaryOperator.Increment"
        }
    }
    
    public struct Decrement: IrUnaryOperatorProtocol {
        public let type: String?
        public init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrUnaryExpression.IrUnaryOperator.Decrement"
        }
    }
    
    public let type: String?
    public let `operator`: IrUnaryOperator
    public let operand: IrExpression
    public let isPrefix: Bool
    public let stringRepresentation: [IrStringRepresentation]
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]
    
    init(
        `operator`: IrUnaryOperator,
        operand: IrExpression,
        isPrefix: Bool,
        stringRepresentation: [IrStringRepresentation],
        location: IrLocation?,
        annotations: [IrAnnotation],
        languageProperties: [String : AnyCodable]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrUnaryExpression"
        self.`operator` = `operator`
        self.operand = operand
        self.isPrefix = isPrefix
        self.stringRepresentation = stringRepresentation
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

/**
 * Assignment expression that represents assigning value to target, e.g. `a = b`.
 */
public struct IrAssignmentExpression: IrExpressionProtocol {
    
    public protocol IrAssignmentOperatorProtocol: Codable {
        var type: String? { get }
    }

    public enum IrAssignmentOperator: IrAssignmentOperatorProtocol {

        case AssignOperator(Assign)
        case PlusAssignOperator(PlusAssign)
        case MinusAssignOperator(MinusAssign)
        case MultiplyAssignOperator(MultiplyAssign)
        case DivideAssignOperator(DivideAssign)
        case ModuloAssignOperator(ModuloAssign)

        private enum CodingKeys: String, CodingKey { case type }

        public init(from decoder: Decoder) throws {
            let container = try decoder.container(keyedBy: CodingKeys.self)
            let type = try container.decode(String.self, forKey: .type)

            switch type {
            case "com.voxeldev.mcodegen.dsl.ir.IrAssignmentExpression.IrAssignmentOperator.Assign":
                self = .AssignOperator(try Assign(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrAssignmentExpression.IrAssignmentOperator.PlusAssign":
                self = .PlusAssignOperator(try PlusAssign(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrAssignmentExpression.IrAssignmentOperator.MinusAssign":
                self = .MinusAssignOperator(try MinusAssign(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrAssignmentExpression.IrAssignmentOperator.MultiplyAssign":
                self = .MultiplyAssignOperator(try MultiplyAssign(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrAssignmentExpression.IrAssignmentOperator.DivideAssign":
                self = .DivideAssignOperator(try DivideAssign(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrAssignmentExpression.IrAssignmentOperator.ModuloAssign":
                self = .ModuloAssignOperator(try ModuloAssign(from: decoder))
            default:
                throw DecodingError.dataCorruptedError(
                    forKey: .type,
                    in: container,
                    debugDescription: "Unknown IrAssignmentOperator kind: \(type)"
                )
            }
        }

        public func encode(to encoder: Encoder) throws {
            switch self {
            case .AssignOperator(let op): try op.encode(to: encoder)
            case .PlusAssignOperator(let op): try op.encode(to: encoder)
            case .MinusAssignOperator(let op): try op.encode(to: encoder)
            case .MultiplyAssignOperator(let op): try op.encode(to: encoder)
            case .DivideAssignOperator(let op): try op.encode(to: encoder)
            case .ModuloAssignOperator(let op): try op.encode(to: encoder)
            }
        }

        // passthrough
        public var type: String? {
            switch self {
            case .AssignOperator(let op): return op.type
            case .PlusAssignOperator(let op): return op.type
            case .MinusAssignOperator(let op): return op.type
            case .MultiplyAssignOperator(let op): return op.type
            case .DivideAssignOperator(let op): return op.type
            case .ModuloAssignOperator(let op): return op.type
            }
        }
    }

    public struct Assign: IrAssignmentOperatorProtocol {
        public let type: String?
        public init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrAssignmentExpression.IrAssignmentOperator.Assign"
        }
    }

    public struct PlusAssign: IrAssignmentOperatorProtocol {
        public let type: String?
        public init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrAssignmentExpression.IrAssignmentOperator.PlusAssign"
        }
    }

    public struct MinusAssign: IrAssignmentOperatorProtocol {
        public let type: String?
        public init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrAssignmentExpression.IrAssignmentOperator.MinusAssign"
        }
    }

    public struct MultiplyAssign: IrAssignmentOperatorProtocol {
        public let type: String?
        public init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrAssignmentExpression.IrAssignmentOperator.MultiplyAssign"
        }
    }

    public struct DivideAssign: IrAssignmentOperatorProtocol {
        public let type: String?
        public init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrAssignmentExpression.IrAssignmentOperator.DivideAssign"
        }
    }

    public struct ModuloAssign: IrAssignmentOperatorProtocol {
        public let type: String?
        public init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrAssignmentExpression.IrAssignmentOperator.ModuloAssign"
        }
    }
    
    public let type: String?
    /// Typically an `IrIdentifierExpression` or `IrPropertyAccessExpression`
    public let target: IrExpression
    public let `operator`: IrAssignmentOperator
    public let value: IrExpression
    public let stringRepresentation: [IrStringRepresentation]
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]
    
    init(
        target: IrExpression,
        `operator`: IrAssignmentOperator,
        value: IrExpression,
        stringRepresentation: [IrStringRepresentation],
        location: IrLocation?,
        annotations: [IrAnnotation],
        languageProperties: [String : AnyCodable]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrAssignmentExpression"
        self.target = target
        self.`operator` = `operator`
        self.value = value
        self.stringRepresentation = stringRepresentation
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

/**
 * Ternary/conditional expression: condition ? ifTrue : ifFalse
 */
public struct IrTernaryExpression: IrExpressionProtocol {
    public let type: String?
    public let condition: IrExpression
    public let ifTrue: IrExpression
    public let ifFalse: IrExpression
    public let stringRepresentation: [IrStringRepresentation]
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]
    
    init(
        condition: IrExpression,
        ifTrue: IrExpression,
        ifFalse: IrExpression,
        stringRepresentation: [IrStringRepresentation],
        location: IrLocation?,
        annotations: [IrAnnotation],
        languageProperties: [String : AnyCodable]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrTernaryExpression"
        self.condition = condition
        self.ifTrue = ifTrue
        self.ifFalse = ifFalse
        self.stringRepresentation = stringRepresentation
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

/**
 * A cast expression, e.g. `(Type) expr` in Java, or `expr as Type` in Kotlin.
 */
public struct IrCastExpression: IrExpressionProtocol {
    public let type: String?
    public let expression: IrExpression
    public let targetType: IrType
    public let stringRepresentation: [IrStringRepresentation]
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]
    
    init(expression: IrExpression,
         targetType: IrType,
         stringRepresentation: [IrStringRepresentation],
         location: IrLocation?,
         annotations: [IrAnnotation],
         languageProperties: [String : AnyCodable]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrCastExpression"
        self.expression = expression
        self.targetType = targetType
        self.stringRepresentation = stringRepresentation
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

/**
 * A type-check expression (Java `instanceof`, Kotlin `is`, Swift `is`).
 */
public struct IrTypeCheckExpression: IrExpressionProtocol {
    public let type: String?
    public let expression: IrExpression
    public let checkType: IrType
    public let stringRepresentation: [IrStringRepresentation]
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]
    
    init(
        expression: IrExpression,
        checkType: IrType,
        stringRepresentation: [IrStringRepresentation],
        location: IrLocation?,
        annotations: [IrAnnotation],
        languageProperties: [String : AnyCodable]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrTypeCheckExpression"
        self.expression = expression
        self.checkType = checkType
        self.stringRepresentation = stringRepresentation
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

/**
 * Fallback when the frontend was unable to convert an expression.
 */
public struct IrExpressionUnknown: IrExpressionProtocol {
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
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrExpressionUnknown"
        self.stringRepresentation = stringRepresentation
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}
