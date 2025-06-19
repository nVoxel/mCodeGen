//
//  ConvertTypes.swift
//  swift-companion
//
//  Created by nVoxel on 19.06.2025.
//

import SyntaxSparrow

nonisolated(unsafe) private let primitiveTypes: [String: IrTypePrimitive.PrimitiveType] = [
    "Void": .VoidType(IrTypePrimitive.Void()), "()": .VoidType(IrTypePrimitive.Void()),
    "Bool": .BooleanType(IrTypePrimitive.Boolean()),
    "Int8": .ByteType(IrTypePrimitive.Byte()),
    "Int16": .ShortType(IrTypePrimitive.Short()),
    "Int32": .IntType(IrTypePrimitive.Int()),
    "Int64": .LongType(IrTypePrimitive.Long()),
    "Int": .LongType(IrTypePrimitive.Long()),
    "Float16": .FloatType(IrTypePrimitive.Float()), "Float": .FloatType(IrTypePrimitive.Float()),
    "Double": .DoubleType(IrTypePrimitive.Double()),
    "Character": .CharType(IrTypePrimitive.Char()), "Unicode.Scalar": .CharType(IrTypePrimitive.Char())
]

extension SyntaxTree {
    func convertType(type: EntityType) -> IrType {
        // TODO: create Swift-specific IrTypes
        // TODO: use SourceKit to check inferred types, etc
        
        switch type {
        case .simple(let rawType):
            let optional = rawType.last == "?"
            let definedType = if (optional) {
                String(rawType.dropLast(1))
            } else {
                rawType
            }
            
            let primitiveType = primitiveTypes[definedType]
            if (primitiveType != nil) {
                return IrType.TypePrimitive(
                    IrTypePrimitive(
                        primitiveType: primitiveType!,
                        isNullable: optional
                    )
                )
            } else {
                let typeParameters = genericArguments(from: definedType).map { typeParameter in
                    IrType.TypeReference(
                        IrTypeReference(
                            referencedClassSimpleName: typeParameter,
                            referencedClassQualifiedName: nil,
                            typeParameters: [] // wrong
                        )
                    )
                }
                
                return IrType.TypeReference(
                    IrTypeReference(
                        referencedClassSimpleName: definedType,
                        referencedClassQualifiedName: nil,
                        typeParameters: typeParameters,
                        isNullable: optional
                    )
                )
            }
        case .array(let t):
            return IrType.TypeArray(
                IrTypeArray(
                    elementType: convertType(type: t.elementType),
                    isNullable: t.isOptional
                )
            )
        case .set(let t):
            return IrType.TypeReference(
                IrTypeReference(
                    referencedClassSimpleName: "Set",
                    referencedClassQualifiedName: nil,
                    typeParameters: [convertType(type: t.elementType)],
                    isNullable: t.isOptional
                )
            )
        case .dictionary(let t):
            return IrType.TypeReference(
                IrTypeReference(
                    referencedClassSimpleName: "Map",
                    referencedClassQualifiedName: nil,
                    typeParameters: [convertType(type: t.keyType), convertType(type: t.valueType)],
                    isNullable: t.isOptional
                )
            )
        case .tuple(let t):
            if (t.elements.count < 2 || t.elements.count > 3) {
                preconditionFailure("Currently not supported tuple")
            }
            
            if (t.elements.count == 2) {
                return IrType.TypeReference(
                    IrTypeReference(
                        referencedClassSimpleName: "Pair",
                        referencedClassQualifiedName: nil,
                        typeParameters: t.elements.map { element in convertType(type: element.type) },
                        isNullable: t.isOptional
                    )
                )
            } else {
                return IrType.TypeReference(
                    IrTypeReference(
                        referencedClassSimpleName: "Triple",
                        referencedClassQualifiedName: nil,
                        typeParameters: t.elements.map { element in convertType(type: element.type) },
                        isNullable: t.isOptional
                    )
                )
            }
        case .closure(let t):
            return IrType.TypeFunction(
                IrTypeFunction(
                    parameterTypes: convertClosureType(type: t.input),
                    returnType: convertType(type: t.output),
                    isNullable: t.isOptional
                )
            )
        case .result(_):
            fatalError("Currently not supported type")
        case .void(_, let optional):
            return IrType.TypePrimitive(
                IrTypePrimitive(
                    primitiveType: IrTypePrimitive.PrimitiveType.VoidType(IrTypePrimitive.Void()),
                    isNullable: optional
                )
            )
        case .empty:
            return IrType.TypePrimitive(
                IrTypePrimitive(
                    primitiveType: IrTypePrimitive.PrimitiveType.VoidType(IrTypePrimitive.Void()),
                    isNullable: false
                )
            )
        }
    }
    
    private func convertClosureType(type: EntityType) -> [IrType] {
        var innerTypes = [IrType]()
        
        switch type {
        case .tuple(let t):
            t.elements.forEach { element in
                innerTypes.append(convertType(type: element.type))
            }
        case .void, .empty:
            return innerTypes
        default:
            innerTypes.append(convertType(type: type))
        }
        
        return innerTypes
    }
    
    func genericArguments(from typeString: String) -> [String] {
        guard
            let lt = typeString.firstIndex(of: "<"),
            let gt = typeString.lastIndex(of: ">"),
            lt < gt
        else { return [] }

        // slice the substring inside “… < … > …”
        let inner = typeString[typeString.index(after: lt)..<gt]

        // split on commas, trim whitespace
        return inner
            .split(separator: ",")
            .map { $0.trimmingCharacters(in: .whitespacesAndNewlines) }
    }
}

