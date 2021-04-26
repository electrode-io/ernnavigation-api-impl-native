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

open class MiniAppNavViewController: UIViewController, ENNavigationProtocol, ENOverlayProtocol {
    let miniAppName: String
    var properties: [AnyHashable: Any]?
    public var finishedCallback: MiniAppFinishedCallback?
    public var finish: Payload?
    public var delegate: ENNavigationDelegate?
    public var navigateWithRoute: NavigateWithRoute?
    public var pushToExistingViewController: Bool = true
    public var globalProperties: [AnyHashable: Any]?

    public init(properties: [AnyHashable: Any]?, miniAppName: String) {
        self.miniAppName = miniAppName
        self.properties = properties ?? [AnyHashable: Any]()
        self.delegate = ENNavigationDelegate()
        super.init(nibName: nil, bundle: nil)
    }

    required public init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override open func viewDidLoad() {
        super.viewDidLoad()
        self.delegate?.viewDidLoad(viewController: self)
    }

    override open func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.delegate?.viewWillAppear()
    }

    override open func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        self.delegate?.viewDidAppear()
        ENNavigationAPIImpl.shared.navigationAPI.events.emitEventNavEvent(eventData: NavEventData(eventType: NavEventType.DID_FOCUS.rawValue, viewId: self.delegate?.viewIdentifier ?? "NOT_SET", jsonPayload: nil))
    }

    open override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        self.delegate?.viewWillDisappear()
    }

    open override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        self.delegate?.viewDidDisappear()
        ENNavigationAPIImpl.shared.navigationAPI.events.emitEventNavEvent(eventData: NavEventData(eventType: NavEventType.DID_BLUR.rawValue, viewId: self.delegate?.viewIdentifier ?? "NOT_SET", jsonPayload: nil))
    }

    func reloadView(ernNavRoute: [AnyHashable : Any]?) {
        self.delegate?.reloadView(viewController: self, ernNavRoute: ernNavRoute)
    }

    func handleNavigationRequestWithPath(routeData: [AnyHashable: Any], completion: ERNNavigationCompletionBlock) {
        self.delegate?.handleNavigationRequestWithPath(routeData: routeData, completion: completion)
    }

    func handleFinishFlow(finalPayLoad: String?, completion: @escaping ERNNavigationCompletionBlock) {
        self.delegate?.handleFinishFlow(finalPayload: finalPayLoad, completion: completion)
    }

    func popToViewControllerWithPath(ernNavRoute: [AnyHashable : Any]?, completion: @escaping ERNNavigationCompletionBlock) {
        self.delegate?.popToViewController(ernNavRoute: ernNavRoute, completion: completion)
    }

    func updateNavigationBar(navBar: NavigationBar, completion: @escaping ERNNavigationCompletionBlock) {
        self.properties?["navigationBar"] = navBar.toDictionary()
        self.delegate?.updateNavigationBar(navBar: navBar, completion: completion)
    }

    func presentOverlay(viewToPresent: UIViewController) {
        self.delegate?.presentOverlay(viewToPresent: viewToPresent)
    }

    func onDismissOverlay() {
        self.delegate?.onDismissOverlay()
    }

    func emitAppData(_ jsonPayload: String?) {
        let eventData = NavEventData(eventType: "APP_DATA", viewId: self.delegate?.viewIdentifier ?? "NOT_SET", jsonPayload: jsonPayload)
        ENNavigationAPIImpl.shared.navigationAPI.events.emitEventNavEvent(eventData: eventData)
    }

}

extension UINavigationController {
    func pushViewControllerWithoutBackButtonTitle(_ viewController: UIViewController, animated: Bool = true) {
        viewControllers.last?.navigationItem.backBarButtonItem = UIBarButtonItem(title: "", style: .plain, target: nil, action: nil)
        pushViewController(viewController, animated: animated)
    }
}
