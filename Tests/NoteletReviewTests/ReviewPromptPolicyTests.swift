import Foundation
import Testing
@testable import NoteletReview

@Suite struct ReviewPromptPolicyTests {
    private let now = Date(timeIntervalSince1970: 1_800_000_000)
    private let day: TimeInterval = 24 * 60 * 60

    private func decide(
        wasAutoPresented: Bool = true,
        previouslySeenVersion: String? = "1.0",
        currentVersion: String = "1.1",
        lastPromptedVersion: String? = nil,
        lastPromptDate: Date? = nil
    ) -> Bool {
        ReviewPromptPolicy.shouldRequestReview(
            wasAutoPresented: wasAutoPresented,
            previouslySeenVersion: previouslySeenVersion,
            currentVersion: currentVersion,
            lastPromptedVersion: lastPromptedVersion,
            lastPromptDate: lastPromptDate,
            now: now
        )
    }

    @Test func asksAfterAutoShownNotesForAnUpdatingUser() {
        #expect(decide())
    }

    @Test func neverAsksAfterNotesOpenedManually() {
        // HIG: never tie the request to a user action like a "What's new" button.
        #expect(!decide(wasAutoPresented: false))
    }

    @Test func neverAsksOnFreshInstall() {
        // HIG: don't ask on first launch or during onboarding.
        #expect(!decide(previouslySeenVersion: nil))
    }

    @Test func asksAtMostOncePerVersion() {
        #expect(!decide(lastPromptedVersion: "1.1", lastPromptDate: now.addingTimeInterval(-90 * day)))
    }

    @Test func waitsAtLeastTwoWeeksBetweenAsks() {
        // HIG: avoid pestering, allow a week or two between requests.
        #expect(!decide(lastPromptedVersion: "1.0", lastPromptDate: now.addingTimeInterval(-13 * day)))
        #expect(decide(lastPromptedVersion: "1.0", lastPromptDate: now.addingTimeInterval(-14 * day)))
    }
}

@Suite struct ReviewPromptStoreTests {
    private func makeDefaults() -> UserDefaults {
        let name = "ReviewPromptStoreTests.\(UUID().uuidString)"
        let defaults = UserDefaults(suiteName: name)!
        defaults.removePersistentDomain(forName: name)
        return defaults
    }

    @Test func startsEmpty() {
        let store = ReviewPromptStore(userDefaults: makeDefaults())
        #expect(store.lastPromptedVersion == nil)
        #expect(store.lastPromptDate == nil)
    }

    @Test func recordsThePrompt() {
        let defaults = makeDefaults()
        let date = Date(timeIntervalSince1970: 1_800_000_000)
        ReviewPromptStore(userDefaults: defaults).recordPrompt(version: "2.0", at: date)

        let reloaded = ReviewPromptStore(userDefaults: defaults)
        #expect(reloaded.lastPromptedVersion == "2.0")
        #expect(reloaded.lastPromptDate == date)
    }
}
