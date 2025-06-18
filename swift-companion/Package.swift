// swift-tools-version: 6.1
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "swift-companion",
    platforms: [
        .macOS(.v13),
        .iOS(.v13)
    ],
    dependencies: [
        .package(url: "https://github.com/apple/swift-argument-parser.git", from: "1.2.0"),
        .package(url: "https://github.com/Flight-School/AnyCodable.git", from: "0.6.7"),
        .package(url: "https://github.com/jpsim/SourceKitten.git", from: "0.37.2"),
        .package(url: "https://github.com/CheekyGhost-Labs/SyntaxSparrow.git", from: "5.1.0")
    ],
    targets: [
        // Targets are the basic building blocks of a package, defining a module or a test suite.
        // Targets can depend on other targets in this package and products from dependencies.
        .executableTarget(
            name: "swift-companion",
            dependencies: [
                .product(name: "ArgumentParser", package: "swift-argument-parser"),
                .product(name: "AnyCodable", package: "AnyCodable"),
                .product(name: "SourceKittenFramework", package: "SourceKitten"),
                .product(name: "SyntaxSparrow", package: "SyntaxSparrow")
            ]
        ),
    ]
)
