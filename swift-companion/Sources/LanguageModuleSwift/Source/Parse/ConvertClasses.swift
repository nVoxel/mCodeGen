//
//  ConvertClasses.swift
//  swift-companion
//
//  Created by nVoxel on 19.06.2025.
//

import SyntaxSparrow
import AnyCodable

extension SyntaxTree {
    
    // TODO: create common type of Class, Protocol, ..., to remove method duplication
    
    func convertClass(swiftClass: Class) -> IrClass {
        let simpleName = swiftClass.name
        
        let kind = IrClassKind.ClassClassKind(IrClassClassKind())
        
        let visibility = if(swiftClass.isPrivate) {
            swiftVisibilityPrivate()
        } else if (swiftClass.isPublic) {
            swiftVisibilityPublic()
        } else if (swiftClass.isOpen) {
            swiftVisibilityOpen()
        } else if (swiftClass.isFilePrivate) {
            swiftVisibilityFileprivate()
        } else {
            swiftVisibilityInternal()
        }
        
        var languageProperties: [String : AnyCodable] = [:]
        
        if (swiftClass.isFinal) {
            languageProperties["final"] = true
        }
        
        // TODO: annotations?
        
        // TODO: generic requirements
        
        let typeParameters = swiftClass.genericParameters.map { typeParameter in
            convertTypeParameter(typeParameter: typeParameter)
        }
        
        let superClasses = swiftClass.inheritance.map { parentTypeName in
            convertSuperClass(parentTypeName: parentTypeName)
        }
        
        let fields = swiftClass.variables.map { swiftVariable in
            convertField(swiftVariable: swiftVariable)
        }
        
        let methods = swiftClass.functions.map { swiftFunction in
            convertFunction(swiftFunction: swiftFunction)
        } + swiftClass.initializers.map { swiftInitializer in
            convertInitializer(className: simpleName, swiftInitializer: swiftInitializer)
        }
        
        // TODO: support initializers
        
        let nestedClasses =
        swiftClass.classes.map { convertClass(swiftClass: $0) }
        + swiftClass.protocols.map { convertProtocol(swiftProtocol: $0) }
        + swiftClass.enumerations.map { convertEnum(swiftEnumeration: $0) }
        + swiftClass.structures.map { convertStruct(swiftStructure: $0) }
        + swiftClass.actors.map { convertActor(swiftActor: $0) }
        
        return IrClass(
            simpleName: simpleName,
            kind: kind,
            visibility: visibility,
            typeParameters: typeParameters,
            superClasses: superClasses,
            fields: fields,
            methods: methods,
            nestedClasses: nestedClasses,
            languageProperties: languageProperties
        )
    }
    
    func convertProtocol(swiftProtocol: ProtocolDecl) -> IrClass {
        let simpleName = swiftProtocol.name
        
        let kind = IrClassKind.InterfaceClassKind(IrInterfaceClassKind())
        
        let visibility = if(swiftProtocol.isPrivate) {
            swiftVisibilityPrivate()
        } else if (swiftProtocol.isPublic) {
            swiftVisibilityPublic()
        } else if (swiftProtocol.isOpen) {
            swiftVisibilityOpen()
        } else if (swiftProtocol.isFilePrivate) {
            swiftVisibilityFileprivate()
        } else {
            swiftVisibilityInternal()
        }
        
        var languageProperties: [String : AnyCodable] = [:]
        
        if (swiftProtocol.isFinal) {
            languageProperties["final"] = true
        }
        
        // TODO: annotations?
        
        // TODO: generic requirements
        
        var typeParameters = [IrTypeParameter]()
        
        swiftProtocol.associatedTypes.forEach { typeParameter in
            typeParameters.append(
                IrTypeParameter(name: typeParameter.name)
            )
        }
        
        let superClasses = swiftProtocol.inheritance.map { parentTypeName in
            convertSuperClass(parentTypeName: parentTypeName)
        }
        
        let fields = swiftProtocol.variables.map { swiftVariable in
            convertField(swiftVariable: swiftVariable)
        }
        
        let methods = swiftProtocol.functions.map { swiftFunction in
            convertFunction(swiftFunction: swiftFunction)
        } + swiftProtocol.initializers.map { swiftInitializer in
            convertInitializer(className: simpleName, swiftInitializer: swiftInitializer)
        }
        
        // TODO: support initializers
        
        let nestedClasses =
        swiftProtocol.classes.map { convertClass(swiftClass: $0) }
        + swiftProtocol.protocols.map { convertProtocol(swiftProtocol: $0) }
        + swiftProtocol.enumerations.map { convertEnum(swiftEnumeration: $0) }
        + swiftProtocol.structures.map { convertStruct(swiftStructure: $0) }
        + swiftProtocol.actors.map { convertActor(swiftActor: $0) }
        
        return IrClass(
            simpleName: simpleName,
            kind: kind,
            visibility: visibility,
            typeParameters: typeParameters,
            superClasses: superClasses,
            fields: fields,
            methods: methods,
            nestedClasses: nestedClasses,
            languageProperties: languageProperties
        )
    }
    
