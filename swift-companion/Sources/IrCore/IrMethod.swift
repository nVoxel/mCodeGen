//
//  IrMethod.swift
//  swift-companion
//
//  Created by nVoxel on 18.06.2025.
//

import Foundation
import AnyCodable

/**
 * Properties shared by methods and constructors.
 */
public protocol IrCallableProtocol: IrElementProtocol {
    var type: String? { get }
    var name: String { get }
    var returnType: IrType { get }
    var parameters: [IrParameter] { get }
    var typeParameters: [IrTypeParameter] { get }
    var body: IrMethodBody? { get }
    var visibility: IrVisibility { get }
    var isAbstract: Bool { get }
    var isStatic: Bool { get }
    var isOverride: Bool { get }
}

public enum IrCallable: IrCallableProtocol {
    case Method(IrMethod)
    case Constructor(IrConstructor)
    
    private enum CodingKeys: String, CodingKey {
        case type
    }
    
    public init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        let type = try container.decode(String.self, forKey: .type)
        
        switch type {
        case "com.voxeldev.mcodegen.dsl.ir.IrMethod": self = .Method(try IrMethod(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrConstructor": self = .Constructor(try IrConstructor(from: decoder))
        default:
            throw DecodingError.dataCorruptedError(
                forKey: .type,
                in: container,
                debugDescription: "Unknown IrMethod kind: \(type)"
            )
        }
    }
    
    public func encode(to encoder: any Encoder) throws {
        switch self {
        case .Method(let e): try e.encode(to: encoder)
        case .Constructor(let e): try e.encode(to: encoder)
        }
    }
    
    public var type: String? {
        switch self {
        case .Method(let e): return e.type
        case .Constructor(let e): return e.type
        }
    }
    
    public var name: String {
        switch self {
        case .Method(let e): return e.name
        case .Constructor(let e): return e.name
        }
    }
    
    public var returnType: IrType {
        switch self {
        case .Method(let e): return e.returnType
        case .Constructor(let e): return e.returnType
        }
    }
    
    public var parameters: [IrParameter] {
        switch self {
        case .Method(let e): return e.parameters
        case .Constructor(let e): return e.parameters
        }
    }
    
    public var typeParameters: [IrTypeParameter] {
        switch self {
        case .Method(let e): return e.typeParameters
        case .Constructor(let e): return e.typeParameters
        }
    }
    
    public var body: IrMethodBody? {
        switch self {
        case .Method(let e): return e.body
        case .Constructor(let e): return e.body
        }
    }
    
    public var visibility: IrVisibility {
        switch self {
        case .Method(let e): return e.visibility
        case .Constructor(let e): return e.visibility
        }
    }
    
    public var isAbstract: Bool {
        switch self {
        case .Method(let e): return e.isAbstract
        case .Constructor(let e): return e.isAbstract
        }
    }
    
    public var isStatic: Bool {
        switch self {
        case .Method(let e): return e.isStatic
        case .Constructor(let e): return e.isStatic
        }
    }
    
    public var isOverride: Bool {
        switch self {
        case .Method(let e): return e.isOverride
        case .Constructor(let e): return e.isOverride
        }
    }
    
    public var location: IrLocation? {
        switch self {
        case .Method(let e): return e.location
        case .Constructor(let e): return e.location
        }
    }
    
    public var annotations: [IrAnnotation] {
        switch self {
        case .Method(let e): return e.annotations
        case .Constructor(let e): return e.annotations
        }
    }
    
    public var languageProperties: [String : AnyCodable] {
        switch self {
        case .Method(let e): return e.languageProperties
        case .Constructor(let e): return e.languageProperties
        }
    }
    
    
}

/**
 * Represents a method definition.
 */
public struct IrMethod: IrCallableProtocol {
    public let type: String?
    public let name: String
    public let returnType: IrType
    public let parameters: [IrParameter]
    public let typeParameters: [IrTypeParameter]
    public let body: IrMethodBody?
    public let visibility: IrVisibility
    public let isAbstract: Bool
    public let isStatic: Bool
    public let isOverride: Bool
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]
    public init(
        name: String,
        returnType: IrType,
        parameters: [IrParameter],
        typeParameters: [IrTypeParameter] = [],
        body: IrMethodBody? = nil,
        visibility: IrVisibility,
        isAbstract: Bool,
        isStatic: Bool,
        isOverride: Bool,
        location: IrLocation? = nil,
        annotations: [IrAnnotation] = [],
        languageProperties: [String: AnyCodable] = [:]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrMethod"
        self.name = name
        self.returnType = returnType
        self.parameters = parameters
        self.typeParameters = typeParameters
        self.body = body
        self.visibility = visibility
        self.isAbstract = isAbstract
        self.isStatic = isStatic
        self.isOverride = isOverride
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

/**
 * Represents a class constructor.
 */
public struct IrConstructor: IrCallableProtocol {
    public let type: String?
    public let otherConstructorCall: IrMethodCallExpression?
    public let name: String
    public let returnType: IrType
    public let parameters: [IrParameter]
    public let typeParameters: [IrTypeParameter]
    public let body: IrMethodBody?
    public let visibility: IrVisibility
    public let isAbstract: Bool
    public let isStatic: Bool
    public let isOverride: Bool
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]
    
    public init(
        otherConstructorCall: IrMethodCallExpression? = nil,
        name: String,
        returnType: IrType,
        parameters: [IrParameter],
        typeParameters: [IrTypeParameter] = [],
        body: IrMethodBody? = nil,
        visibility: IrVisibility,
        isAbstract: Bool,
        isStatic: Bool,
        isOverride: Bool,
        location: IrLocation? = nil,
        annotations: [IrAnnotation] = [],
        languageProperties: [String: AnyCodable] = [:]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrConstructor"
        self.otherConstructorCall = otherConstructorCall
        self.name = name
        self.returnType = returnType
        self.parameters = parameters
        self.typeParameters = typeParameters
        self.body = body
        self.visibility = visibility
        self.isAbstract = isAbstract
        self.isStatic = isStatic
        self.isOverride = isOverride
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

/**
 * Represents a method parameter (name + type [+ default value]).
 */
public struct IrParameter: IrElementProtocol {
    public let type: String?
    public let name: String
    public let irType: IrType
    public let defaultValue: IrExpression?
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]
    
    public init(
        name: String,
        irType: IrType,
        defaultValue: IrExpression? = nil,
        location: IrLocation? = nil,
        annotations: [IrAnnotation] = [],
        languageProperties: [String: AnyCodable] = [:]) {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrParameter"
            self.name = name
            self.irType = irType
            self.defaultValue = defaultValue
            self.location = location
            self.annotations = annotations
            self.languageProperties = languageProperties
        }
}

/**
 * Container for a method/constructor body (list of statements).
 */
public struct IrMethodBody: IrElementProtocol {
    public let type: String?
    public let statements: [IrStatement]
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]
    
    public init(
        statements: [IrStatement],
        location: IrLocation? = nil,
        annotations: [IrAnnotation] = [],
        languageProperties: [String: AnyCodable] = [:]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrMethodBody"
        self.statements = statements
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}
