//
//  ConvertFunctions.swift
//  swift-companion
//
//  Created by nVoxel on 19.06.2025.
//

import SyntaxSparrow
import AnyCodable

extension SyntaxTree {
    
    // TODO: create common type of Function, Initializer, to remove method duplication
    
    func convertFunction(swiftFunction: Function) -> IrCallable {
        return IrCallable.Method(
            convertFunctionAsMethod(swiftFunction: swiftFunction)
        )
    }
    
    func convertFunctionAsMethod(swiftFunction: Function) -> IrMethod {
        let name = swiftFunction.identifier
        let returnType = swiftFunction.signature.output == nil ? IrType.TypePrimitive(IrTypePrimitive(
            primitiveType: IrTypePrimitive.PrimitiveType.VoidType(IrTypePrimitive.Void()),
            isNullable: false
        )
        ) : convertType(type: swiftFunction.signature.output!)
        
        let visibility = if (swiftFunction.isPrivate) {
            swiftVisibilityPrivate()
        } else if (swiftFunction.isPublic) {
            swiftVisibilityPublic()
        } else if (swiftFunction.isInternal) {
            swiftVisibilityInternal()
        } else if (swiftFunction.isOpen) {
            swiftVisibilityOpen()
        } else {
            swiftVisibilityFileprivate()
        }
        
        var languageProperties: [String : AnyCodable] = [:]
        
        if (swiftFunction.containsModifierWithKeyword(.static)) {
            languageProperties["static"] = true
        }
        
        if (swiftFunction.isFinal) {
            languageProperties["final"] = true
        }
        
        // TODO: Swift-specific modifiers
        
        let parameters = swiftFunction.signature.input.map { parameter in
            IrParameter(
                name: parameter.secondName ?? parameter.name ?? "Ir:UnnamedParameter",
                irType: convertType(type: parameter.type),
                // defaultValue: TODO: support expressions
            )
        }
        
        let typeParameters = swiftFunction.genericParameters.map { typeParameter in
            convertTypeParameter(typeParameter: typeParameter)
        }
        
        // TODO: method body
        
        return IrMethod(
            name: name,
            returnType: returnType,
            parameters: parameters,
            typeParameters: typeParameters,
            visibility: visibility,
            isAbstract: false,
            isStatic: false,
            isOverride: false,
            languageProperties: languageProperties
        )
    }
    
    func convertInitializer(className: String, swiftInitializer: Initializer) -> IrCallable {
        let name = className
        let returnType = IrType.TypeReference(
            IrTypeReference(
                referencedClassSimpleName: className,
                referencedClassQualifiedName: nil,
                typeParameters: [],
                isNullable: false
            )
        )
        
        let visibility = if (swiftInitializer.isPrivate) {
            swiftVisibilityPrivate()
        } else if (swiftInitializer.isPublic) {
            swiftVisibilityPublic()
        } else if (swiftInitializer.isInternal) {
            swiftVisibilityInternal()
        } else if (swiftInitializer.isOpen) {
            swiftVisibilityOpen()
        } else {
            swiftVisibilityFileprivate()
        }
        
        var languageProperties: [String : AnyCodable] = [:]
        
        if (swiftInitializer.containsModifierWithKeyword(.static)) {
            languageProperties["static"] = true
        }
        
        if (swiftInitializer.isFinal) {
            languageProperties["final"] = true
        }
        
        // TODO: Swift-specific modifiers
        
        let parameters = swiftInitializer.parameters.map { parameter in
            IrParameter(
                name: parameter.secondName ?? parameter.name ?? "Ir:UnnamedParameter",
                irType: convertType(type: parameter.type),
                // defaultValue: TODO: support expressions
            )
        }
        
        let typeParameters = swiftInitializer.genericParameters.map { typeParameter in
            convertTypeParameter(typeParameter: typeParameter)
        }
        
        // TODO: other constructor call + method body
        
        return IrCallable.Constructor(
            IrConstructor(
                name: name,
                returnType: returnType,
                parameters: parameters,
                typeParameters: typeParameters,
                visibility: visibility,
                isAbstract: false,
                isStatic: false,
                isOverride: false,
                languageProperties: languageProperties
            )
        )
    }
}
