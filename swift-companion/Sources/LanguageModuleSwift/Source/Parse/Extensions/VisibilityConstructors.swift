//
//  VisibilityConstructors.swift
//  swift-companion
//
//  Created by nVoxel on 19.06.2025.
//

func swiftVisibilityPrivate() -> IrVisibility {
    return IrVisibility.VisibilityPrivate(
        IrVisibilityPrivate(
            stringRepresentation: [
                IrStringRepresentation(
                    language: languageName,
                    representation: "private"
                )
            ]
        )
    )
}

func swiftVisibilityPublic() -> IrVisibility {
    return IrVisibility.VisibilityPublic(
        IrVisibilityPublic(
            stringRepresentation: [
                IrStringRepresentation(
                    language: languageName,
                    representation: "public"
                )
            ]
        )
    )
}

func swiftVisibilityInternal() -> IrVisibility {
    return IrVisibility.VisibilityInternal(
        IrVisibilityInternal(
            stringRepresentation: [
                IrStringRepresentation(
                    language: languageName,
                    representation: "internal"
                )
            ]
        )
    )
}

func swiftVisibilityOpen() -> IrVisibility {
    return IrVisibility.VisibilityOpen(
        IrVisibilityOpen(
            stringRepresentation: [
                IrStringRepresentation(
                    language: languageName,
                    representation: "open"
                )
            ]
        )
    )
}

func swiftVisibilityFileprivate() -> IrVisibility {
    return IrVisibility.VisibilityFileprivate(
        IrVisibilityFileprivate(
            stringRepresentation: [
                IrStringRepresentation(
                    language: languageName,
                    representation: "fileprivate"
                )
            ]
        )
    )
}
