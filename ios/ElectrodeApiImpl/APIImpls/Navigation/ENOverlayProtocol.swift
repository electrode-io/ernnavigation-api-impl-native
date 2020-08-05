//
//  ENOverlayProtocol.swift
//  ElectrodeContainer
//
//  Created by Jeffrey Wang on 8/5/20.
//  Copyright © 2020 Walmart. All rights reserved.
//

import Foundation

protocol ENOverlayProtocol: class {
    func onDismissOverlay() // Previous overlay is dismissed, use this method to restore state, ie: navigation bar state
}
