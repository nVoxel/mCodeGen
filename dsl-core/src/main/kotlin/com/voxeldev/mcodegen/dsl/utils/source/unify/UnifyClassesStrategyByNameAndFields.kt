package com.voxeldev.mcodegen.dsl.utils.source.unify

import com.voxeldev.mcodegen.dsl.ir.IrClass
import com.voxeldev.mcodegen.dsl.ir.IrField
import com.voxeldev.mcodegen.dsl.ir.IrFile
import com.voxeldev.mcodegen.dsl.ir.builders.irFile

/**
 * Returns a list of classes whose names match and exist in all sources.
 */
class UnifyClassesStrategyByNameAndFields :
    UnifySourcesListStrategy<IrFile, IrFile> {

    override fun getUnifiedSourcesList(vararg sources: IrFile): IrFile {
        return if (sources.isEmpty()) {
            throw IllegalArgumentException("At least one source should be provided to UnifySourcesListStrategy")
        } else {
            val irFile = irFile(sources.first().name)

            val commonClasses = if (sources.size < 2) {
                 sources.firstOrNull()?.declarations?.filterIsInstance<IrClass>() ?: emptyList()
            } else {
                getCommonClasses(*sources)
            }

            commonClasses.forEach { commonClass ->
                irFile.addDeclaration(commonClass)
            }

            irFile.build()
        }
    }

    private fun getCommonClasses(vararg sources: IrFile): List<IrClass> {
        // Hash‑maps keyed by class name for all sources except the first one
        val classesHashMaps: List<Map<String, IrClass>> = sources
            .drop(1)
            .map { irFile ->
                irFile.declarations
                    .filterIsInstance<IrClass>()
                    .associateBy { it.name }
            }

        // Iterate through classes in the first source, keeping only those
        // that appear (by name) in every other source
        return sources.first().declarations
            .filterIsInstance<IrClass>()
            .mapNotNull { irClass ->
                val matches = classesHashMaps.mapNotNull { it[irClass.name] }
                if (matches.size < classesHashMaps.size) return@mapNotNull null

                val allMatches = listOf(irClass) + matches

                val unifiedFields = getCommonFields(allMatches)
                val unifiedNested = getCommonNestedClasses(allMatches)

                createUnifiedCopy(irClass, unifiedFields, unifiedNested)
            }
    }

    /**
     * Finds fields that are present (by name) in all classes from [matchingClasses].
     */
    private fun getCommonFields(matchingClasses: List<IrClass>): List<IrField> {
        if (matchingClasses.size < 2) return matchingClasses.firstOrNull()?.fields ?: emptyList()

        val fieldsHashMaps = matchingClasses
            .drop(1)
            .map { it.fields.associateBy { field -> field.name } }

        return matchingClasses.first().fields.mapNotNull { irField ->
            val matches = fieldsHashMaps
                .mapNotNull { it[irField.name] }
                .filter { foundIrField -> irField.type == foundIrField.type }

            if (matches.size == fieldsHashMaps.size) irField else null
        }
    }

    /**
     * Finds nested classes that are present (by name) in all [matchingClasses] and
     * recursively unifies their fields and nested classes.
     */
    private fun getCommonNestedClasses(matchingClasses: List<IrClass>): List<IrClass> {
        if (matchingClasses.isEmpty()) return emptyList()
        if (matchingClasses.size < 2) return matchingClasses.first().nestedClasses

        val nestedHashMaps = matchingClasses
            .drop(1)
            .map { it.nestedClasses.associateBy { nested -> nested.name } }

        return matchingClasses.first().nestedClasses.mapNotNull { nested ->
            val matches = nestedHashMaps.mapNotNull { it[nested.name] }
            if (matches.size < nestedHashMaps.size) return@mapNotNull null

            val allMatches = listOf(nested) + matches
            val unifiedFields = getCommonFields(allMatches)
            val unifiedNested = getCommonNestedClasses(allMatches)

            createUnifiedCopy(nested, unifiedFields, unifiedNested)
        }
    }

    private fun createUnifiedCopy(
        irClass: IrClass,
        commonFields: List<IrField>,
        commonNestedClasses: List<IrClass>,
    ): IrClass = IrClass(
        name = irClass.name,
        kind = irClass.kind,
        visibility = irClass.visibility,
        typeParameters = irClass.typeParameters,
        superClasses = irClass.superClasses,
        fields = commonFields,
        methods = irClass.methods,
        initializers = irClass.initializers,
        nestedClasses = commonNestedClasses,
        location = irClass.location,
        annotations = irClass.annotations,
        languageProperties = irClass.languageProperties,
    )
}
