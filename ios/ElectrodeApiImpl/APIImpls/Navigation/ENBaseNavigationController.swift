//
//  ENBaseNavigationController.swift
//  ElectrodeApiImpl
//
//  Created by Jeffrey Wang on 3/17/21.
//  Copyright © 2021 Walmart. All rights reserved.
//

import Foundation
import UIKit

class ENBaseNavigationController: UINavigationController, ENMiniAppNavDataProvider {

    var rootComponentName: String
    var properties: [AnyHashable : Any]?
    var navigateWithRoute: NavigateWithRoute = { _ in
        return false
    }
    var finish: Payload?
    var backToMiniApp: BackToRoute = { _, _ in
        return false
    }

    init(properties: [AnyHashable : Any]?, rootComponentName: String) {
        self.rootComponentName = rootComponentName
        self.properties = properties
        super.init(nibName: nil, bundle: nil)
        self.navigateWithRoute = { route in
            let path = route["path"] as? String ?? ""
            return self.navigate(path, route)
        }
        self.finish = { payload in
            self.finishFlow(payload)
        }
        self.backToMiniApp = { componentName, backProperties in
            return self.backToMiniApp(componentName, backProperties)
        }
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        let navDelegate = ENNavigationDelegate()
        navDelegate.viewDidLoad(viewController: self)
        addCloseButton()
    }

    // Override and return true to provide custom navigate implementation
    // return false to use default implementation
    func navigate(_ path: String, _ route: [AnyHashable: Any]) -> Bool {
        return false
    }

    // Override to provide custom finish implementation
    func finishFlow(_ payload: [AnyHashable: Any]?) {
        dismiss(animated: true)
    }

    // Override and return true to provide custom back to implementation
    // return false to use default implementation
    func backToMiniApp(_ componentName: String, _ backProperties: [AnyHashable: Any]) -> Bool {
        return false
    }

    // Override and return false to remove default close button
    func showCloseBarButton() -> Bool {
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

}
