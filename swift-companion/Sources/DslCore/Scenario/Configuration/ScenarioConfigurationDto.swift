//
//  ScenarioConfiguration.swift
//  swift-companion
//
//  Created by nVoxel on 16.06.2025.
//

import Foundation
import AnyCodable

struct ScenarioConfigurationDTO: Codable {
    let sourcesDir: String
    let outputDir: String
    let properties: [ScenarioConfigurationPropertyDTO]
}

struct ScenarioConfigurationPropertyDTO: Codable {
    let language: String
    let propertyName: String
    let propertyValue: AnyCodable
}
