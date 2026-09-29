import SwiftUI
import Testing
@testable import NoteletReview

@Suite struct NoteletConfigurationTests {
    @Test func inheritsTheHostAppTintByDefault() {
        #expect(NoteletConfiguration().accentColor == nil)
    }

    @Test func usesSemanticTextStylesByDefault() {
        // Semantic styles pick up the app's .fontDesign and Dynamic Type.
        let typography = NoteletConfiguration().typography
        #expect(typography.pageTitle == .title.bold())
        #expect(typography.mediaTitle == .title3.bold())
        #expect(typography.rowTitle == .body.weight(.semibold))
        #expect(typography.body == .body)
    }

    @Test func acceptsTheAppsOwnTypography() {
        let custom = NoteletTypography(pageTitle: .custom("Avenir-Heavy", size: 28, relativeTo: .title))
        #expect(NoteletConfiguration(typography: custom).typography.pageTitle == custom.pageTitle)
    }
}
