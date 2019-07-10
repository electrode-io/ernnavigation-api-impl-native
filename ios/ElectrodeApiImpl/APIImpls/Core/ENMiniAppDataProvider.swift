//
//  ENMiniAppDataProvider.swift
//  ErnRunner
//
//  Created by Lianci Liu on 6/27/19.
//  Copyright © 2019 Walmart. All rights reserved.
//

import Foundation

@objc public protocol ENMiniAppDataProvider: class {
    var miniAppName: String { get }
    var properties: [AnyHashable : Any]? { get set }
}
