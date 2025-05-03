package com.voxeldev.mcodegen.dsl.language.java.source.parse.extensions

import com.voxeldev.mcodegen.dsl.ir.IrClass
import com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrAnnotationClassKind
import com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrClassClassKind
import com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrEnumClassKind
import com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrInterfaceClassKind
import com.voxeldev.mcodegen.dsl.ir.builders.IrFileBuilder
import com.voxeldev.mcodegen.dsl.ir.builders.irClass
import com.voxeldev.mcodegen.dsl.ir.builders.irSuperClass
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.language.java.ir.packagePrivateVisibility
import com.voxeldev.mcodegen.dsl.language.java.ir.privateVisibility
import com.voxeldev.mcodegen.dsl.language.java.ir.protectedVisibility
import com.voxeldev.mcodegen.dsl.language.java.ir.publicVisibility
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.com.intellij.psi.PsiClass
import org.jetbrains.kotlin.com.intellij.psi.PsiModifier

context(JavaModule, ScenarioScope)
internal fun convertClasses(psiClasses: Array<PsiClass>, irFileBuilder: IrFileBuilder) {
    psiClasses.forEach { psiClass ->
        irFileBuilder.addDeclaration(convertClass(psiClass))
    }
}

context(JavaModule, ScenarioScope)
private fun convertClass(psiClass: PsiClass): IrClass {
    val className = psiClass.qualifiedName ?: "Ir:UnnamedClass"

    val irClassBuilder = irClass(className)

    irClassBuilder.addLanguageProperty("simpleName", psiClass.name ?: "Ir:UnnamedClass")

    irClassBuilder.kind(
        when {
            psiClass.isAnnotationType -> IrAnnotationClassKind
            psiClass.isEnum -> IrEnumClassKind
            psiClass.isInterface -> IrInterfaceClassKind
            else -> IrClassClassKind
        }
    )

    irClassBuilder.visibility(
        when {
            psiClass.hasModifierProperty(PsiModifier.PUBLIC) -> publicVisibility()

            psiClass.hasModifierProperty(PsiModifier.PROTECTED) -> protectedVisibility()

            psiClass.hasModifierProperty(PsiModifier.PRIVATE) -> privateVisibility()

            else -> if (psiClass.isInterface) publicVisibility() else packagePrivateVisibility()
        }
    )

    if (psiClass.hasModifierProperty(PsiModifier.ABSTRACT)) {
        irClassBuilder.addLanguageProperty(
            PsiModifier.ABSTRACT, true
        )
    }

    if (psiClass.hasModifierProperty(PsiModifier.FINAL)) {
        irClassBuilder.addLanguageProperty(
            PsiModifier.FINAL, true
        )
    }

    if (psiClass.hasModifierProperty(PsiModifier.STATIC)) {
        irClassBuilder.addLanguageProperty(
            PsiModifier.STATIC, true
        )
    }

    psiClass.annotations.forEach { annotation ->
        irClassBuilder.addAnnotation(convertAnnotation(annotation))
    }

    psiClass.typeParameters.forEach { typeParameter ->
        irClassBuilder.addTypeParameter(convertTypeParameter(typeParameter))
    }

    // Convert superclass and interfaces
    psiClass.extendsList?.referencedTypes?.forEach { type ->
        val resolvedClass = type.resolve() ?: return@forEach
        irClassBuilder.addSuperClass(
            irSuperClass(convertClass(resolvedClass)).apply {
                // TODO: support inheritance cases with types like List<T>
            }.build()
        )
    }
    psiClass.implementsList?.referencedTypes?.forEach { type ->
        val resolvedInterface = type.resolve() ?: return@forEach
        irClassBuilder.addSuperClass(
            irSuperClass(convertClass(resolvedInterface)).apply {
                // TODO: support inheritance cases with types like List<T>
            }.build()
        )
    }

    // Convert fields
    convertFields(psiClass, psiClass.fields, irClassBuilder)

    // Convert methods
    convertMethods(psiClass, psiClass.methods, irClassBuilder)

    // Convert initializers
    convertInitializers(psiClass.initializers, irClassBuilder)

    // Convert nested classes
    psiClass.innerClasses.forEach { nestedClass ->
        irClassBuilder.addNestedClass(convertClass(nestedClass))
    }

    return irClassBuilder.build()
}