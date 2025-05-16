package com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions

import com.voxeldev.mcodegen.dsl.ir.IrField
import com.voxeldev.mcodegen.dsl.ir.IrType
import com.voxeldev.mcodegen.dsl.ir.builders.IrClassBuilder
import com.voxeldev.mcodegen.dsl.ir.builders.IrFieldBuilder
import com.voxeldev.mcodegen.dsl.ir.builders.irField
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.internalVisibility
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.privateVisibility
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.protectedVisibility
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.publicVisibility
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtModifierListOwner
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.isError

// for fields declared in the class body
context(KotlinModule, BindingContext, ScenarioScope)
internal fun convertFieldsAsProperties(
    ktClassOrObject: KtClassOrObject,
    fields: List<KtProperty>,
    irClassBuilder: IrClassBuilder,
) {
    fields.forEach { field ->
        val irField = convertFieldAsProperty(ktClassOrObject, field) ?: return@forEach
        irClassBuilder.addField(irField)
    }
}

context(KotlinModule, BindingContext, ScenarioScope)
internal fun convertFieldAsParameter(
    ktClassOrObject: KtClassOrObject,
    ktField: KtParameter,
): IrField? {
    val name = ktField.name ?: return null

    val irFieldBuilder = irField(
        name = name,
        type = convertFieldType(ktClassOrObject, ktField)
    )

    convertFieldModifiers(ktField, irFieldBuilder)

    irFieldBuilder.mutable(isMutable = ktField.valOrVarKeyword?.text != KtTokens.VAL_KEYWORD.value)

    ktField.annotationEntries.forEach { ktAnnotationEntry ->
        irFieldBuilder.addAnnotation(convertAnnotation(ktClassOrObject, ktAnnotationEntry))
    }

    ktField.defaultValue?.let { defaultValue ->
        irFieldBuilder.initializer(convertStatement(ktClassOrObject, defaultValue))
    }

    return irFieldBuilder.build()
}

context(KotlinModule, BindingContext, ScenarioScope)
internal fun convertFieldAsProperty(
    ktClassOrObject: KtClassOrObject,
    ktField: KtProperty,
): IrField? {
    val name = ktField.name ?: return null

    val irFieldBuilder = irField(
        name = name,
        type = convertFieldType(ktClassOrObject, ktField)
    )

    convertFieldModifiers(ktField, irFieldBuilder)

    irFieldBuilder.mutable(isMutable = ktField.isVar)

    ktField.annotationEntries.forEach { ktAnnotationEntry ->
        irFieldBuilder.addAnnotation(convertAnnotation(ktClassOrObject, ktAnnotationEntry))
    }

    ktField.initializer?.let { initializer ->
        irFieldBuilder.initializer(convertStatement(ktClassOrObject, initializer))
    }

    return irFieldBuilder.build()
}

context(KotlinModule, BindingContext, ScenarioScope)
private fun convertFieldModifiers(
    ktField: KtModifierListOwner,
    irFieldBuilder: IrFieldBuilder,
) {
    irFieldBuilder.visibility(
        when {
            ktField.hasModifier(KtTokens.PROTECTED_KEYWORD) -> protectedVisibility()
            ktField.hasModifier(KtTokens.INTERNAL_KEYWORD) -> internalVisibility()
            ktField.hasModifier(KtTokens.PRIVATE_KEYWORD) -> privateVisibility()
            else -> publicVisibility()
        }
    )

    if (ktField.hasModifier(KtTokens.ABSTRACT_KEYWORD)) {
        irFieldBuilder.addLanguageProperty(
            KtTokens.ABSTRACT_KEYWORD.value, true
        )
    }

    if (ktField.hasModifier(KtTokens.FINAL_KEYWORD)) {
        irFieldBuilder.addLanguageProperty(
            KtTokens.FINAL_KEYWORD.value, true
        )
    }

    if (ktField.hasModifier(KtTokens.OPEN_KEYWORD)) {
        irFieldBuilder.addLanguageProperty(
            KtTokens.OPEN_KEYWORD.value, true
        )
    }
}

context(KotlinModule, BindingContext, ScenarioScope)
private fun convertFieldType(
    ktClassOrObject: KtClassOrObject,
    ktField: KtCallableDeclaration,
): IrType {
    val preloadedTypeParameters = preloadTypeParameters(ktClassOrObject.typeParameters)

    // try to convert explicit type
    ktField.typeReference?.typeElement?.let { typeElement ->
        return convertKtTypeElement(typeElement, preloadedTypeParameters)
    }

    // try to convert inferred type
    val variableDescriptor = this@BindingContext.get(BindingContext.VARIABLE, ktField)

    if (variableDescriptor == null || variableDescriptor.type.isError) {
        // probably caused by missing source roots
        throw IllegalArgumentException("Please, specify the type for ${ktField.name} field explicitly")
    }

    return convertKotlinType(variableDescriptor.type, preloadedTypeParameters)
}
