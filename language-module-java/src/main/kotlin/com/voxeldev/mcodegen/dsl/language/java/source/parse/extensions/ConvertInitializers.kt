package com.voxeldev.mcodegen.dsl.language.java.source.parse.extensions

import com.voxeldev.mcodegen.dsl.ir.IrClassInitializer
import com.voxeldev.mcodegen.dsl.ir.builders.IrClassBuilder
import com.voxeldev.mcodegen.dsl.ir.builders.irClassInitializer
import com.voxeldev.mcodegen.dsl.ir.builders.irMethodBody
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.com.intellij.psi.PsiClassInitializer
import org.jetbrains.kotlin.com.intellij.psi.PsiModifier

context(JavaModule, ScenarioScope)
internal fun convertInitializers(
    psiClassInitializers: Array<PsiClassInitializer>,
    irClassBuilder: IrClassBuilder,
) {
    psiClassInitializers.forEach { psiClassInitializer ->
        irClassBuilder.addInitializer(
            initializer = convertInitializer(
                psiClassInitializer = psiClassInitializer,
            )
        )
    }
}

context(JavaModule, ScenarioScope)
private fun convertInitializer(
    psiClassInitializer: PsiClassInitializer,
): IrClassInitializer {
    val initializer = irClassInitializer(
        kind = when {
            psiClassInitializer.hasModifierProperty(PsiModifier.STATIC) -> {
                IrClassInitializer.IrStaticClassInitializerKind
            }

            else -> {
                IrClassInitializer.IrInstanceClassInitializerKind
            }
        }
    ).apply {
        val irMethodBodyBuilder = irMethodBody()
        psiClassInitializer.body.statements.forEach { statement ->
            irMethodBodyBuilder.addStatement(
                statement = convertStatement(psiStatement = statement),
            )
        }

        body(irMethodBodyBuilder.build())
    }.build()

    return initializer
}