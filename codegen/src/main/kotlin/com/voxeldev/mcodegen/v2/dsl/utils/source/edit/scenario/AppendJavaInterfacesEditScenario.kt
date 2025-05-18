package com.voxeldev.mcodegen.v2.dsl.utils.source.edit.scenario

import com.voxeldev.mcodegen.dsl.ir.IrClass
import com.voxeldev.mcodegen.dsl.ir.IrFile
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import com.voxeldev.mcodegen.dsl.source.edit.scenario.EditScenarioBaseImpl
import com.voxeldev.mcodegen.dsl.source.edit.step.EditStep
import com.voxeldev.mcodegen.v2.constants.TdCommonScenarioConstants
import com.voxeldev.mcodegen.v2.dsl.utils.source.edit.step.AddJavaGettersEditStep
import com.voxeldev.mcodegen.v2.dsl.utils.source.edit.step.AddJavaImportEditStep
import com.voxeldev.mcodegen.v2.dsl.utils.source.edit.step.AppendJavaInterfacesEditStep

private val ignoredClasses = setOf<String>(
    "TdApi",
    "Object",
)

context(ScenarioScope)
fun appendJavaInterfacesEditScenario(commonClasses: IrFile): AppendJavaInterfacesEditScenario {
    return AppendJavaInterfacesEditScenario(commonClasses.declarations.filterIsInstance<IrClass>())
}

context(ScenarioScope)
class AppendJavaInterfacesEditScenario internal constructor(
    private val commonClasses: List<IrClass>,
): EditScenarioBaseImpl() {

    override fun getSteps(): List<EditStep> = buildList {
        add(AddJavaImportEditStep("com.voxeldev.tgdrive.*"))

        val commonClassesMap = getMap(commonClasses.first()) // TDLib has only one top level class
        add(AppendJavaInterfacesEditStep(commonClassesMap))

        add(
            AddJavaGettersEditStep(
                classNamesToAddGetters = commonClassesMap.keys,
                ignoredFieldNames = setOf(TdCommonScenarioConstants.CONSTRUCTOR_FIELD),
                fixGettersForKotlin = true,
            )
        )
    }

    private fun getMap(irClass: IrClass): Map<String, Set<String>> {
        val classSimpleName = irClass.name.split(".").last()
        val map = hashMapOf<String, Set<String>>()

        if (classSimpleName !in ignoredClasses) {
            map[classSimpleName] = setOf("Td$classSimpleName")
        }

        irClass.nestedClasses.forEach { nestedClass ->
            map += getMap(nestedClass)
        }

        return map
    }
}