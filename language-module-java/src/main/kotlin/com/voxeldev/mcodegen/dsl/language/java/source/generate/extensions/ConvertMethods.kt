package com.voxeldev.mcodegen.dsl.language.java.source.generate.extensions

import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeSpec
import com.voxeldev.mcodegen.dsl.ir.IrClass
import com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrInterfaceClassKind
import com.voxeldev.mcodegen.dsl.ir.IrConstructor
import com.voxeldev.mcodegen.dsl.ir.IrExpression
import com.voxeldev.mcodegen.dsl.ir.IrMethod
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPrivate
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityProtected
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPublic
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.language.java.source.parse.extensions.JAVA_METHOD_DEFAULT_VALUE
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.com.intellij.psi.PsiModifier
import javax.lang.model.element.Modifier

context(JavaModule, ScenarioScope)
internal fun convertMethods(
    irClass: IrClass,
    irMethods: List<IrMethod>,
    poetClassBuilder: TypeSpec.Builder,
) {
    irMethods.forEach { irMethod ->
        poetClassBuilder.addMethod(convertMethod(irClass, irMethod))
    }
}

context(JavaModule, ScenarioScope)
private fun convertMethod(
    irClass: IrClass,
    irMethod: IrMethod,
): MethodSpec {
    val poetMethod = if (irMethod is IrConstructor) {
        MethodSpec.constructorBuilder()
    } else {
        MethodSpec.methodBuilder(irMethod.name).apply {
            returns(convertType(irMethod.returnType))
        }
    }

    poetMethod.apply {
        if (irClass.kind != IrInterfaceClassKind) {
            when (irMethod.visibility) {
                is IrVisibilityPublic -> addModifiers(Modifier.PUBLIC)
                is IrVisibilityProtected -> addModifiers(Modifier.PROTECTED)
                is IrVisibilityPrivate -> addModifiers(Modifier.PRIVATE)
            }
        }

        if (irMethod.isAbstract) {
            addModifiers(Modifier.ABSTRACT)
        }

        if (irMethod.isStatic) {
            addModifiers(Modifier.STATIC)
        }

        if (irMethod.languageProperties[PsiModifier.NATIVE] == true) {
            addModifiers(Modifier.NATIVE)
        }

        irMethod.annotations.forEach { irAnnotation ->
            addAnnotation(convertAnnotation(irClass, irAnnotation))
        }

        irMethod.typeParameters.forEach { irTypeParameter ->
            addTypeVariable(convertTypeParameter(irTypeParameter))
        }

        irMethod.parameters.forEach { irParameter ->
            addParameter(
                ParameterSpec.builder(
                    convertType(irParameter.type),
                    irParameter.name,
                ).build()
            )
        }

        val defaultValue = irMethod.languageProperties[JAVA_METHOD_DEFAULT_VALUE] as? IrExpression
        defaultValue?.let {
            defaultValue(convertExpression(irClass, defaultValue))
        }

        if (irMethod is IrConstructor) {
            irMethod.otherConstructorCall?.let { otherConstructorCall ->
                poetMethod.addStatement(convertExpression(irClass, otherConstructorCall))
            }
        }

        irMethod.body?.let { irMethodBody ->
            val bodyCodeBlock = CodeBlock.builder()
            irMethodBody.statements.forEach { irBodyStatement ->
                bodyCodeBlock.add(convertStatement(irClass, irBodyStatement))
            }
            addCode(bodyCodeBlock.build())
        }
    }

    return poetMethod.build()
}