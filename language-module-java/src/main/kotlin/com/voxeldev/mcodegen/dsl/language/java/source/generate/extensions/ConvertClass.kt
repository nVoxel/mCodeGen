package com.voxeldev.mcodegen.dsl.language.java.source.generate.extensions

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeSpec
import com.voxeldev.mcodegen.dsl.ir.IrClass
import com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrAnnotationClassKind
import com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrClassClassKind
import com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrEnumClassKind
import com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrInterfaceClassKind
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPrivate
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityProtected
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPublic
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.language.java.source.parse.extensions.JAVA_CLASS_SIMPLE_NAME
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.com.intellij.psi.PsiModifier
import javax.lang.model.element.Modifier

context(JavaModule, ScenarioScope)
internal fun convertClass(irClass: IrClass): TypeSpec {
    val name = irClass.languageProperties[JAVA_CLASS_SIMPLE_NAME] as? String ?: irClass.name

    val poetClassBuilder = when (irClass.kind) {
        IrClassClassKind -> TypeSpec.classBuilder(name)
        IrInterfaceClassKind -> TypeSpec.interfaceBuilder(name)
        IrEnumClassKind -> TypeSpec.enumBuilder(name)
        IrAnnotationClassKind -> TypeSpec.annotationBuilder(name)
        else -> throw IllegalArgumentException("Unsupported class kind : ${irClass.kind}")
    }.apply {
        when (irClass.visibility) {
            is IrVisibilityPublic -> addModifiers(Modifier.PUBLIC)
            is IrVisibilityProtected -> addModifiers(Modifier.PROTECTED)
            is IrVisibilityPrivate -> addModifiers(Modifier.PRIVATE)
        }

        if (irClass.kind != IrInterfaceClassKind && irClass.kind != IrAnnotationClassKind) {
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

        val extends = irClass.superClasses.filter { it.kind == IrClassClassKind }.run {
            if (size > 1) {
                throw IllegalStateException("Java currently does not support more than one superclass")
            }

            firstOrNull()
        }

        extends?.let {
            // TODO: support inheritance cases with types like List<T> using ParameterizedTypeName
            superclass(ClassName.bestGuess(extends.superClassName))
        }

        val implements = irClass.superClasses.filter { it.kind == IrInterfaceClassKind }

        implements.forEach { implementedInterface ->
            // TODO: support inheritance cases with types like List<T> using ParameterizedTypeName
            addSuperinterface(ClassName.bestGuess(implementedInterface.superClassName))
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