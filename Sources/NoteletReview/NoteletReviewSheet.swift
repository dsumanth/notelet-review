
import StoreKit
import SwiftUI

/// Wraps Notelet's `noteletSheet` so that closing the release notes is
/// always followed by the system review request, gated by
/// `ReviewPromptPolicy`.
struct NoteletReviewSheet: ViewModifier {
    /// Apple's sample code waits two seconds after the triggering moment so
    /// the prompt never lands on top of the dismissal.
    private static let promptDelay: Duration = .seconds(2)

    let notes: [NoteletVersionNotes]
    let version: NoteletPresentedVersion?
    let onDismiss: () -> Void
    let configuration: NoteletConfiguration
    let userDefaults: UserDefaults
    let canRequestReview: () -> Bool
    let onReviewRequested: () -> Void

    @Environment(\.requestReview) private var requestReview
    /// Captured before Notelet marks the current version as seen, so a
    /// fresh install (nothing seen yet) can be told apart from an update.
    @State private var previouslySeenVersion: String?

    func body(content: Content) -> some View {
        content
            .onAppear {
                previouslySeenVersion = NoteletStorage.getLatestSeenAppVersion(userDefaults: userDefaults)
            }
            .noteletSheet(
                notes: notes,
                version: version,
                onDismiss: handleDismiss,
                configuration: configuration,
                userDefaults: userDefaults
            )
    }

    private func handleDismiss() {
        onDismiss()

        let currentVersion = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "0"
        let store = ReviewPromptStore(userDefaults: userDefaults)
        let now = Date()
        let shouldAsk = ReviewPromptPolicy.shouldRequestReview(
            wasAutoPresented: version == .current,
            hostAllowsReview: canRequestReview(),
            previouslySeenVersion: previouslySeenVersion,
            currentVersion: currentVersion,
            lastPromptedVersion: store.lastPromptedVersion,
            lastPromptDate: store.lastPromptDate,
            now: now
        )
        previouslySeenVersion = currentVersion
        guard shouldAsk else { return }

        store.recordPrompt(version: currentVersion, at: now)
        onReviewRequested()
        Task { @MainActor in
            try? await Task.sleep(for: Self.promptDelay)
            requestReview()
        }
    }
}

extension View {
    /// Attach a Notelet release notes sheet that asks for an App Store
    /// rating right after the user closes it.
    ///
    /// Takes the same parameters as Notelet's `noteletSheet`. The review
    /// request only fires when the sheet was shown automatically for an app
    /// update (`version: .current`), at most once per version and at least
    /// two weeks apart. See `ReviewPromptPolicy` for the full rule set.
    ///
    /// - Parameters:
    ///   - canRequestReview: Return `false` to skip the request, for example
    ///     when the app asked for a review recently from another screen.
    ///   - onReviewRequested: Called when the request is made, so the app can
    ///     count it against its own review budget.
    public func noteletReviewSheet(
        notes: [NoteletVersionNotes],
        version: NoteletPresentedVersion? = nil,
        onDismiss: @escaping () -> Void = { },
        configuration: NoteletConfiguration = .init(),
        userDefaults: UserDefaults = .standard,
        canRequestReview: @escaping () -> Bool = { true },
        onReviewRequested: @escaping () -> Void = { }
    ) -> some View {
        modifier(
            NoteletReviewSheet(
                notes: notes,
                version: version,
                onDismiss: onDismiss,
                configuration: configuration,
                userDefaults: userDefaults,
                canRequestReview: canRequestReview,
                onReviewRequested: onReviewRequested
            )
        )
    }
}
