//
//  Main.swift
//  swift-companion
//
//  Created by nVoxel on 17.06.2025.
//

import Foundation
import ArgumentParser
import SwiftParser
import SwiftSyntax
import SourceKittenFramework
import SyntaxSparrow

public let languageName = "swift"

@main
struct swift_companion: ParsableCommand {
    
    @Option(name: [.customLong("scope", withSingleDash: true), .long], help: "Path to the JSON-serialized ScenarioScope file")
    var scenarioScopePath: String = "/var/folders/s1/vdp7mbc561nd8bkf8_0b8t3h0000gn/T/scenario-scope-17896688055966376106.json"
    
    @Option(name: [.customLong("source", withSingleDash: true), .long], help: "Path to the source code file")
    var sourcePath: String = "swift/test.swift"
    
    mutating func run() throws {
        let data = try Data(contentsOf: URL(fileURLWithPath: scenarioScopePath))
        let dto = try JSONDecoder().decode(ScenarioScopeDTO.self, from: data)
        
        let ir = try getIrFromFile(dto: dto)
        
        let irJson = String(data: try! JSONEncoder().encode(ir), encoding: .utf8)!
        print(irJson)
    }
    
    private func getIrFromFile(dto: ScenarioScopeDTO) throws -> IrFile {
        let sourcesDir = URL(fileURLWithPath: dto.scenarioConfiguration.sourcesDir, isDirectory: true)
        let pathToFile = sourcesDir.appending(path: sourcePath)
        let fileName = pathToFile.lastPathComponent
        
        let source = try String(contentsOf: pathToFile, encoding: .utf8)
        let syntaxTree = SyntaxTree(viewMode: .sourceAccurate, sourceBuffer: source)
        
        syntaxTree.collectChildren()
        
        return IrFile(
            name: fileName,
            imports: [],
            declarations: syntaxTree.convertTopLevelDeclarations()
        )
    }
}
