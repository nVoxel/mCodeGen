//
//  IrLocation.swift
//  swift-companion
//
//  Created by nVoxel on 17.06.2025.
//

import Foundation

public struct IrLocation: Codable {
    public let type: String?
    public let filePath: String
    public let lineNumber: Int
    public let columnNumber: Int

    public init(filePath: String, lineNumber: Int, columnNumber: Int) {
        self.type = "com.voxeldev.mcodegen.dsl.ir.IrLocation"
        self.filePath = filePath
        self.lineNumber = lineNumber
        self.columnNumber = columnNumber
    }
}
