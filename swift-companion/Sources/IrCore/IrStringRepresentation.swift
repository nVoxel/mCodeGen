//
//  IrStringRepresentation.swift
//  swift-companion
//
//  Created by nVoxel on 17.06.2025.
//

import Foundation

/**
 * Represents a string representation of an IR (Intermediate Representation) element.
 * This class is used to store how an IR element should be represented as a string
 * in the generated code.
 */
public struct IrStringRepresentation: Codable {
    public let type: String?
    public let language: String
    public let representation: String

    public init(language: String, representation: String) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrStringRepresentation"
        self.language = language
        self.representation = representation
    }
}
