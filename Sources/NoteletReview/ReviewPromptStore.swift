import Foundation

/// Persists when the review request was last made, so `ReviewPromptPolicy`
/// can enforce once-per-version and the minimum interval.
struct ReviewPromptStore {
    private enum Key {
        static let lastPromptedVersion = "NoteletReview.LastPromptedVersion"
        static let lastPromptDate = "NoteletReview.LastPromptDate"
    }

    let userDefaults: UserDefaults

    var lastPromptedVersion: String? {
        userDefaults.string(forKey: Key.lastPromptedVersion)
    }

    var lastPromptDate: Date? {
        userDefaults.object(forKey: Key.lastPromptDate) as? Date
    }

    func recordPrompt(version: String, at date: Date) {
        userDefaults.set(version, forKey: Key.lastPromptedVersion)
        userDefaults.set(date, forKey: Key.lastPromptDate)
    }
}
