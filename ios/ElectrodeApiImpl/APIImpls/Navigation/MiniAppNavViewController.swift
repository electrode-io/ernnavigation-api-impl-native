/*
 * Copyright 2019 Walmart Labs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import UIKit

open class MiniAppNavViewController: UIViewController, ENNavigationProtocol {
    let miniAppName: String
    let properties: [AnyHashable: Any]?
    public var finishedCallback: MiniAppFinishedCallback?
    public var finish: Payload?
    var delegate: ENNavigationDelegate?
    public var navigateWithRoute: NavigateWithRoute?
    public var globalProperties: [AnyHashable: Any]?
    public init(properties: [AnyHashable: Any]?, miniAppName: String) {
        self.miniAppName = miniAppName
        self.properties = properties
        super.init(nibName: nil, bundle: nil)
    }

    required public init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override open func viewDidLoad() {
        super.viewDidLoad()
        self.delegate = ENNavigationDelegate()
        self.delegate?.viewDidLoad(viewController: self)
        self.delegate?.delegate = self
    }

    override open func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.delegate?.viewWillAppear()
    }

    func reloadView(ernNavRoute: [AnyHashable : Any]?) {
        self.delegate?.reloadView(viewController: self, ernNavRoute: ernNavRoute)
    }

    open override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        self.delegate?.viewDidDisapper()
    }

    func handleNavigationRequestWithPath(routeData: [AnyHashable: Any], completion: ERNNavigationCompletionBlock) {
        self.delegate?.handleNavigationRequestWithPath(routeData: routeData, completion: completion)
    }

    func handleFinishFlow(finalPayLoad: String?, completion: @escaping ERNNavigationCompletionBlock) {
        self.delegate?.handleFinishFlow(finalPayload: finalPayLoad, completion: completion)
    }

    func popToViewControllerWithPath(ernNavRoute: [AnyHashable : Any]?, completion: ERNNavigationCompletionBlock) {
        self.delegate?.popToViewController(ernNavRoute: ernNavRoute, completion: completion)
    }

    func updateNavigationBar(navBar: NavigationBar, completion: @escaping ERNNavigationCompletionBlock) {
        self.delegate?.updateNavigationBar(navBar: navBar, completion: completion)
    }
}
