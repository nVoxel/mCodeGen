//
//  ConvertTypeParameter.swift
//  swift-companion
//
//  Created by nVoxel on 19.06.2025.
//

import SyntaxSparrow

extension SyntaxTree {
    func convertTypeParameter(typeParameter: GenericParameter) -> IrTypeParameter {
        var extendsList = [IrType]()
        if (typeParameter.type != nil) {
            let types = typeParameter.type?.split(separator: ",")
            types?.forEach { type in
                extendsList.append(
                    IrType.TypeReference(
                        IrTypeReference(
                            referencedClassSimpleName: String(type.trimmingCharacters(in: .whitespacesAndNewlines)),
                            referencedClassQualifiedName: nil,
                            typeParameters: [],
                            isNullable: false
                        )
                    )
                )
            }
        }
        
        return IrTypeParameter(name: typeParameter.name, extendsList: extendsList)
    }
}
