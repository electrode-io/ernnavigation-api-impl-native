/*
 * Copyright 2021 Walmart Labs
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

import Foundation
import UIKit

open class ENBaseNavigationController: UINavigationController, ENMiniAppNavDataProvider {

    open var rootComponentName: String = ""
    open var properties: [AnyHashable : Any]?
    open var globalProperties: [AnyHashable : Any]?
    open var navigateWithRoute: NavigateWithRoute = { _ in
        return false
    }
    open var finish: Payload?
    open var backTo: BackToRoute = { _, _ in
        return false
    }

    public init() {
        super.init(nibName: nil, bundle: nil)
        self.rootComponentName = getRootComponentName()
        self.properties = getProps()
        self.globalProperties = globalProps()
        self.navigateWithRoute = { route in
            let path = route["path"] as? String ?? ""
            return self.navigate(path, route)
        }
        self.finish = { payload in
            self.finishFlow(payload)
        }
        self.backTo = { componentName, backProperties in
            return self.backToMiniApp(componentName, backProperties)
        }
    }

    public required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    open override func viewDidLoad() {
        super.viewDidLoad()
        let navDelegate = ENNavigationDelegate()
        navDelegate.viewDidLoad(viewController: self)
        addCloseButton()
    }

    // Override to provide React Native component name of root view
    open func getRootComponentName() -> String {
        return ""
    }

    // Override to provide properties to root view
    open func getProps() -> [AnyHashable : Any]? {
        return nil
    }

    // Override to provide global properties to all views
    open func globalProps() -> [AnyHashable : Any]? {
        return nil
    }

    // Override and return true to provide custom navigate implementation
    // return false to use default implementation
    open func navigate(_ path: String, _ route: [AnyHashable: Any]) -> Bool {
        return false
    }

    // Override to provide custom finish implementation
    open func finishFlow(_ payload: [AnyHashable: Any]?) {
        dismiss(animated: true)
    }

    // Override and return true to provide custom back to implementation
    // return false to use default implementation
    open func backToMiniApp(_ componentName: String, _ backProperties: [AnyHashable: Any]) -> Bool {
        return false
    }

    // Override and return false to remove default close button
    open func showCloseBarButton() -> Bool {
        return true
    }

    func addCloseButton() {
        if showCloseBarButton(){
            var closeButton: UIBarButtonItem
            if #available(iOS 13.0, *) {
                closeButton = UIBarButtonItem(barButtonSystemItem: .close, target: self, action: #selector(dismissModal))
            } else {
                closeButton = UIBarButtonItem(barButtonSystemItem: .done, target: self, action: #selector(dismissModal))
            }
            navigationBar.topItem?.leftBarButtonItem = closeButton
        }
    }

    @objc private func dismissModal(_ sender: UIButton) {
        dismiss(animated: true)
    }

    open func emitPayloadToCurrentRNView(_ jsonPayload: String?) {
        if let miniAppNavVC = viewControllers.last as? MiniAppNavViewController {
            miniAppNavVC.emitAppData(jsonPayload)
        }
    }
}
