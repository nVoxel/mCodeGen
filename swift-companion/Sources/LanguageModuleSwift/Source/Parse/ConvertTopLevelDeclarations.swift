//
//  ConvertTopLevelDeclarations.swift
//  swift-companion
//
//  Created by nVoxel on 19.06.2025.
//

import SyntaxSparrow

extension SyntaxTree {
    func convertTopLevelDeclarations() -> [IrElement] {
        var irElements = [IrElement]()
        
        var topLevelDeclarations: [any Declaration] = []
        
        topLevelDeclarations.append(contentsOf: classes)
        topLevelDeclarations.append(contentsOf: protocols)
        topLevelDeclarations.append(contentsOf: enumerations)
        topLevelDeclarations.append(contentsOf: structures)
        topLevelDeclarations.append(contentsOf: actors)
        topLevelDeclarations.append(contentsOf: variables)
        topLevelDeclarations.append(contentsOf: functions)
        
        topLevelDeclarations.sort { $0.node.position.utf8Offset < $1.node.position.utf8Offset }
        
        for topLevelDeclaration in topLevelDeclarations {
            switch topLevelDeclaration {
            case let swiftClass as Class: irElements.append(IrElement.Class(convertClass(swiftClass: swiftClass)))
            case let swiftProtocol as ProtocolDecl: irElements.append(IrElement.Class(convertProtocol(swiftProtocol: swiftProtocol)))
            case let swiftEnumeration as Enumeration: irElements.append(IrElement.Class(convertEnum(swiftEnumeration: swiftEnumeration)))
            case let swiftStructure as Structure: irElements.append(IrElement.Class(convertStruct(swiftStructure: swiftStructure)))
            case let swiftActor as Actor: irElements.append(IrElement.Class(convertActor(swiftActor: swiftActor)))
            case let swiftVariable as Variable: irElements.append(IrElement.Field(convertField(swiftVariable: swiftVariable)))
            case let swiftFunction as Function: irElements.append(IrElement.Method(convertFunctionAsMethod(swiftFunction: swiftFunction)))
            default: continue
            }
        }
        
        return irElements
    }
}
