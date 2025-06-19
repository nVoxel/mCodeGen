//
//  ConvertSuperclass.swift
//  swift-companion
//
//  Created by nVoxel on 19.06.2025.
//

import SyntaxSparrow

extension SyntaxTree {
    func convertSuperClass(parentTypeName: String) -> IrSuperClass {
        // TODO: use SourceKit to check superClassKind
        let typeArguments = genericArguments(from: parentTypeName).map { typeArgumentName in
            IrType.TypeReference(
                IrTypeReference(referencedClassSimpleName: typeArgumentName, referencedClassQualifiedName: nil, typeParameters: [])
            )
        }
        
        return IrSuperClass(
            superClassSimpleName: parentTypeName,
            kind: IrClassKind.InterfaceClassKind(IrInterfaceClassKind()),
            types: typeArguments
        )
    }
}
