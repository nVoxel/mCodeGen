//
//  IrVisibility.swift
//  swift-companion
//
//  Created by nVoxel on 17.06.2025.
//

import Foundation

public protocol IrVisibilityProtocol: Codable {
    var type: String? { get }
    var stringRepresentation: [IrStringRepresentation] { get }
}

/**
 * Represents the visibility modifiers in the IR (Intermediate Representation) system.
 * This protocol defines the different levels of visibility that can be applied to code elements.
 */
public enum IrVisibility: IrVisibilityProtocol {
    case VisibilityPublic(IrVisibilityPublic)
    case VisibilityInternal(IrVisibilityInternal)
    case VisibilityPrivate(IrVisibilityPrivate)
    case VisibilityOpen(IrVisibilityOpen)
    case VisibilityFileprivate(IrVisibilityFileprivate)
    
    private enum CodingKeys: String, CodingKey {
        case type
    }
    
    public init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        let type = try container.decode(String.self, forKey: .type)
        
        switch type {
        case "com.voxeldev.mcodegen.dsl.ir.IrVisibilityPublic": self = .VisibilityPublic(try IrVisibilityPublic(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrVisibilityInternal": self = .VisibilityInternal(try IrVisibilityInternal(from: decoder))
        case "com.voxeldev.mcodegen.dsl.ir.IrVisibilityPrivate": self = .VisibilityPrivate(try IrVisibilityPrivate(from: decoder))
        case "com.voxeldev.mcodegen.dsl.language.swift.ir.IrVisibilityOpen": self = .VisibilityOpen(try IrVisibilityOpen(from: decoder))
        case "com.voxeldev.mcodegen.dsl.language.swift.ir.IrVisibilityFileprivate": self = .VisibilityFileprivate(try IrVisibilityFileprivate(from: decoder))
        default:
            throw DecodingError.dataCorruptedError(
                forKey: .type,
                in: container,
                debugDescription: "Unknown IrVisibility kind: \(type)"
            )
        }
    }
    
    public func encode(to encoder: any Encoder) throws {
        switch self {
        case .VisibilityPublic(let e): try e.encode(to: encoder)
        case .VisibilityInternal(let e): try e.encode(to: encoder)
        case .VisibilityPrivate(let e): try e.encode(to: encoder)
        case .VisibilityOpen(let e): try e.encode(to: encoder)
        case .VisibilityFileprivate(let e): try e.encode(to: encoder)
        }
    }
    
    public var type: String? {
        switch self {
        case .VisibilityPublic(let e): e.type
        case .VisibilityInternal(let e): e.type
        case .VisibilityPrivate(let e): e.type
        case .VisibilityOpen(let e): e.type
        case .VisibilityFileprivate(let e): e.type
        }
    }
    
    public var stringRepresentation: [IrStringRepresentation] {
        switch self {
        case .VisibilityPublic(let e): return e.stringRepresentation
        case .VisibilityInternal(let e): return e.stringRepresentation
        case .VisibilityPrivate(let e): return e.stringRepresentation
        case .VisibilityOpen(let e): return e.stringRepresentation
        case .VisibilityFileprivate(let e): return e.stringRepresentation
        }
    }
}

public struct IrVisibilityPublic: IrVisibilityProtocol {
    public let type: String?
    public let stringRepresentation: [IrStringRepresentation]
    public init(stringRepresentation: [IrStringRepresentation]) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrVisibilityPublic"
        self.stringRepresentation = stringRepresentation
    }
}

public struct IrVisibilityInternal: IrVisibilityProtocol {
    public let type: String?
    public let stringRepresentation: [IrStringRepresentation]
    public init(stringRepresentation: [IrStringRepresentation]) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrVisibilityInternal"
        self.stringRepresentation = stringRepresentation
    }
}

public struct IrVisibilityPrivate: IrVisibilityProtocol {
    public let type: String?
    public let stringRepresentation: [IrStringRepresentation]
    public init(stringRepresentation: [IrStringRepresentation]) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrVisibilityPrivate"
        self.stringRepresentation = stringRepresentation
    }
}

public struct IrVisibilityOpen: IrVisibilityProtocol {
    public let type: String?
    public let stringRepresentation: [IrStringRepresentation]
    public init(stringRepresentation: [IrStringRepresentation]) {
        self.type = "com.voxeldev.mcodegen.dsl.language.swift.ir.IrVisibilityOpen"
        self.stringRepresentation = stringRepresentation
    }
}

public struct IrVisibilityFileprivate: IrVisibilityProtocol {
    public let type: String?
    public let stringRepresentation: [IrStringRepresentation]
    public init(stringRepresentation: [IrStringRepresentation]) {
        self.type = "com.voxeldev.mcodegen.dsl.language.swift.ir.IrVisibilityFileprivate"
        self.stringRepresentation = stringRepresentation
    }
}
