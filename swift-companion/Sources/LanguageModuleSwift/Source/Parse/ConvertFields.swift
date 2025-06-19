//
//  ConvertFields.swift
//  swift-companion
//
//  Created by nVoxel on 19.06.2025.
//

import SyntaxSparrow
import AnyCodable

extension SyntaxTree {
    func convertField(swiftVariable: Variable) -> IrField {
        let name = swiftVariable.name
        let type = convertType(type: swiftVariable.type)

        let visibility = if (swiftVariable.isPrivate) {
            swiftVisibilityPrivate()
        } else if (swiftVariable.isPublic) {
            swiftVisibilityPublic()
        } else if (swiftVariable.isInternal) {
            swiftVisibilityInternal()
        } else if (swiftVariable.isOpen) {
            swiftVisibilityOpen()
        } else {
            swiftVisibilityFileprivate()
        }
        
        var languageProperties: [String : AnyCodable] = [:]
        
        if (swiftVariable.containsModifierWithKeyword(.static)) {
            languageProperties["static"] = true
        }
        
        if (swiftVariable.isFinal) {
            languageProperties["final"] = true
        }
        
        // TODO: Swift-specific modifiers
        
        let isMutable = swiftVariable.keyword == "var"
        
        // TODO: think of annotations support in any way

        // TODO: support initializer
        
        return IrField(
            name: name,
            irType: type,
            visibility: visibility,
            isMutable: isMutable,
            languageProperties: languageProperties
        )
    }
}
