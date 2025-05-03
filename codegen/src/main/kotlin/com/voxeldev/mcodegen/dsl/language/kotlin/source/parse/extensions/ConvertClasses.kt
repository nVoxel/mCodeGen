package com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions

import com.voxeldev.mcodegen.dsl.ir.IrClass
import com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrAnnotationClassKind
import com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrClassClassKind
import com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrEnumClassKind
import com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrInterfaceClassKind
import com.voxeldev.mcodegen.dsl.ir.IrSuperClass
import com.voxeldev.mcodegen.dsl.ir.IrTypeReference
import com.voxeldev.mcodegen.dsl.ir.builders.IrFileBuilder
import com.voxeldev.mcodegen.dsl.ir.builders.irClass
import com.voxeldev.mcodegen.dsl.ir.builders.irSuperClass
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.IrObjectClassKind
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.builders.irClassStub
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.internalVisibility
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.privateVisibility
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.protectedVisibility
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.publicVisibility
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.DescriptorToSourceUtils
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperClassNotAny
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperInterfaces

context(KotlinModule, BindingContext, ScenarioScope)
internal fun convertClasses(ktClassesOrObjects: List<KtClassOrObject>, irFileBuilder: IrFileBuilder) {
    ktClassesOrObjects.forEach { psiClass ->
        irFileBuilder.addDeclaration(convertClass(psiClass))
    }
}

context(KotlinModule, BindingContext, ScenarioScope)
private fun convertClass(ktClassOrObject: KtClassOrObject): IrClass {
    val className = ktClassOrObject.fqName?.asString() ?: "Ir:UnnamedClass"
    val classDescriptor = this@BindingContext.get(BindingContext.CLASS, ktClassOrObject)
        ?: throw IllegalArgumentException("Unable to get KtClass descriptor through BindingContext")

    val irClassBuilder = irClass(className)

    irClassBuilder.addLanguageProperty("simpleName", ktClassOrObject.name ?: "Ir:UnnamedClass")

    irClassBuilder.kind(
        when {
            ktClassOrObject is KtObjectDeclaration -> IrObjectClassKind
            ktClassOrObject is KtClass && ktClassOrObject.isAnnotation() -> IrAnnotationClassKind
            ktClassOrObject is KtClass && ktClassOrObject.isEnum() -> IrEnumClassKind
            ktClassOrObject is KtClass && ktClassOrObject.isInterface() -> IrInterfaceClassKind
            else -> IrClassClassKind
        }
    )

    irClassBuilder.visibility(
        when {
            ktClassOrObject.hasModifier(KtTokens.PROTECTED_KEYWORD) -> protectedVisibility()

            ktClassOrObject.hasModifier(KtTokens.INTERNAL_KEYWORD) -> internalVisibility()

            ktClassOrObject.hasModifier(KtTokens.PRIVATE_KEYWORD) -> privateVisibility()

            else -> publicVisibility()
        }
    )

    if (ktClassOrObject.hasModifier(KtTokens.ABSTRACT_KEYWORD)) {
        irClassBuilder.addLanguageProperty(
            KtTokens.ABSTRACT_KEYWORD.value, true
        )
    }

    if (ktClassOrObject.hasModifier(KtTokens.COMPANION_KEYWORD)) {
        irClassBuilder.addLanguageProperty(
            KtTokens.COMPANION_KEYWORD.value, true
        )
    }

    if (ktClassOrObject.hasModifier(KtTokens.DATA_KEYWORD)) {
        irClassBuilder.addLanguageProperty(
            KtTokens.DATA_KEYWORD.value, true
        )
    }

    if (ktClassOrObject.hasModifier(KtTokens.FINAL_KEYWORD)) {
        irClassBuilder.addLanguageProperty(
            KtTokens.FINAL_KEYWORD.value, true
        )
    }

    if (ktClassOrObject.hasModifier(KtTokens.OPEN_KEYWORD)) {
        irClassBuilder.addLanguageProperty(
            KtTokens.OPEN_KEYWORD.value, true
        )
    }

    if (ktClassOrObject.hasModifier(KtTokens.INNER_KEYWORD)) {
        irClassBuilder.addLanguageProperty(
            KtTokens.OPEN_KEYWORD.value, true
        )
    }

    if (ktClassOrObject.hasModifier(KtTokens.SEALED_KEYWORD)) {
        irClassBuilder.addLanguageProperty(
            KtTokens.SEALED_KEYWORD.value, true
        )
    }

    // TODO: convert annotations

    convertTypeParameters(ktClassOrObject, ktClassOrObject.typeParameters, irClassBuilder)

    val superClass = classDescriptor.getSuperClassNotAny()?.let { superClassDescriptor ->
        convertSuperclass(ktClassOrObject, superClassDescriptor)
    }

    superClass?.let { irClassBuilder.addSuperClass(superClass) }

    val interfaces = classDescriptor.getSuperInterfaces().mapNotNull { superInterfaceDescriptor ->
        convertSuperclass(ktClassOrObject, superInterfaceDescriptor)
    }

    interfaces.forEach { superInterface -> irClassBuilder.addSuperClass(superInterface) }

    ktClassOrObject.declarations.filterIsInstance<KtClassOrObject>().forEach { nestedClass ->
        irClassBuilder.addNestedClass(convertClass(nestedClass))
    }

    return irClassBuilder.build()
}

