//
//  ENNavigationProtocol.swift
//  ErnRunner
//
//  Created by Lianci Liu on 6/27/19.
//  Copyright © 2019 Walmart. All rights reserved.
//

import Foundation

typealias ERNNavigationCompletionBlock = (String) -> Void

protocol ENNavigationProtocol: class {
    func handleNavigationRequestWithPath(routeData: [AnyHashable: Any], completion: ERNNavigationCompletionBlock)
    func handleFinishFlow(finalPayLoad: String?, completion: @escaping ERNNavigationCompletionBlock)
    func popToViewControllerWithPath(path: String?, completion: ERNNavigationCompletionBlock)
    func updateNavigationBar(navBar: NavigationBar, completion: @escaping ERNNavigationCompletionBlock)
}
