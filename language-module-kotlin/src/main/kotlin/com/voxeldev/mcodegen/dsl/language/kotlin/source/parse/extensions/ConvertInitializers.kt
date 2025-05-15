package com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions

import com.voxeldev.mcodegen.dsl.ir.IrClassInitializer
import com.voxeldev.mcodegen.dsl.ir.builders.IrClassBuilder
import com.voxeldev.mcodegen.dsl.ir.builders.irClassInitializer
import com.voxeldev.mcodegen.dsl.ir.builders.irMethodBody
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtClassInitializer
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.resolve.BindingContext

context(KotlinModule, BindingContext, ScenarioScope)
internal fun convertInitializers(
    initializers: List<KtClassInitializer>,
    irClassBuilder: IrClassBuilder,
) {
    initializers.forEach { ktClassInitializer ->
        irClassBuilder.addInitializer(
            initializer = convertInitializer(ktClassInitializer)
        )
    }
}

context(KotlinModule, BindingContext, ScenarioScope)
private fun convertInitializer(ktClassInitializer: KtClassInitializer): IrClassInitializer {
    val containingDeclaration = ktClassInitializer.containingDeclaration

    val initializer = irClassInitializer(
        kind = when {
            containingDeclaration is KtObjectDeclaration && containingDeclaration.isCompanion() -> {
                IrClassInitializer.IrStaticClassInitializerKind
            }

            else -> {
                IrClassInitializer.IrInstanceClassInitializerKind
            }
        }
    ).apply {
        ktClassInitializer.body?.let { body ->
            val irMethodBodyBuilder = irMethodBody()
            if (body is KtBlockExpression) {
                body.statements.forEach { statement ->
                    irMethodBodyBuilder.addStatement(
                        convertStatement(containingDeclaration, statement)
                    )
                }
            } else {
                irMethodBodyBuilder.addStatement(
                    convertStatement(containingDeclaration, body)
                )
            }

            body(irMethodBodyBuilder.build())
        }
    }.build()

    return initializer
}