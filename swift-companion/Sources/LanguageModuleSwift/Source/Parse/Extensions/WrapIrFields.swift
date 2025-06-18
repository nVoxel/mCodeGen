//
//  WrapIrFields.swift
//  swift-companion
//
//  Created by nVoxel on 19.06.2025.
//

func wrapIrFields(irFields: [IrField]) -> [IrElement] {
    return irFields.map({ IrElement.Field($0) })
}
