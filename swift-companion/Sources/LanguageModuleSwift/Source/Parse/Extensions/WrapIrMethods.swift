//
//  WrapIrMethods.swift
//  swift-companion
//
//  Created by nVoxel on 19.06.2025.
//

func wrapIrMethods(irMethods: [IrMethod]) -> [IrElement] {
    return irMethods.map({ IrElement.Method($0) })
}
