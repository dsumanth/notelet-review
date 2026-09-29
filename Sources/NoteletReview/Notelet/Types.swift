//
//  types.swift
//  Notelet
//
//  Created by Mykola Harmash on 05.05.26.
//

import Foundation
import SwiftUI

public enum NoteletVersionNoteItem: Sendable, Codable {
    case media(kind: MediaKind, url: URL, title: LocalizedStringResource, description: LocalizedStringResource)
    case list(title: LocalizedStringResource, rows: [ListRow])
    
    public enum MediaKind: Sendable, Codable {
        case image
        case video
    }
    
    public struct ListRow: Sendable, Codable {
        public init(symbolSystemName: String, title: LocalizedStringResource, description: LocalizedStringResource) {
            self.symbolSystemName = symbolSystemName
            self.title = title
            self.description = description
        }
        
        let symbolSystemName: String
        let title: LocalizedStringResource
        let description: LocalizedStringResource
    }
}

public struct NoteletVersionNotes: Sendable, Codable {
    public init(version: String, items: [NoteletVersionNoteItem]) {
        self.version = version
        self.items = items
    }
    
    let version: String
    let items: [NoteletVersionNoteItem]
}

public enum NoteletPresentedVersion: Sendable, Hashable {
    case current
    case v(String)
}

/// How tall the sheet is on iPhone. iPad always uses the large detent.
public enum NoteletSheetHeight: Sendable {
    /// 85% of the screen, leaving a sliver of the presenting view visible.
    case standard
    /// The full large detent.
    case full
}

/// Fonts used by the sheet. Defaults are semantic text styles, so they follow
/// the app's `.fontDesign(...)` and Dynamic Type. Pass
/// `Font.custom(_:size:relativeTo:)` to use the app's own typeface.
public struct NoteletTypography: Sendable {
    let pageTitle: Font
    let mediaTitle: Font
    let rowTitle: Font
    let body: Font

    public init(
        pageTitle: Font = .title.bold(),
        mediaTitle: Font = .title3.bold(),
        rowTitle: Font = .body.weight(.semibold),
        body: Font = .body
    ) {
        self.pageTitle = pageTitle
        self.mediaTitle = mediaTitle
        self.rowTitle = rowTitle
        self.body = body
    }
}

public struct NoteletConfiguration: Sendable {
    let nextButtonLabel: LocalizedStringResource
    let doneButtonLabel: LocalizedStringResource
    /// `nil` inherits the host app's tint.
    let accentColor: Color?
    let typography: NoteletTypography
    let sheetHeight: NoteletSheetHeight
    
    public init(
        nextButtonLabel: LocalizedStringResource = "Next",
        doneButtonLabel: LocalizedStringResource = "Done",
        accentColor: Color? = nil,
        typography: NoteletTypography = .init(),
        sheetHeight: NoteletSheetHeight = .standard
    ) {
        self.nextButtonLabel = nextButtonLabel
        self.doneButtonLabel = doneButtonLabel
        self.accentColor = accentColor
        self.typography = typography
        self.sheetHeight = sheetHeight
    }
}
