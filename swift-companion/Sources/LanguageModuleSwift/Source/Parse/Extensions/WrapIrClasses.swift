//
//  WrapIrClass.swift
//  swift-companion
//
//  Created by nVoxel on 19.06.2025.
//

func wrapIrClasses(irClasses: [IrClass]) -> [IrElement] {
    return irClasses.map({ IrElement.Class($0) })
}
