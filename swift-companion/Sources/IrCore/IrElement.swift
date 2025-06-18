//
//  IrElement.swift
//  swift-companion
//
//  Created by nVoxel on 17.06.2025.
//

import Foundation
import AnyCodable

public protocol IrElementProtocol: Codable {
    var type: String? { get }
    
    /// The source location of the element within its original file, if available.
    var location: IrLocation? { get }

    /// A collection of annotations attached to this element.
    var annotations: [IrAnnotation] { get }

    /// Arbitrary language‑specific key/value pairs.
    var languageProperties: [String: AnyCodable] { get }
}

/**
 * Base interface for all IR (Intermediate Representation) elements in the code generation system.
 * This interface defines the common properties that all IR elements must have.
 */
public enum IrElement: IrElementProtocol {
    case Class(IrClass)
    case SuperClass(IrSuperClass)
    case Field(IrField)
    case TypeParameter(IrTypeParameter)
    case ClassInitializer(IrClassInitializer)
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
    case Import(IrImport)
    case Method(IrMethod)
    case Constructor(IrConstructor)
    case Parameter(IrParameter)
    case MethodBody(IrMethodBody)
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
    case TypeReference(IrTypeReference)
    case TypePrimitive(IrTypePrimitive)
    case TypeFunction(IrTypeFunction)
    case TypeArray(IrTypeArray)
    case TypeGeneric(IrTypeGeneric)
    
    private enum CodingKeys: String, CodingKey {
        case type
    }
    
    public init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        let type = try container.decode(String.self, forKey: .type)
        
