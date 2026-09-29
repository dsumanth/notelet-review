//
//  SwiftUIView.swift
//  Notelet
//
//  Created by Mykola Harmash on 05.05.26.
//

import SwiftUI

struct BulletListNoteItemView: View {
    let title: LocalizedStringResource
    let rows: [NoteletVersionNoteItem.ListRow]
    let typography: NoteletTypography
    
    var body: some View {
        VStack(alignment: .leading, spacing: 40) {
            Text(title)
                .font(typography.pageTitle)
            
            VStack(alignment: .leading, spacing: 32) {
                ForEach(Array(rows.enumerated()), id: \.offset) { _, row in
                    HStack(alignment: .top, spacing: 18) {
                        Image(systemName: row.symbolSystemName)
                            .symbolRenderingMode(.hierarchical)
                            .font(.system(size: 32).weight(.semibold))
                            .foregroundStyle(.tint)
                            .frame(width: 48, alignment: .center)
                            .accessibilityHidden(true)
                        
                        VStack(alignment: .leading, spacing: 2) {
                            Text(row.title)
                                .font(typography.rowTitle)
                                .foregroundStyle(.primary)
                            Text(row.description)
                                .font(typography.body)
                                .foregroundStyle(.secondary)
                        }
                    }
                    .accessibilityElement(children: .combine)
                }
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.horizontal, 40)
        .padding(.top, 64)
        .padding(.bottom, 64)
    }
}
