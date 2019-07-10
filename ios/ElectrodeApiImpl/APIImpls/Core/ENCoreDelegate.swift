//
//  ENCoreDelegate.swift
//  ErnRunner
//
//  Created by Lianci Liu on 6/27/19.
//  Copyright © 2019 Walmart. All rights reserved.
//

import UIKit

@objcMembers public class ENCoreDelegate: NSObject {
    var viewController: MiniAppNavViewController?

    public func viewDidLoad(viewController: UIViewController) {
        if let miniAppVC = viewController as? MiniAppNavViewController {
            if let v = self.createView(name: miniAppVC.miniAppName, properties: miniAppVC.properties) {
                viewController.view.addSubview(v)
                if #available(iOS 11.0, *) {
                    v.frame = viewController.view.safeAreaLayoutGuide.layoutFrame
                    let guide = viewController.view.safeAreaLayoutGuide
                    NSLayoutConstraint.activate([
                        NSLayoutConstraint(item: v, attribute: .top, relatedBy: .equal, toItem: guide, attribute: .top, multiplier: 1, constant: 0),
                        NSLayoutConstraint(item: v, attribute: .bottom, relatedBy: .equal, toItem: guide, attribute: .bottom, multiplier: 1, constant: 0),
                        NSLayoutConstraint(item: v, attribute: .leading, relatedBy: .equal, toItem: guide, attribute: .leading, multiplier: 1, constant: 0),
                        NSLayoutConstraint(item: v, attribute: .trailing, relatedBy: .equal, toItem: guide, attribute: .trailing, multiplier: 1, constant: 0)
                        ])
                } else {
                    NSLayoutConstraint.activate([
                        NSLayoutConstraint(item: v, attribute: .top, relatedBy: .equal, toItem: viewController.view, attribute: .top, multiplier: 1, constant: 0),
                        NSLayoutConstraint(item: v, attribute: .bottom, relatedBy: .equal, toItem: viewController.view, attribute: .bottom, multiplier: 1, constant: 0),
                        NSLayoutConstraint(item: v, attribute: .leading, relatedBy: .equal, toItem: viewController.view, attribute: .leading, multiplier: 1, constant: 0),
                        NSLayoutConstraint(item: v, attribute: .trailing, relatedBy: .equal, toItem: viewController.view, attribute: .trailing, multiplier: 1, constant: 0)
                        ])
                }
                self.viewController = miniAppVC
            }
        }
    }

    private func createView(name: String, properties: [AnyHashable : Any]?) -> UIView? {
        let viewController = ElectrodeReactNative.sharedInstance().miniApp(withName: name, properties: properties)
        if let rnView = viewController.view {
            rnView.translatesAutoresizingMaskIntoConstraints = false
            return rnView
        }
        return nil
    }
}
