package com.voxeldev.mcodegen.dsl.ir.builders

import com.voxeldev.mcodegen.dsl.ir.IrElement
import com.voxeldev.mcodegen.dsl.ir.IrFile
import com.voxeldev.mcodegen.dsl.ir.IrImport

/**
 * Creates a new [IrFileBuilder] instance with the given file name.
 */
fun irFile(name: String): IrFileBuilder = IrFileBuilder(name)

/**
 * Builder class for creating [IrFile] instances.
 */
class IrFileBuilder(private val name: String) {
    private var imports: MutableList<IrImport> = mutableListOf()
    private var declarations: MutableList<IrElement> = mutableListOf()
    private var languageProperties: MutableMap<String, Any> = mutableMapOf()

    fun addImport(import: IrImport) {
        imports.add(import)
    }

    fun addDeclaration(declaration: IrElement) {
        declarations.add(declaration)
    }

    fun addLanguageProperty(key: String, value: Any) {
        languageProperties[key] = value
    }

    fun build(): IrFile {
        return IrFile(
            name = name,
            imports = imports,
            declarations = declarations,
            languageProperties = languageProperties,
        )
    }
} 