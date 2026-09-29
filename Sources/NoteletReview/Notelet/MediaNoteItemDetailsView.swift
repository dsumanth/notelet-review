//
//  File.swift
//  Notelet
//
//  Created by Mykola Harmash on 05.05.26.
//

import SwiftUI

struct MediaNoteItemDetailsView: View {
    let title: LocalizedStringResource
    let description: LocalizedStringResource
    let typography: NoteletTypography
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(title)
                .font(typography.mediaTitle)
            Text(description)
                .font(typography.body)

        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .multilineTextAlignment(.leading)
        .fixedSize(horizontal: false, vertical: true)
        .padding(.horizontal, 30)
        .padding(.bottom, 30)
        .foregroundStyle(.primary)
    }
}
