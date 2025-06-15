package com.voxeldev.mcodegen.dsl.language.swift.ir

import com.voxeldev.mcodegen.dsl.ir.IrElement
import com.voxeldev.mcodegen.dsl.ir.builders.IrElementBuilder

const val SWIFT_ELEMENT_MODULE = "swiftElementModule"

/**
 * @return Swift module name followed by `.` or just `.` if the module name was empty.
 */
fun IrElement.getSwiftElementModule() = (languageProperties[SWIFT_ELEMENT_MODULE] as? String)?.let { moduleName ->
    "${moduleName}."
} ?: "."

fun IrElementBuilder.setSwiftElementModule(moduleName: String) {
    addLanguageProperty(SWIFT_ELEMENT_MODULE, moduleName)
}
