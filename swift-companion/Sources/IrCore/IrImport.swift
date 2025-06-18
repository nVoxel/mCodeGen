//
//  IrImport.swift
//  swift-companion
//
//  Created by nVoxel on 17.06.2025.
//

import Foundation
import AnyCodable

/**
 * Represents an import statement in the IR (Intermediate Representation) system.
 * This class contains the information needed to generate an import statement in the target language.
 */
public struct IrImport: IrElementProtocol {
    public let type: String?
    public let path: String
    public let isWildcard: Bool

    // MARK: - IrElement
    public let location: IrLocation?
    public let annotations: [IrAnnotation]
    public let languageProperties: [String: AnyCodable]

    public init(
        path: String,
        isWildcard: Bool,
        location: IrLocation? = nil,
        annotations: [IrAnnotation] = [],
        languageProperties: [String: AnyCodable] = [:]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrImport"
        self.path = path
        self.isWildcard = isWildcard
        self.location = location
        self.annotations = annotations
        self.languageProperties = languageProperties
    }
}
