package com.voxeldev.mcodegen.dsl.language.java.source.generate.extensions

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeSpec
import com.voxeldev.mcodegen.dsl.ir.IrClass
import com.voxeldev.mcodegen.dsl.ir.IrClassKind
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPrivate
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityProtected
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPublic
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.com.intellij.psi.PsiModifier
import javax.lang.model.element.Modifier

context(JavaModule, ScenarioScope)
internal fun convertClass(irClass: IrClass): TypeSpec {
    val name = irClass.languageProperties["simpleName"] as? String ?: irClass.name

    val poetClassBuilder = when (irClass.kind) {
        IrClassKind.CLASS -> TypeSpec.classBuilder(name)
        IrClassKind.INTERFACE -> TypeSpec.interfaceBuilder(name)
        IrClassKind.ENUM -> TypeSpec.enumBuilder(name)
        IrClassKind.ANNOTATION -> TypeSpec.annotationBuilder(name)
    }.apply {
        when (irClass.visibility) {
            is IrVisibilityPublic -> addModifiers(Modifier.PUBLIC)
            is IrVisibilityProtected -> addModifiers(Modifier.PROTECTED)
            is IrVisibilityPrivate -> addModifiers(Modifier.PRIVATE)
        }

        if (irClass.kind != IrClassKind.INTERFACE && irClass.kind != IrClassKind.ANNOTATION) {
            if (irClass.languageProperties[PsiModifier.ABSTRACT] == true) {
                addModifiers(Modifier.ABSTRACT)
            }
        }

        if (irClass.languageProperties[PsiModifier.FINAL] == true) {
            addModifiers(Modifier.FINAL)
        }

        if (irClass.languageProperties[PsiModifier.STATIC] == true) {
            addModifiers(Modifier.STATIC)
        }

        irClass.annotations.forEach { irAnnotation ->
            addAnnotation(convertAnnotation(irClass, irAnnotation))
        }

        irClass.typeParameters.forEach { irTypeParameter ->
            addTypeVariable(convertTypeParameter(irTypeParameter))
        }

        val extends = irClass.superClasses.filter { it.kind == IrClassKind.CLASS }.run {
            if (size > 1) {
                throw IllegalStateException("Java currently does not support more than one superclass")
            }

            firstOrNull()
        }

        extends?.let {
            superclass(ClassName.bestGuess(extends.name))
        }

        val implements = irClass.superClasses.filter { it.kind == IrClassKind.INTERFACE }

        implements.forEach { implementedInterface ->
            addSuperinterface(ClassName.bestGuess(implementedInterface.name))
        }

        convertFields(irClass, irClass.fields, this)

        convertMethods(irClass, irClass.methods, this)

        convertInitializers(irClass, irClass.initializers, this)

        irClass.nestedClasses.forEach { nestedIrClass ->
            addType(convertClass(irClass = nestedIrClass))
        }
    }

    return poetClassBuilder.build()
}