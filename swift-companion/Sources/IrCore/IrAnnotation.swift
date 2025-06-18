//
//  IrAnnotation.swift
//  swift-companion
//
//  Created by nVoxel on 17.06.2025.
//

import Foundation
import AnyCodable

/**
 * Represents an annotation in the IR (Intermediate Representation) system.
 * Annotations provide additional metadata about code elements.
 */
public struct IrAnnotation: Codable {
    public let type: String?
    public let name: String
    public let parameters: [IrAnnotationParameter]
    /// Arbitrary language-specific key/value pairs.
    public let languageProperties: [String: AnyCodable]

    public init(
        name: String,
        parameters: [IrAnnotationParameter],
        languageProperties: [String: AnyCodable] = [:]
    ) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrAnnotation"
        self.name = name
        self.parameters = parameters
        self.languageProperties = languageProperties
    }
}

/**
 * Represents a parameter of an annotation in the IR (Intermediate Representation) system.
 */
public struct IrAnnotationParameter: Codable {
    public let type: String?
    public let parameterName: String
    public let parameterValue: IrExpression

    public init(parameterName: String, parameterValue: IrExpression) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrAnnotationParameter"
        self.parameterName = parameterName
        self.parameterValue = parameterValue
    }
}
