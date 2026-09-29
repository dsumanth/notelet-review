# NoteletReview (iOS)

Rich "What's new" release notes for SwiftUI apps, followed by the App Store rating prompt, the moment it is most likely to land well.

Built on [Notelet](https://github.com/mykolaharmash/notelet) by Mykola Harmash (MIT, vendored in `Sources/NoteletReview/Notelet`). Not affiliated with the original project. Android counterpart: **notelet-review-android**.

## Why

Showing release notes, then asking for a rating right after the user closes the sheet, works well:

- There's a good chance the update ships something people were waiting for.
- It's a purely positive moment.
- You have already interrupted the user (for a good reason), so the prompt feels less jarring.

## Install (Swift Package Manager)

In Xcode: **File > Add Package Dependencies...**, enter this repo's URL, and add `NoteletReview` to your app target. Requires iOS 17.

## Usage

```swift
import SwiftUI
import NoteletReview

let RELEASE_NOTES: [NoteletVersionNotes] = [
    .init(version: "1.2.0", items: [
        .list(title: "What's new", rows: [
            .init(symbolSystemName: "wand.and.stars", title: "New editor tools", description: "More formatting with fewer taps."),
        ]),
        .media(kind: .image, url: URL(string: "https://example.com/new-ui.jpg")!,
               title: "Updated UI", description: "Refreshed visuals across key screens."),
        .media(kind: .video, url: URL(string: "https://example.com/walkthrough.mp4")!,
               title: "Quick walkthrough", description: "A short clip of the new flow."),
    ]),
]

struct ContentView: View {
    var body: some View {
        HomeView()
            .noteletReviewSheet(notes: RELEASE_NOTES, version: .current)
    }
}
```

`version: .current` reads `CFBundleShortVersionString`, shows that version's notes once, and marks it as seen on dismiss. To show a changelog manually (for example from Settings) use `version: .v("1.2.0")` or drive it from `@State`. Manual presentations never trigger the review prompt.

When onboarding finishes, call `NoteletStorage.markCurrentVersionAsSeen()` so new users only see notes (and the prompt) on their next update. `NoteletStorage.resetSeenVersion()` helps while debugging.

## The review request is built in

There is no public way to present the sheet without the review logic. The request goes through SwiftUI's `requestReview` (StoreKit) and only fires when all of these hold:

| Rule | Source |
|---|---|
| Only the system prompt is used, no custom "Enjoying the app?" pre-prompt | App Review Guideline 5.6.1 |
| Not on first launch or during onboarding (the user must be updating from a version they already saw) | HIG, Ratings and reviews |
| Not in response to a user action (manual `.v(...)` changelogs never ask) | HIG, Ratings and reviews |
| At least 14 days between requests | HIG: "allow a week or two between requests" |
| At most once per app version | Apple sample code, "Requesting App Store reviews" |
| Fired 2 seconds after the sheet closes, never on top of it | Apple sample code |

Apple additionally limits the prompt to 3 displays per 365 days and may show nothing, so treat it as a request. In development builds the prompt always appears; in TestFlight it never does.

## Adapts to your app's design

Every visual default comes from your app:

- **Accent**: inherits your app's tint (`.tint(...)` or the `AccentColor` asset).
- **Type**: semantic text styles, so `.fontDesign(.rounded)`, `.serif` etc. and Dynamic Type apply.
- **Light/dark mode, locale, layout direction**: inherited from the environment.

Override anything with `NoteletConfiguration`:

```swift
.noteletReviewSheet(
    notes: RELEASE_NOTES,
    version: .current,
    configuration: .init(
        nextButtonLabel: "Continue",
        doneButtonLabel: "Got it",
        accentColor: .orange,
        typography: .init(
            pageTitle: .custom("Avenir-Heavy", size: 28, relativeTo: .title),
            body: .custom("Avenir-Book", size: 17, relativeTo: .body)
        ),
        sheetHeight: .full
    )
)
```

All text is `LocalizedStringResource`, so it lands in your String Catalog. `NoteletVersionNotes` is `Codable`, so you can load notes remotely.

## Tests

```
xcodebuild test -scheme NoteletReview -destination 'platform=iOS Simulator,name=iPhone 17'
```

## License

MIT. See [LICENSE](LICENSE).
