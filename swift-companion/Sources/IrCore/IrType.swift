//
//  IrType.swift
//  swift-companion
//
//  Created by nVoxel on 17.06.2025.
//

import Foundation
import AnyCodable

public protocol IrTypeProtocol: IrElementProtocol {
    var type: String? { get }
    /// Indicates whether the type is nullable (`T?` in Kotlin, `T?` in Swift).
    var isNullable: Bool { get }
}

/**
 * Base protocol for all types in the IR (Intermediate Representation) system.
 * This class represents the type system used in the code generation process.
 */
public indirect enum IrType: IrTypeProtocol {
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
        case "com.voxeldev.mcodegen.dsl.ir.IrTypeReference": self = .TypeReference(try IrTypeReference(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrTypePrimitive": self = .TypePrimitive(try IrTypePrimitive(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrTypeFunction": self = .TypeFunction(try IrTypeFunction(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrTypeArray": self = .TypeArray(try IrTypeArray(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrTypeGeneric": self = .TypeGeneric(try IrTypeGeneric(from: decoder))
        default:
            throw DecodingError.dataCorruptedError(
                forKey: .type,
                in: container,
                debugDescription: "Unknown IrType kind: \(type)"
            )
        }
    }
    
    public func encode(to encoder: any Encoder) throws {
        switch self {
        case .TypeReference(let e): try e.encode(to: encoder)
        case .TypePrimitive(let e): try e.encode(to: encoder)
        case .TypeFunction(let e): try e.encode(to: encoder)
        case .TypeArray(let e): try e.encode(to: encoder)
        case .TypeGeneric(let e): try e.encode(to: encoder)
        }
    }
    
    public var type: String? {
        switch self {
        case .TypeReference(let e): return e.type
        case .TypePrimitive(let e): return e.type
        case .TypeFunction(let e): return e.type
        case .TypeArray(let e): return e.type
        case .TypeGeneric(let e): return e.type
        }
    }
    
    public var isNullable: Bool {
        switch self {
        case .TypeReference(let e): return e.isNullable
        case .TypePrimitive(let e): return e.isNullable
        case .TypeFunction(let e): return e.isNullable
        case .TypeArray(let e): return e.isNullable
        case .TypeGeneric(let e): return e.isNullable
        }
    }
    
    public var location: IrLocation? {
        switch self {
        case .TypeReference(let e): return e.location
        case .TypePrimitive(let e): return e.location
        case .TypeFunction(let e): return e.location
        case .TypeArray(let e): return e.location
        case .TypeGeneric(let e): return e.location
        }
    }
    
    public var annotations: [IrAnnotation] {
        switch self {
        case .TypeReference(let e): return e.annotations
        case .TypePrimitive(let e): return e.annotations
        case .TypeFunction(let e): return e.annotations
        case .TypeArray(let e): return e.annotations
        case .TypeGeneric(let e): return e.annotations
        }
    }
    
    public var languageProperties: [String : AnyCodable] {
        switch self {
        case .TypeReference(let e): return e.languageProperties
        case .TypePrimitive(let e): return e.languageProperties
        case .TypeFunction(let e): return e.languageProperties
        case .TypeArray(let e): return e.languageProperties
        case .TypeGeneric(let e): return e.languageProperties
        }
    }
}

public struct IrTypeReference: IrTypeProtocol {
    public let type: String?
    public let referencedClassSimpleName: String
    public let referencedClassQualifiedName: String?
    public let typeParameters: [IrType]

    // MARK: – IrType
    public let isNullable: Bool
    // MARK: – IrElement
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]

    public init(
        referencedClassSimpleName: String,
        referencedClassQualifiedName: String?,
        typeParameters: [IrType],
        isNullable: Bool = true,
        location: IrLocation? = nil,
        annotations: [IrAnnotation] = [],
        languageProperties: [String: AnyCodable] = [:]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrTypeReference"
        self.referencedClassSimpleName = referencedClassSimpleName
        self.referencedClassQualifiedName = referencedClassQualifiedName
        self.typeParameters = typeParameters
        self.isNullable = isNullable
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

public struct IrTypePrimitive: IrTypeProtocol {
    
    public protocol PrimitiveTypeProtocol : Codable {
        var type: String? { get }
    }
    
    public enum PrimitiveType : PrimitiveTypeProtocol {
        case VoidType(IrTypePrimitive.Void)
        case BooleanType(IrTypePrimitive.Boolean)
        case ByteType(IrTypePrimitive.Byte)
        case ShortType(IrTypePrimitive.Short)
        case IntType(IrTypePrimitive.Int)
        case LongType(IrTypePrimitive.Long)
        case CharType(IrTypePrimitive.Char)
        case FloatType(IrTypePrimitive.Float)
        case DoubleType(IrTypePrimitive.Double)
        
        private enum CodingKeys: String, CodingKey {
            case type
        }
        
        public init(from decoder: Decoder) throws {
            let container = try decoder.container(keyedBy: CodingKeys.self)
            let type = try container.decode(String.self, forKey: .type)
            
            switch type {
            case "com.voxeldev.mcodegen.dsl.ir.IrTypePrimitive.PrimitiveType.Void": self = .VoidType(try IrTypePrimitive.Void(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrTypePrimitive.PrimitiveType.Boolean": self = .BooleanType(try IrTypePrimitive.Boolean(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrTypePrimitive.PrimitiveType.Byte": self = .ByteType(try IrTypePrimitive.Byte(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrTypePrimitive.PrimitiveType.Short": self = .ShortType(try IrTypePrimitive.Short(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrTypePrimitive.PrimitiveType.Int": self = .IntType(try IrTypePrimitive.Int(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrTypePrimitive.PrimitiveType.Long": self = .LongType(try IrTypePrimitive.Long(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrTypePrimitive.PrimitiveType.Char": self = .CharType(try IrTypePrimitive.Char(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrTypePrimitive.PrimitiveType.Float": self = .FloatType(try IrTypePrimitive.Float(from: decoder))
            case "com.voxeldev.mcodegen.dsl.ir.IrTypePrimitive.PrimitiveType.Double": self = .DoubleType(try IrTypePrimitive.Double(from: decoder))
            default:
                throw DecodingError.dataCorruptedError(
                    forKey: .type,
                    in: container,
                    debugDescription: "Unknown PrimitiveType kind: \(type)"
                )
            }
        }
        
        public func encode(to encoder: any Encoder) throws {
            switch self {
            case .VoidType(let e): try e.encode(to: encoder)
            case .BooleanType(let e): try e.encode(to: encoder)
            case .ByteType(let e): try e.encode(to: encoder)
            case .ShortType(let e): try e.encode(to: encoder)
            case .IntType(let e): try e.encode(to: encoder)
            case .LongType(let e): try e.encode(to: encoder)
            case .CharType(let e): try e.encode(to: encoder)
            case .FloatType(let e): try e.encode(to: encoder)
            case .DoubleType(let e): try e.encode(to: encoder)
            }
        }
        
        public var type: String? {
            switch self {
            case .VoidType(let e): e.type
            case .BooleanType(let e): e.type
            case .ByteType(let e): e.type
            case .ShortType(let e): e.type
            case .IntType(let e): e.type
            case .LongType(let e): e.type
            case .CharType(let e): e.type
            case .FloatType(let e): e.type
            case .DoubleType(let e): e.type
            }
        }
    }
    
    public struct Void : PrimitiveTypeProtocol {
        public let type: String?
        
        init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrTypePrimitive.PrimitiveType.Void"
        }
    }
    
    public struct Boolean : PrimitiveTypeProtocol {
        public let type: String?
        
        init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrTypePrimitive.PrimitiveType.Boolean"
        }
    }
    
    public struct Byte : PrimitiveTypeProtocol {
        public let type: String?
        
        init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrTypePrimitive.PrimitiveType.Byte"
        }
    }
    
    public struct Short : PrimitiveTypeProtocol {
        public let type: String?
        
        init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrTypePrimitive.PrimitiveType.Short"
        }
    }
    
    public struct Int : PrimitiveTypeProtocol {
        public let type: String?
        
        init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrTypePrimitive.PrimitiveType.Int"
        }
    }
    
    public struct Long : PrimitiveTypeProtocol {
        public let type: String?
        
        init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrTypePrimitive.PrimitiveType.Long"
        }
    }
    
    public struct Char : PrimitiveTypeProtocol {
        public let type: String?
        
        init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrTypePrimitive.PrimitiveType.Char"
        }
    }
    
    public struct Float : PrimitiveTypeProtocol {
        public let type: String?
        
        init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrTypePrimitive.PrimitiveType.Float"
        }
    }
    
    public struct Double : PrimitiveTypeProtocol {
        public let type: String?
        
        init() {
            self.type = "com.voxeldev.mcodegen.dsl.ir.IrTypePrimitive.PrimitiveType.Double"
        }
    }
    
    public let type: String?

    public let primitiveType: PrimitiveType

    // MARK: – IrType
    public let isNullable: Bool
    // MARK: – IrElement
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]

    public init(
        primitiveType: PrimitiveType,
        isNullable: Bool = true,
        location: IrLocation? = nil,
        annotations: [IrAnnotation] = [],
        languageProperties: [String: AnyCodable] = [:]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrTypePrimitive"
        self.primitiveType = primitiveType
        self.isNullable = isNullable
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

public struct IrTypeFunction: IrTypeProtocol {
    public let type: String?
    
    public let parameterTypes: [IrType]
    public let returnType: IrType

    // IrType
    public let isNullable: Bool
    // IrElement
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]

    public init(
        parameterTypes: [IrType],
        returnType: IrType,
        isNullable: Bool = true,
        location: IrLocation? = nil,
        annotations: [IrAnnotation] = [],
        languageProperties: [String: AnyCodable] = [:]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrTypeFunction"
        self.parameterTypes = parameterTypes
        self.returnType = returnType
        self.isNullable = isNullable
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

public struct IrTypeArray: IrTypeProtocol {
    public let type: String?
    
    public let elementType: IrType

    // IrType
    public let isNullable: Bool
    // IrElement
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]

    public init(
        elementType: IrType,
        isNullable: Bool = true,
        location: IrLocation? = nil,
        annotations: [IrAnnotation] = [],
        languageProperties: [String: AnyCodable] = [:]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrTypeArray"
        self.elementType = elementType
        self.isNullable = isNullable
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

public struct IrTypeGeneric: IrTypeProtocol {
    public let type: String?
    
    public let name: String

    // IrType
    public let isNullable: Bool
    // IrElement
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]

    public init(
        name: String,
        isNullable: Bool = true,
        location: IrLocation? = nil,
        annotations: [IrAnnotation] = [],
        languageProperties: [String: AnyCodable] = [:]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrTypeGeneric"
        self.name = name
        self.isNullable = isNullable
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}
