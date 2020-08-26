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

import Foundation

class ENNavigationAPIImpl: NSObject {
    static let shared = ENNavigationAPIImpl()
    let navigationAPI: EnNavigationAPI
    weak var currentViewController: ENNavigationProtocol?

    private override init() {
        self.navigationAPI = EnNavigationAPI()
        super.init()
        self.registerFinishRequestHandler()
        self.registerUpdateRequestHandler()
        self.registerBackRequestHandler()
        self.registerNavigationRequestHandler()
    }

    func registerFinishRequestHandler() {
        _ = self.navigationAPI.requests.registerFinishRequestHandler(handler: { (data, block) in
            let finishFlow = data as? String
            self.currentViewController?.handleFinishFlow(finalPayLoad: finishFlow, completion: { (messageCompletion) in
                block(messageCompletion, nil)
                return
            })
        })
    }

    func registerUpdateRequestHandler() {
        _ = self.navigationAPI.requests.registerUpdateRequestHandler(handler: { (data, block) in
            if let d = data as? ErnNavRoute, let ernData = d.toDictionary() as? [AnyHashable : Any] {
                if let navBarDict = ernData["navigationBar"] as? [AnyHashable : Any] {
                    let navBar = NavigationBar(dictionary: navBarDict)
                    self.currentViewController?.updateNavigationBar(navBar: navBar, completion: { (message) in
                        if message == "success" {
                            return block(message, nil)
                        } else {
                            let failureMessage = ElectrodeBridgeFailureMessage.createFailureMessage(withCode: "error", message: message)
                            return block(nil, failureMessage)
                        }
                    })
                }
            }
        })
    }

    func registerBackRequestHandler() {
        _ = self.navigationAPI.requests.registerBackRequestHandler(handler: { (data, block) in
            let d = data as? ErnNavRoute
            let ernData = d?.toDictionary() as? [AnyHashable : Any]
            self.currentViewController?.popToViewControllerWithPath(ernNavRoute: ernData, completion: { (message) in
                if message == "success" {
                    return block(message, nil)
                } else {
                    let failureMessage = ElectrodeBridgeFailureMessage.createFailureMessage(withCode: "error", message: message)
                    return block(nil, failureMessage)
                }
            })
        })
    }

    func registerNavigationRequestHandler() {
        _ = self.navigationAPI.requests.registerNavigateRequestHandler(handler: { (data, block) in
            if let d = data as? ErnNavRoute, let ernData = d.toDictionary() as? [AnyHashable : Any] {
                self.currentViewController?.handleNavigationRequestWithPath(routeData: ernData, completion: { (message) in
                    return block(message, nil)
                })
            }
        })
    }
}
