//
//  IrClass.swift
//  swift-companion
//
//  Created by nVoxel on 18.06.2025.
//

import Foundation
import AnyCodable

/**
 * Represents a class in the IR (Intermediate Representation) system.
 * This class contains all the necessary information to generate a class definition,
 * including its name, visibility, methods, and other properties.
 */
public struct IrClass: IrElementProtocol {
    public let type: String?
    public let qualifiedName: String?
    public let simpleName: String
    public let kind: IrClassKind
    public let visibility: IrVisibility
    public let typeParameters: [IrTypeParameter]
    public let superClasses: [IrSuperClass]
    public let fields: [IrField]
    public let methods: [IrCallable]
    public let initializers: [IrClassInitializer]
    public let nestedClasses: [IrClass]

    // IrElement
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]

    public init(
        qualifiedName: String? = nil,
        simpleName: String,
        kind: IrClassKind,
        visibility: IrVisibility,
        typeParameters: [IrTypeParameter] = [],
        superClasses: [IrSuperClass] = [],
        fields: [IrField] = [],
        methods: [IrCallable] = [],
        initializers: [IrClassInitializer] = [],
        nestedClasses: [IrClass] = [],
        location: IrLocation? = nil,
        annotations: [IrAnnotation] = [],
        languageProperties: [String : AnyCodable] = [:]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrClass"
        self.qualifiedName = qualifiedName
        self.simpleName = simpleName
        self.kind = kind
        self.visibility = visibility
        self.typeParameters = typeParameters
        self.superClasses = superClasses
        self.fields = fields
        self.methods = methods
        self.initializers = initializers
        self.nestedClasses = nestedClasses
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

public protocol IrClassKindProtocol : Codable {
    var type: String? { get }
}

public enum IrClassKind : IrClassKindProtocol {
    case ClassClassKind(IrClassClassKind)
    case InterfaceClassKind(IrInterfaceClassKind)
    case EnumClassKind(IrEnumClassKind)
    case AnnotationClassKind(IrAnnotationClassKind)
    case ActorClassKind(IrActorClassKind)
    case StructClassKind(IrStructClassKind)
    
    private enum CodingKeys: String, CodingKey {
        case type
    }
    
    public init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        let type = try container.decode(String.self, forKey: .type)
        
        switch type {
        case "com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrClassClassKind": self = .ClassClassKind(try IrClassClassKind(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrInterfaceClassKind": self = .InterfaceClassKind(try IrInterfaceClassKind(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrEnumClassKind": self = .EnumClassKind(try IrEnumClassKind(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrAnnotationClassKind": self = .AnnotationClassKind(try IrAnnotationClassKind(from: decoder))
        case "com.voxeldev.mcodegen.dsl.language.swift.ir.IrActorClassKind": self = .ActorClassKind(try IrActorClassKind(from: decoder))
        case "com.voxeldev.mcodegen.dsl.language.swift.ir.IrStructClassKind": self = .StructClassKind(try IrStructClassKind(from: decoder))
        default:
            throw DecodingError.dataCorruptedError(
                forKey: .type,
                in: container,
                debugDescription: "Unknown IrClassKind kind: \(type)"
            )
        }
    }
    
    public func encode(to encoder: any Encoder) throws {
        switch self {
        case .ClassClassKind(let e): try e.encode(to: encoder)
        case .InterfaceClassKind(let e): try e.encode(to: encoder)
        case .EnumClassKind(let e): try e.encode(to: encoder)
        case .AnnotationClassKind(let e): try e.encode(to: encoder)
        case .ActorClassKind(let e): try e.encode(to: encoder)
        case .StructClassKind(let e): try e.encode(to: encoder)
        }
    }
    
    public var type: String? {
        switch self {
        case .ClassClassKind(let e): e.type
        case .InterfaceClassKind(let e): e.type
        case .EnumClassKind(let e): e.type
        case .AnnotationClassKind(let e): e.type
        case .ActorClassKind(let e): e.type
        case .StructClassKind(let e): e.type
        }
    }
}

public struct IrClassClassKind : IrClassKindProtocol {
    public let type: String?
    
    init() {
        type = "com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrClassClassKind"
    }
}

public struct IrInterfaceClassKind : IrClassKindProtocol {
    public let type: String?
    
    init() {
        type = "com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrInterfaceClassKind"
    }
}

public struct IrEnumClassKind : IrClassKindProtocol {
    public let type: String?
    
    init() {
        type = "com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrEnumClassKind"
    }
}

public struct IrAnnotationClassKind : IrClassKindProtocol {
    public let type: String?
    
    init() {
        type = "com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrAnnotationClassKind"
    }
}

public struct IrActorClassKind : IrClassKindProtocol {
    public let type: String?
    
    init() {
        type = "com.voxeldev.mcodegen.dsl.language.swift.ir.IrActorClassKind"
    }
}

public struct IrStructClassKind : IrClassKindProtocol {
    public let type: String?
    
    init() {
        type = "com.voxeldev.mcodegen.dsl.language.swift.ir.IrStructClassKind"
    }
}

public struct IrSuperClass: IrElementProtocol {
    public let type: String?
    public let superClassSimpleName: String
    public let superClassQualifiedName: String?
    public let kind: IrClassKind
    public let types: [IrType]

    // IrElement
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]

    public init(
        superClassSimpleName: String,
        superClassQualifiedName: String? = nil,
        kind: IrClassKind,
        types: [IrType] = [],
        location: IrLocation? = nil,
        annotations: [IrAnnotation] = [],
        languageProperties: [String : AnyCodable] = [:]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrSuperClass"
        self.superClassSimpleName = superClassSimpleName
        self.superClassQualifiedName = superClassQualifiedName
        self.kind = kind
        self.types = types
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

public struct IrField: IrElementProtocol {
    public let type: String?
    public let name: String
    public let irType: IrType
    public let visibility: IrVisibility
    public let isMutable: Bool
    public let initializer: IrStatement?

    // IrElement
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]

    public init(
        name: String,
        irType: IrType,
        visibility: IrVisibility,
        isMutable: Bool,
        initializer: IrStatement? = nil,
        location: IrLocation? = nil,
        annotations: [IrAnnotation] = [],
        languageProperties: [String : AnyCodable] = [:]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrField"
        self.name = name
        self.irType = irType
        self.visibility = visibility
        self.isMutable = isMutable
        self.initializer = initializer
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

public struct IrTypeParameter: IrElementProtocol {
    public let type: String?
    public let name: String
    public let extendsList: [IrType]

    // IrElement
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]

    public init(
        name: String,
        extendsList: [IrType] = [],
        location: IrLocation? = nil,
        annotations: [IrAnnotation] = [],
        languageProperties: [String : AnyCodable] = [:]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrTypeParameter"
        self.name = name
        self.extendsList = extendsList
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}

public protocol IrClassInitializerKindProtocol : Codable {
    var type: String? { get }
}

public enum IrClassInitializerKind : IrClassInitializerKindProtocol {
    case InstanceClassInitializerKind(IrInstanceClassInitializerKind)
    case StaticClassInitializerKind(IrStaticClassInitializerKind)
    
    private enum CodingKeys: String, CodingKey {
        case type
    }
    
    public init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        let type = try container.decode(String.self, forKey: .type)
        
        switch type {
        case "com.voxeldev.mcodegen.dsl.ir.IrClassInitializer.IrInstanceClassInitializerKind": self = .InstanceClassInitializerKind(try IrInstanceClassInitializerKind(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrClassInitializer.IrStaticClassInitializerKind": self = .StaticClassInitializerKind(try IrStaticClassInitializerKind(from: decoder))
        default:
            throw DecodingError.dataCorruptedError(
                forKey: .type,
                in: container,
                debugDescription: "Unknown IrClassInitializerKind kind: \(type)"
            )
        }
    }
    
    public func encode(to encoder: any Encoder) throws {
        switch self {
        case .InstanceClassInitializerKind(let e): try e.encode(to: encoder)
        case .StaticClassInitializerKind(let e): try e.encode(to: encoder)
        }
    }
    
    public var type: String? {
        switch self {
        case .InstanceClassInitializerKind(let e): e.type
        case .StaticClassInitializerKind(let e): e.type
        }
    }
}

public struct IrInstanceClassInitializerKind : IrClassInitializerKindProtocol {
    public let type: String?
    public init() {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrClassInitializer.IrInstanceClassInitializerKind"
    }
}

public struct IrStaticClassInitializerKind : IrClassInitializerKindProtocol {
    public let type: String?
    public init() {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrClassInitializer.IrStaticClassInitializerKind"
    }
}

public struct IrClassInitializer: IrElementProtocol {
    public let type: String?
    public let kind: IrClassInitializerKind
    public let body: IrMethodBody?

    // IrElement
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]

    public init(
        kind: IrClassInitializerKind,
        body: IrMethodBody? = nil,
        location: IrLocation? = nil,
        annotations: [IrAnnotation] = [],
        languageProperties: [String : AnyCodable] = [:]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrClassInitializer"
        self.kind = kind
        self.body = body
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}
