//
//  ENMiniAppNavDataProvider.swift
//  ErnRunner
//
//  Created by Lianci Liu on 6/27/19.
//  Copyright © 2019 Walmart. All rights reserved.
//

import Foundation

typealias MiniAppFinishedCallback = (String?) -> Void

protocol ENMiniAppNavDataProvider: ENMiniAppDataProvider {
    var finishedCallback: MiniAppFinishedCallback? { get set }
}
