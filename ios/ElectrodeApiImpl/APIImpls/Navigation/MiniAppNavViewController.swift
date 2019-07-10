//
//  MiniAppNavViewController.swift
//  ErnRunner
//
//  Created by Lianci Liu on 6/27/19.
//  Copyright © 2019 Walmart. All rights reserved.
//

import UIKit

class MiniAppNavViewController: UIViewController, ENNavigationProtocol {
    let miniAppName: String
    let properties: [AnyHashable : Any]?
    var finishedCallback: MiniAppFinishedCallback?
    var delegate: ENNavigationDelegate?
    init(properties: [AnyHashable: Any]?, miniAppName: String) {
        self.miniAppName = miniAppName
        self.properties = properties
        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        self.delegate = ENNavigationDelegate()
        self.delegate?.viewDidLoad(viewController: self)
        self.delegate?.delegate = self
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.delegate?.viewWillAppear()
    }

    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        self.delegate?.viewDidDisapper()
    }

    func handleNavigationRequestWithPath(routeData: [AnyHashable: Any], completion: ERNNavigationCompletionBlock) {
        self.delegate?.handleNavigationRequestWithPath(routeData: routeData, completion: completion)
    }

    func handleFinishFlow(finalPayLoad: String?, completion: @escaping ERNNavigationCompletionBlock) {
        self.delegate?.handleFinishFlow(finalPayload: finalPayLoad, completion: completion)
    }

    func popToViewControllerWithPath(path: String?, completion: ERNNavigationCompletionBlock) {
        self.delegate?.popToViewController(path: path, completion: completion)
    }

    func updateNavigationBar(navBar: NavigationBar, completion: @escaping ERNNavigationCompletionBlock) {
        self.delegate?.updateNavigationBar(navBar: navBar, completion: completion)
    }
}
