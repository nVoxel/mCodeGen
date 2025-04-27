package com.voxeldev.mcodegen.dsl.language.java.source.parse.extensions

import com.voxeldev.mcodegen.dsl.ir.IrClass
import com.voxeldev.mcodegen.dsl.ir.IrClassKind
import com.voxeldev.mcodegen.dsl.ir.builders.IrFileBuilder
import com.voxeldev.mcodegen.dsl.ir.builders.irClass
import com.voxeldev.mcodegen.dsl.ir.builders.irTypeParameter
import com.voxeldev.mcodegen.dsl.ir.builders.irTypeReference
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.language.java.JavaModule.PSI_CLASS
import com.voxeldev.mcodegen.dsl.language.java.JavaModule.visitedClasses
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
    if (visitedClasses.contains(className)) {
        return visitedClasses[className]!!.build()
    }

    val irClassBuilder = irClass(className)
    visitedClasses[className] = irClassBuilder

    irClassBuilder.kind(
        when {
            psiClass.isAnnotationType -> IrClassKind.ANNOTATION
            psiClass.isEnum -> IrClassKind.ENUM
            psiClass.isInterface -> IrClassKind.INTERFACE
            else -> IrClassKind.CLASS
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

    psiClass.typeParameters.forEach { typeParameter ->
        val irTypeParameter = irTypeParameter(typeParameter.name ?: "Ir:UnnamedTypeParameter")
        typeParameter.extendsListTypes.forEach { extendsType ->
            val referencedClass = extendsType.resolve() ?: return@forEach
            val referencedClassName = referencedClass.qualifiedName ?: return@forEach
            irTypeParameter.addExtendsType(
                extendsType = irTypeReference(referencedClassName).apply {
                    addLanguageProperty(PSI_CLASS, referencedClass)
                }.build()
            )
        }
        irClassBuilder.addTypeParameter(irTypeParameter.build())
    }

    // Convert superclass and interfaces
    psiClass.extendsList?.referencedTypes?.forEach { type ->
        val resolvedClass = type.resolve() ?: return@forEach
        irClassBuilder.addSuperClass(convertClass(resolvedClass))
    }
    psiClass.implementsList?.referencedTypes?.forEach { type ->
        val resolvedInterface = type.resolve() ?: return@forEach
        irClassBuilder.addSuperClass(convertClass(resolvedInterface))
    }

    // Convert fields
    convertFields(psiClass, psiClass.fields, irClassBuilder)

    // Convert methods
    convertMethods(psiClass, psiClass.methods, irClassBuilder)

    // Convert nested classes
    psiClass.innerClasses.forEach { innerClass ->
        irClassBuilder.addNestedClass(convertClass(innerClass))
    }

    return irClassBuilder.build()
}