    func convertEnum(swiftEnumeration: Enumeration) -> IrClass {
        let simpleName = swiftEnumeration.name
        
        let kind = IrClassKind.EnumClassKind(IrEnumClassKind())
        
        let visibility = if(swiftEnumeration.isPrivate) {
            swiftVisibilityPrivate()
        } else if (swiftEnumeration.isPublic) {
            swiftVisibilityPublic()
        } else if (swiftEnumeration.isOpen) {
            swiftVisibilityOpen()
        } else if (swiftEnumeration.isFilePrivate) {
            swiftVisibilityFileprivate()
        } else {
            swiftVisibilityInternal()
        }
        
        var languageProperties: [String : AnyCodable] = [:]
        
        if (swiftEnumeration.isFinal) {
            languageProperties["final"] = true
        }
        
        // TODO: annotations?
        
        // TODO: generic requirements
        
        let typeParameters = swiftEnumeration.genericParameters.map { typeParameter in
            convertTypeParameter(typeParameter: typeParameter)
        }
        
        let superClasses = swiftEnumeration.inheritance.map { parentTypeName in
            convertSuperClass(parentTypeName: parentTypeName)
        }
        
        let fields = swiftEnumeration.variables.map { swiftVariable in
            convertField(swiftVariable: swiftVariable)
        }
        
        let methods = swiftEnumeration.functions.map { swiftFunction in
            convertFunction(swiftFunction: swiftFunction)
        } + swiftEnumeration.initializers.map { swiftInitializer in
            convertInitializer(className: simpleName, swiftInitializer: swiftInitializer)
        }
        
        // TODO: support initializers
        
        let nestedClasses =
        swiftEnumeration.classes.map { convertClass(swiftClass: $0) }
        + swiftEnumeration.protocols.map { convertProtocol(swiftProtocol: $0) }
        + swiftEnumeration.enumerations.map { convertEnum(swiftEnumeration: $0) }
        + swiftEnumeration.structures.map { convertStruct(swiftStructure: $0) }
        + swiftEnumeration.actors.map { convertActor(swiftActor: $0) }
        
        return IrClass(
            simpleName: simpleName,
            kind: kind,
            visibility: visibility,
            typeParameters: typeParameters,
            superClasses: superClasses,
            fields: fields,
            methods: methods,
            nestedClasses: nestedClasses,
            languageProperties: languageProperties
        )
    }
    
