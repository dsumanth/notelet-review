// swift-tools-version: 6.0

import PackageDescription

let package = Package(
    name: "NoteletReview",
    platforms: [
        .iOS(.v17),
    ],
    products: [
        .library(
            name: "NoteletReview",
            targets: ["NoteletReview"]
        ),
    ],
    targets: [
        .target(
            name: "NoteletReview"
        ),
        .testTarget(
            name: "NoteletReviewTests",
            dependencies: ["NoteletReview"]
        ),
    ],
    swiftLanguageModes: [.v6]
)