        switch type {
        case "com.voxeldev.mcodegen.dsl.ir.IrClass": self = .Class(try IrClass(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrSuperClass": self = .SuperClass(try IrSuperClass(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrField": self = .Field(try IrField(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrTypeParameter": self = .TypeParameter(try IrTypeParameter(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrClassInitializer": self = .ClassInitializer(try IrClassInitializer(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrEmptyExpression": self = .EmptyExpression(try IrEmptyExpression(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrLiteralExpression": self = .LiteralExpression(try IrLiteralExpression(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrImport": self = .Import(try IrImport(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrMethod": self = .Method(try IrMethod(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrConstructor": self = .Constructor(try IrConstructor(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrParameter": self = .Parameter(try IrParameter(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrMethodBody": self = .MethodBody(try IrMethodBody(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.EmptyStatement": self = .EmptyStatement(try IrEmptyStatement(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.ExpressionStatement": self = .ExpressionStatement(try IrExpressionStatement(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.VariableDeclarationStatement": self = .VariableDeclarationStatement(try IrVariableDeclarationStatement(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.BlockStatement": self = .BlockStatement(try IrBlockStatement(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IfStatement": self = .IfStatement(try IrIfStatement(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.ForStatement": self = .ForStatement(try IrForStatement(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.WhileStatement": self = .WhileStatement(try IrWhileStatement(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.DoWhileStatement": self = .DoWhileStatement(try IrDoWhileStatement(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.SwitchStatement": self = .SwitchStatement(try IrSwitchStatement(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.SwitchStatement.SwitchStatementCase": self = .SwitchStatementCase(try IrSwitchStatement.IrSwitchStatementCase(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.ReturnStatement": self = .ReturnStatement(try IrReturnStatement(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.BreakStatement": self = .BreakStatement(try IrBreakStatement(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.ContinueStatement": self = .ContinueStatement(try IrContinueStatement(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.ThrowStatement": self = .ThrowStatement(try IrThrowStatement(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.TryCatchStatement": self = .TryCatchStatement(try IrTryCatchStatement(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.TryCatchStatement.TryCatchStatementClause": self = .TryCatchStatementClause(try IrTryCatchStatement.IrTryCatchStatementClause(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.StatementUnknown": self = .StatementUnknown(try IrStatementUnknown(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrTypeReference": self = .TypeReference(try IrTypeReference(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrTypePrimitive": self = .TypePrimitive(try IrTypePrimitive(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrTypeFunction": self = .TypeFunction(try IrTypeFunction(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrTypeArray": self = .TypeArray(try IrTypeArray(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrTypeGeneric": self = .TypeGeneric(try IrTypeGeneric(from: decoder))
        default:
            throw DecodingError.dataCorruptedError(
                forKey: .type,
                in: container,
                debugDescription: "Unknown IrElement kind: \(type)"
            )
        }
    }
    
    public func encode(to encoder: any Encoder) throws {
        switch self {
        case .Class(let e): try e.encode(to: encoder)
        case .SuperClass(let e): try e.encode(to: encoder)
        case .Field(let e): try e.encode(to: encoder)
        case .TypeParameter(let e): try e.encode(to: encoder)
        case .ClassInitializer(let e): try e.encode(to: encoder)
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
        case .Import(let e): try e.encode(to: encoder)
        case .Method(let e): try e.encode(to: encoder)
        case .Constructor(let e): try e.encode(to: encoder)
        case .Parameter(let e): try e.encode(to: encoder)
        case .MethodBody(let e): try e.encode(to: encoder)
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
        case .TypeReference(let e): try e.encode(to: encoder)
        case .TypePrimitive(let e): try e.encode(to: encoder)
        case .TypeFunction(let e): try e.encode(to: encoder)
        case .TypeArray(let e): try e.encode(to: encoder)
        case .TypeGeneric(let e): try e.encode(to: encoder)
        }
    }
    
    public var type: String? {
        switch self {
        case .Class(let e): return e.type
        case .SuperClass(let e): return e.type
        case .Field(let e): return e.type
        case .TypeParameter(let e): return e.type
        case .ClassInitializer(let e): return e.type
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
        case .Import(let e): return e.type
        case .Method(let e): return e.type
        case .Constructor(let e): return e.type
        case .Parameter(let e): return e.type
        case .MethodBody(let e): return e.type
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
        case .TypeReference(let e): return e.type
        case .TypePrimitive(let e): return e.type
        case .TypeFunction(let e): return e.type
        case .TypeArray(let e): return e.type
        case .TypeGeneric(let e): return e.type
        }
    }
    
    public var location: IrLocation? {
        switch self {
        case .Class(let e): return e.location
        case .SuperClass(let e): return e.location
        case .Field(let e): return e.location
        case .TypeParameter(let e): return e.location
        case .ClassInitializer(let e): return e.location
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
        case .Import(let e): return e.location
        case .Method(let e): return e.location
        case .Constructor(let e): return e.location
        case .Parameter(let e): return e.location
        case .MethodBody(let e): return e.location
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
        case .TypeReference(let e): return e.location
        case .TypePrimitive(let e): return e.location
        case .TypeFunction(let e): return e.location
        case .TypeArray(let e): return e.location
        case .TypeGeneric(let e): return e.location
        }
    }
    
    public var annotations: [IrAnnotation] {
        switch self {
        case .Class(let e): return e.annotations
        case .SuperClass(let e): return e.annotations
        case .Field(let e): return e.annotations
        case .TypeParameter(let e): return e.annotations
        case .ClassInitializer(let e): return e.annotations
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
        case .Import(let e): return e.annotations
        case .Method(let e): return e.annotations
        case .Constructor(let e): return e.annotations
        case .Parameter(let e): return e.annotations
        case .MethodBody(let e): return e.annotations
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
        case .TypeReference(let e): return e.annotations
        case .TypePrimitive(let e): return e.annotations
        case .TypeFunction(let e): return e.annotations
        case .TypeArray(let e): return e.annotations
        case .TypeGeneric(let e): return e.annotations
        }
    }
    
    public var languageProperties: [String : AnyCodable] {
        switch self {
        case .Class(let e): return e.languageProperties
        case .SuperClass(let e): return e.languageProperties
        case .Field(let e): return e.languageProperties
        case .TypeParameter(let e): return e.languageProperties
        case .ClassInitializer(let e): return e.languageProperties
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
        case .Import(let e): return e.languageProperties
        case .Method(let e): return e.languageProperties
        case .Constructor(let e): return e.languageProperties
        case .Parameter(let e): return e.languageProperties
        case .MethodBody(let e): return e.languageProperties
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
        case .TypeReference(let e): return e.languageProperties
        case .TypePrimitive(let e): return e.languageProperties
        case .TypeFunction(let e): return e.languageProperties
        case .TypeArray(let e): return e.languageProperties
        case .TypeGeneric(let e): return e.languageProperties
        }
    }
}