    func convertStruct(swiftStructure: Structure) -> IrClass {
        let simpleName = swiftStructure.name
        
        let kind = IrClassKind.StructClassKind(IrStructClassKind())
        
        let visibility = if(swiftStructure.isPrivate) {
            swiftVisibilityPrivate()
        } else if (swiftStructure.isPublic) {
            swiftVisibilityPublic()
        } else if (swiftStructure.isOpen) {
            swiftVisibilityOpen()
        } else if (swiftStructure.isFilePrivate) {
            swiftVisibilityFileprivate()
        } else {
            swiftVisibilityInternal()
        }
        
        var languageProperties: [String : AnyCodable] = [:]
        
        if (swiftStructure.isFinal) {
            languageProperties["final"] = true
        }
        
        // TODO: annotations?
        
        // TODO: generic requirements
        
        let typeParameters = swiftStructure.genericParameters.map { typeParameter in
            convertTypeParameter(typeParameter: typeParameter)
        }
        
        let superClasses = swiftStructure.inheritance.map { parentTypeName in
            convertSuperClass(parentTypeName: parentTypeName)
        }
        
        let fields = swiftStructure.variables.map { swiftVariable in
            convertField(swiftVariable: swiftVariable)
        }
        
        let methods = swiftStructure.functions.map { swiftFunction in
            convertFunction(swiftFunction: swiftFunction)
        } + swiftStructure.initializers.map { swiftInitializer in
            convertInitializer(className: simpleName, swiftInitializer: swiftInitializer)
        }
        
        // TODO: support initializers
        
        let nestedClasses =
        swiftStructure.classes.map { convertClass(swiftClass: $0) }
        + swiftStructure.protocols.map { convertProtocol(swiftProtocol: $0) }
        + swiftStructure.enumerations.map { convertEnum(swiftEnumeration: $0) }
        + swiftStructure.structures.map { convertStruct(swiftStructure: $0) }
        + swiftStructure.actors.map { convertActor(swiftActor: $0) }
        
        return IrClass(
            simpleName: simpleName,
            kind: kind,
            visibility: visibility,
            typeParameters: typeParameters,
            superClasses: superClasses,
            fields: fields,
            methods: methods,
            nestedClasses: nestedClasses,
            languageProperties: languageProperties
        )
    }
    
    func convertActor(swiftActor: Actor) -> IrClass {
        let simpleName = swiftActor.name
        
        let kind = IrClassKind.ActorClassKind(IrActorClassKind())
        
        let visibility = if(swiftActor.isPrivate) {
            swiftVisibilityPrivate()
        } else if (swiftActor.isPublic) {
            swiftVisibilityPublic()
        } else if (swiftActor.isOpen) {
            swiftVisibilityOpen()
        } else if (swiftActor.isFilePrivate) {
            swiftVisibilityFileprivate()
        } else {
            swiftVisibilityInternal()
        }
        
        var languageProperties: [String : AnyCodable] = [:]
        
        if (swiftActor.isFinal) {
            languageProperties["final"] = true
        }
        
        // TODO: annotations?
        
        // TODO: generic requirements
        
        let typeParameters = swiftActor.genericParameters.map { typeParameter in
            convertTypeParameter(typeParameter: typeParameter)
        }
        
        let superClasses = swiftActor.inheritance.map { parentTypeName in
            convertSuperClass(parentTypeName: parentTypeName)
        }
        
        let fields = swiftActor.variables.map { swiftVariable in
            convertField(swiftVariable: swiftVariable)
        }
        
        let methods = swiftActor.functions.map { swiftFunction in
            convertFunction(swiftFunction: swiftFunction)
        } + swiftActor.initializers.map { swiftInitializer in
            convertInitializer(className: simpleName, swiftInitializer: swiftInitializer)
        }
        
        // TODO: support initializers
        
        let nestedClasses =
        swiftActor.classes.map { convertClass(swiftClass: $0) }
        + swiftActor.protocols.map { convertProtocol(swiftProtocol: $0) }
        + swiftActor.enumerations.map { convertEnum(swiftEnumeration: $0) }
        + swiftActor.structures.map { convertStruct(swiftStructure: $0) }
        + swiftActor.actors.map { convertActor(swiftActor: $0) }
        
        return IrClass(
            simpleName: simpleName,
            kind: kind,
            visibility: visibility,
            typeParameters: typeParameters,
            superClasses: superClasses,
            fields: fields,
            methods: methods,
            nestedClasses: nestedClasses,
            languageProperties: languageProperties
        )
    }
}
