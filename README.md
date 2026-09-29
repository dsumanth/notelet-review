# NoteletReview

Rich "What's new" release notes for **iOS (SwiftUI)** and **Android (Jetpack Compose)**, followed by the platform's rating prompt at the moment it is most likely to land well.

The iOS package vendors [Notelet](https://github.com/mykolaharmash/notelet) by Mykola Harmash (MIT); the Android library is a Compose port of it. Not affiliated with the original project.

```
Package.swift, Sources/, Tests/   iOS Swift package (repo root, required by SwiftPM)
android/                          Android Gradle project, library module :notelet-review
```

Both platforms share the same API shape, the same review policy and the same version tags.

## Why

Showing release notes, then asking for a rating right after the user closes the sheet, works well:

- There's a good chance the update ships something people were waiting for.
- It's a purely positive moment.
- You have already interrupted the user (for a good reason), so the prompt feels less jarring.

## The review request is built in

Neither platform exposes the sheet without the review logic. iOS uses SwiftUI's `requestReview` (StoreKit); Android uses the [Play In-App Review API](https://developer.android.com/guide/playcore/in-app-review). Both apply Apple's rules, which are stricter than Play's and satisfy both stores. The request only fires when all of these hold:

| Rule | Source |
|---|---|
| Only the system prompt is used, no "Enjoying the app?" pre-prompt | App Review Guideline 5.6.1, Play in-app review guidelines |
| Not on first launch or during onboarding (the user must be updating from a version they already saw) | HIG, Ratings and reviews |
| Not in response to a user action (manually opened changelogs never ask) | HIG; Play: no button that triggers the flow |
| At least 14 days between requests | HIG: "allow a week or two between requests" |
| At most once per app version | Apple sample code, "Requesting App Store reviews" |
| Fired 2 seconds after the sheet closes, never on top of it | Apple sample code |

The stores add their own limits (Apple: 3 displays per 365 days) and may show nothing, so treat it as a request. On iOS the prompt always appears in development builds and never in TestFlight. On Android it only appears for Play-installed builds; use internal app sharing or internal testing.

## Adapts to your app's design

| | iOS | Android |
|---|---|---|
| Accent | Your app's tint (`.tint(...)` or the `AccentColor` asset) | `MaterialTheme.colorScheme.primary` / `onPrimary` |
| Type | Semantic text styles, so `.fontDesign(...)` and Dynamic Type apply | `headlineMedium`, `titleLarge`, `titleMedium`, `bodyLarge` from your typography |
| Shapes and surfaces | System sheet and materials | `shapes.large`, Material 3 bottom sheet, your `Button` shape |
| Dark mode, locale | Inherited from the environment | Follows your color scheme and resources |

Everything can be overridden with `NoteletConfiguration`: button labels, accent, typography (including custom typefaces) and sheet height.

## iOS

Requires iOS 17. In Xcode: **File > Add Package Dependencies...**, enter this repo's URL, and add `NoteletReview` to your app target.

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

`version: .current` reads `CFBundleShortVersionString`, shows that version's notes once, and marks it as seen on dismiss. For a manual changelog (for example from Settings) use `version: .v("1.2.0")`, driven from `@State`.

Overrides:

```swift
.noteletReviewSheet(
    notes: RELEASE_NOTES,
    version: .current,
    configuration: .init(
        doneButtonLabel: "Got it",
        accentColor: .orange,
        typography: .init(pageTitle: .custom("Avenir-Heavy", size: 28, relativeTo: .title)),
        sheetHeight: .full
    )
)
```

All text is `LocalizedStringResource`, so it lands in your String Catalog. `NoteletVersionNotes` is `Codable`, so notes can be loaded remotely.

## Android

Requires minSdk 24 and Compose Material 3. Through [JitPack](https://jitpack.io) once the repo is on GitHub and tagged:

```kotlin
// settings.gradle.kts, inside dependencyResolutionManagement.repositories
maven("https://jitpack.io")

// app/build.gradle.kts
implementation("com.github.<your-github-user>:notelet-review:<tag>")
```

Or include the module from a local checkout:

```kotlin
// settings.gradle.kts
include(":notelet-review")
project(":notelet-review").projectDir = file("../notelet-review/android/notelet-review")

// app/build.gradle.kts
implementation(project(":notelet-review"))
```

```kotlin
val releaseNotes = listOf(
    VersionNotes("1.2.0", listOf(
        NoteItem.List("What's new", listOf(
            ListRow(Icons.Filled.AutoAwesome, "New editor tools", "More formatting with fewer taps."),
        )),
        NoteItem.Media(MediaKind.Image, "https://example.com/new-ui.jpg", "Updated UI", "Refreshed visuals across key screens."),
        NoteItem.Media(MediaKind.Video, "https://example.com/walkthrough.mp4", "Quick walkthrough", "A short clip of the new flow."),
    )),
)

@Composable
fun App() {
    AppTheme {
        HomeScreen()
        NoteletReviewSheet(notes = releaseNotes, version = PresentedVersion.Current)
    }
}
```

`PresentedVersion.Current` reads your `versionName`. For a manual changelog pass `PresentedVersion.Specific("1.2.0")` from state and reset it to `null` in `onDismiss`. For localized notes, build the list inside a composable with `stringResource(...)`.

Overrides:

```kotlin
NoteletReviewSheet(
    notes = releaseNotes,
    version = PresentedVersion.Current,
    configuration = NoteletConfiguration(
        doneButtonLabel = "Got it",
        accentColor = Color(0xFFFF9800),
        typography = NoteletTypography(pageTitle = MaterialTheme.typography.displaySmall),
        sheetHeight = SheetHeight.Full,
    ),
)
```

## New users

When onboarding finishes, mark the current version as seen so new users only see notes (and the prompt) on their next update:

- iOS: `NoteletStorage.markCurrentVersionAsSeen()`
- Android: `NoteletStorage.markCurrentVersionAsSeen(context)`

`resetSeenVersion` on either platform helps while debugging.

## Tests

```
xcodebuild test -scheme NoteletReview -destination 'platform=iOS Simulator,name=iPhone 17'
cd android && ./gradlew :notelet-review:testDebugUnitTest
```

## License

MIT. See [LICENSE](LICENSE).