context(KotlinModule, BindingContext, ScenarioScope)
private fun convertSuperclass(
    ktClassOrObject: KtClassOrObject,
    classDescriptor: ClassDescriptor,
): IrSuperClass? {
    val fqName = classDescriptor.fqNameSafe.asString()
    if (ktClassOrObject.isAnnotation() && fqName == "kotlin.Annotation") {
        return null
    }

    val declaration = DescriptorToSourceUtils.descriptorToDeclaration(classDescriptor)
    return (declaration as? KtClassOrObject)?.let { superClassAsKt ->
        val superClass = convertClass(superClassAsKt)
        createSuperClassFromSuper(ktClassOrObject, superClass)
    } ?: createSuperClassStubFromSuper(ktClassOrObject, classDescriptor, fqName)
}

context(KotlinModule, BindingContext, ScenarioScope)
private fun createSuperClassFromSuper(
    ktClassOrObject: KtClassOrObject,
    superClass: IrClass,
): IrSuperClass {
    val irSuperClassBuilder = irSuperClass(superClass)

    val preloadedTypeParameters = preloadTypeParameters(ktClassOrObject.typeParameters)
    val superAsType = ktClassOrObject.superTypeListEntries
        .mapNotNull { superTypeListEntry -> superTypeListEntry.typeReference?.typeElement }
        .mapNotNull { superTypeElement -> convertType(superTypeElement, preloadedTypeParameters) as? IrTypeReference }
        .find { superTypeReference -> superTypeReference.referencedClassName == superClass.name }
        ?: throw IllegalArgumentException("Unable to find superclass in ktClassOrObject by name")

    superAsType.typeParameters.forEach { typeParameter ->
        irSuperClassBuilder.addType(typeParameter)
    }

    return irSuperClassBuilder.build()
}

context(KotlinModule, BindingContext, ScenarioScope)
private fun createSuperClassStubFromSuper(
    ktClassOrObject: KtClassOrObject,
    classDescriptor: ClassDescriptor,
    superFqName: String,
): IrSuperClass {
    return createSuperClassFromSuper(
        ktClassOrObject = ktClassOrObject,
        superClass = irClassStub(superFqName).apply {
            visibility(publicVisibility())
            kind(
                when(classDescriptor.kind) {
                    ClassKind.OBJECT -> IrObjectClassKind
                    ClassKind.ANNOTATION_CLASS -> IrAnnotationClassKind
                    ClassKind.ENUM_CLASS -> IrEnumClassKind
                    ClassKind.INTERFACE -> IrInterfaceClassKind
                    // ClassKind.ENUM_ENTRY ->
                    else -> IrClassClassKind
                }
            )
        }.build(),
    )
}
