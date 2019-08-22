//
//  ENNavigationAPIImpl.swift
//  ErnRunner
//
//  Created by Lianci Liu on 6/27/19.
//  Copyright © 2019 Walmart. All rights reserved.
//

import Foundation

class ENNavigationAPIImpl: NSObject {
    static let shared = ENNavigationAPIImpl()
    let navigationAPI: EnNavigationAPI
    weak var delegate: ENNavigationProtocol?

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
            self.delegate?.handleFinishFlow(finalPayLoad: finishFlow, completion: { (messageCompletion) in
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
                    self.delegate?.updateNavigationBar(navBar: navBar, completion: { (message) in
                        return block(message, nil)
                    })
                }
            }
        })
    }

    func registerBackRequestHandler() {
        _ = self.navigationAPI.requests.registerBackRequestHandler(handler: { (data, block) in
            let d = data as? ErnNavRoute
            let ernData = d?.toDictionary() as? [AnyHashable : Any]
            let path = ernData?["path"] as? String ?? nil
            self.delegate?.popToViewControllerWithPath(path: path, completion: { (message) in
                return block(message, nil)
            })
        })
    }

    func registerNavigationRequestHandler() {
        _ = self.navigationAPI.requests.registerNavigateRequestHandler(handler: { (data, block) in
            if let d = data as? ErnNavRoute, let ernData = d.toDictionary() as? [AnyHashable : Any] {
                self.delegate?.handleNavigationRequestWithPath(routeData: ernData, completion: { (message) in
                    return block(message, nil)
                })
            }
        })
    }
}
