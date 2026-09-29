import Foundation

/// Decides whether dismissing the release notes sheet should be followed by
/// the system review request. Every rule maps to Apple's guidance:
///
/// - App Review Guideline 5.6.1: only the system API may ask for a rating,
///   so there is no custom pre-prompt anywhere in this package.
/// - HIG "Ratings and reviews": don't ask on first launch or during
///   onboarding, don't ask in response to a user action (such as a
///   "What's new" button), and avoid pestering (allow a week or two
///   between requests).
/// - Apple's "Requesting App Store reviews" sample: ask at most once per
///   app version.
///
/// The system also caps the prompt at 3 displays per 365 days and may show
/// nothing at all, which is why this is a request, never a guarantee.
enum ReviewPromptPolicy {
    static let minimumIntervalBetweenPrompts: TimeInterval = 14 * 24 * 60 * 60

    static func shouldRequestReview(
        wasAutoPresented: Bool,
        previouslySeenVersion: String?,
        currentVersion: String,
        lastPromptedVersion: String?,
        lastPromptDate: Date?,
        now: Date
    ) -> Bool {
        guard wasAutoPresented else { return false }
        guard let previouslySeenVersion, previouslySeenVersion != currentVersion else { return false }
        guard lastPromptedVersion != currentVersion else { return false }
        guard let lastPromptDate else { return true }
        return now.timeIntervalSince(lastPromptDate) >= minimumIntervalBetweenPrompts
    }
}
