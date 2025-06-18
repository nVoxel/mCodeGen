//
//  IrFile.swift
//  swift-companion
//
//  Created by nVoxel on 17.06.2025.
//

import Foundation
import AnyCodable

/**
 * Represents a source file in the IR (Intermediate Representation) system.
 * This class contains all the necessary information to generate a source file,
 * including its package name, imports, and class definitions.
 */
public struct IrFile: Codable {
    public let type: String?
    public let name: String
    public let imports: [IrImport]
    public let declarations: [IrElement]
    public let languageProperties: [String: AnyCodable]

    public init(
        name: String,
        imports: [IrImport],
        declarations: [IrElement],
        languageProperties: [String: AnyCodable] = [:]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrFile"
        self.name = name
        self.imports = imports
        self.declarations = declarations
        self.languageProperties = languageProperties
    }
}
