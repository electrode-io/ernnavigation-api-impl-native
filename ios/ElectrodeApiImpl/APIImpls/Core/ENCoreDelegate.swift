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

@objcMembers public class ENCoreDelegate: NSObject {
    var viewController: MiniAppNavViewController?
    public var viewIdentifier: String = "NOT_SET"
    static let KEY_UNIQUE_VIEW_IDENTIFIER = "viewId"
    var rnView: UIView?

    public func viewDidLoad(viewController: UIViewController) {
        if let miniAppVC = viewController as? MiniAppNavViewController {
            self.viewIdentifier = UUID().uuidString
            let viewIdProperty = [ENCoreDelegate.KEY_UNIQUE_VIEW_IDENTIFIER: viewIdentifier] as [AnyHashable : Any]
            let extraProperties = miniAppVC.pushToExistingViewController ?
                combineRouteData(dictionary1: viewIdProperty, dictionary2: miniAppVC.globalProperties) :
                viewIdProperty
            self.createView(name: miniAppVC.miniAppName, properties: combineRouteData(dictionary1: miniAppVC.properties, dictionary2: extraProperties))
            if let v = self.rnView {
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

    private func createView(name: String, properties: [AnyHashable : Any]?) {
        let overlay = properties?["overlay"] as? Bool ?? false
        let view = ElectrodeReactNative.sharedInstance().miniAppView(withName: name, properties: properties, overlay: overlay)
        view.translatesAutoresizingMaskIntoConstraints = false
        rnView = view
    }

    func reloadView(viewController: UIViewController, ernNavRoute: [AnyHashable: Any]?) {
        if let v = self.rnView, let miniAppVC = viewController as? MiniAppNavViewController {
            var combinedProperties = self.combineRouteData(dictionary1: miniAppVC.properties, dictionary2: ernNavRoute) ?? [:]
            combinedProperties["timestamp"] = Date()
            combinedProperties["viewId"] = self.viewIdentifier
            ElectrodeReactNative.sharedInstance().update(v, withProps: combinedProperties)
        }
    }

    func combineRouteData(dictionary1: [AnyHashable: Any]?, dictionary2: [AnyHashable: Any]?) -> [AnyHashable: Any]? {
        guard let dict1 = dictionary1 else {
            return dictionary2
        }
        guard let dict2 = dictionary2 else {
            return dictionary1
        }

        var result = dict2.merging(dict1) {(current, _) in current }

        /*  When both dictionaries have jsonPayloads,
            Convert the json strings to dictionaries,
            Merge and convert back to json strings
            Set back to "jsonPayload" key
        */
        let payload1 = dict1["jsonPayload"] as? String
        let payload2 = dict2["jsonPayload"] as? String
        if let payload1 = payload1, let payload2 = payload2 {
            let jsonDict1 = payload1.convertStringToDict()
            let jsonDict2 = payload2.convertStringToDict()
            if let jsonDict1 = jsonDict1, let jsonDict2 = jsonDict2 {
                let jsonResult = jsonDict2.merging(jsonDict1) {(current, _) in current }
                let jsonString = ENNavigationUtils.jsonToString(json: jsonResult)
                result["jsonPayload"] = jsonString
            } else if let _ = jsonDict1 {
                result["jsonPayload"] = payload1
            } else {
                result["jsonPayload"] = payload2
            }
        } else if let payload1 = payload1 {
            result["jsonPayload"] = payload1
        } else {
            result["jsonPayload"] = payload2
        }
        return result
    }

    func deinitRNView() {
        self.rnView?.removeFromSuperview()
        rnView = nil
    }
}
