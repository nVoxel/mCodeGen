package com.voxeldev.mcodegen.dsl.language.kotlin.ir

import com.voxeldev.mcodegen.dsl.ir.IrAnnotation
import com.voxeldev.mcodegen.dsl.ir.IrClass
import com.voxeldev.mcodegen.dsl.ir.IrClassInitializer
import com.voxeldev.mcodegen.dsl.ir.IrClassKind
import com.voxeldev.mcodegen.dsl.ir.IrField
import com.voxeldev.mcodegen.dsl.ir.IrLocation
import com.voxeldev.mcodegen.dsl.ir.IrMethod
import com.voxeldev.mcodegen.dsl.ir.IrSuperClass
import com.voxeldev.mcodegen.dsl.ir.IrTypeParameter
import com.voxeldev.mcodegen.dsl.ir.IrVisibility

/**
 * This is a fallback type.
 * Marks that we were unable to obtain full IrClass info during source code analysis.
 */
class IrClassStub(
    name: String,
    kind: IrClassKind,
    visibility: IrVisibility,
    typeParameters: List<IrTypeParameter>,
    superClasses: List<IrSuperClass>,
    fields: List<IrField>,
    methods: List<IrMethod>,
    initializers: List<IrClassInitializer>,
    nestedClasses: List<IrClass>,
    location: IrLocation? = null,
    annotations: List<IrAnnotation> = emptyList(),
    languageProperties: Map<String, Any> = emptyMap()
) : IrClass(
    name = name,
    kind = kind,
    visibility = visibility,
    typeParameters = typeParameters,
    superClasses = superClasses,
    fields = fields,
    methods = methods,
    initializers = initializers,
    nestedClasses = nestedClasses,
    location = location,
    annotations = annotations,
    languageProperties = languageProperties,
)