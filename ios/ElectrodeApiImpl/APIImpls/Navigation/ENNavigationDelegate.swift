//
//  ENNavigationDelegate.swift
//  ErnRunner
//
//  Created by Lianci Liu on 6/27/19.
//  Copyright © 2019 Walmart. All rights reserved.
//

import UIKit

class ENNavigationDelegate: ENCoreDelegate {

    var navigationAPI: EnNavigationAPI?
    var delegate: ENNavigationProtocol?

    override func viewDidLoad(viewController: UIViewController) {
        if let navigationVC = viewController as? UINavigationController {
            if let vc = navigationVC as? ENMiniAppNavDataProvider {
                let miniAppName = vc.miniAppName
                let properties = vc.properties
                let finishedCallback = vc.finishedCallback
                let miniappVC = MiniAppNavViewController(properties: properties, miniAppName: miniAppName)
                miniappVC.view.frame = UIScreen.main.bounds
                miniappVC.finishedCallback = finishedCallback
                navigationVC.navigationBar.isTranslucent = false
                navigationVC.pushViewController(miniappVC, animated: false)
            }
        } else {
            super.viewDidLoad(viewController: viewController)
        }
    }

    func viewWillAppear() {
        self.viewController?.navigationController?.isNavigationBarHidden = false
        if let vc = self.viewController {
            ENNavigationAPIImpl.shared.delegate = vc
        }
    }

    func viewDidDisapper() {
        self.viewController?.navigationController?.navigationBar.isHidden = false
    }

    func popToViewController(path: String?, completion: ERNNavigationCompletionBlock) {
        if let p = path, let viewControllers = self.viewController?.navigationController?.viewControllers {
            for vc in viewControllers {
                if let miniappVC = vc as? MiniAppNavViewController, p == miniappVC.miniAppName {
                    self.viewController?.navigationController?.popToViewController(miniappVC, animated: true)
                    return completion("success")
                }
            }
            return completion("cannot find path from view Controllet stack")
        } else {
            self.viewController?.navigationController?.popViewController(animated: true)
            return completion("success")
        }
    }

    func handleFinishFlow(finalPayload: String?, completion: @escaping ERNNavigationCompletionBlock) {
        if let payLoad = finalPayload, let payloadDict = self.convertStringToDictionary(jsonPayLoad: payLoad), payloadDict.count > 0 {
            let path = payloadDict["page"] as? String ?? ""
            if path == "finishFlow" {
                self.viewController?.dismiss(animated: true, completion: {
                    completion("Finished status")
                    if ((self.viewController?.finishedCallback) != nil) {
                        self.viewController?.finishedCallback?(nil)
                    }
                })
            }
            return
        } else {
            if ((self.viewController?.finishedCallback) != nil) {
                self.viewController?.finishedCallback?(nil)
            }
            return completion("Finished status")
        }
    }

    func updateNavigationBar(navBar: NavigationBar, completion: @escaping ERNNavigationCompletionBlock) {
        if let vc = self.viewController {
            self.getNavBarTitle(title: navBar.title, viewController: vc)
            if let buttons = navBar.buttons {
                self.getNavBarButtons(buttons: buttons, viewController: vc)
            }
            return completion("success")
        }
        return
    }

    func handleNavigationRequestWithPath(routeData: NSDictionary, completion: ERNNavigationCompletionBlock) {
        let path = routeData["path"] as? String ?? ""
        let vc = MiniAppNavViewController(properties: routeData, miniAppName: path)
        if let navigationBarDict = routeData["navigationBar"] as? [AnyHashable: Any] {
            let navBar = NavigationBar(dictionary: navigationBarDict)
            self.getNavBarTitle(title: navBar.title, viewController: vc)
            if let buttons = navBar.buttons {
                self.getNavBarButtons(buttons: buttons, viewController: vc)
            }
        }
        vc.finishedCallback = self.viewController?.finishedCallback
        self.viewController?.navigationController?.pushViewController(vc, animated: true)
        return completion("success")
    }

    func getNavBarTitle(title: String, viewController: UIViewController) {
        viewController.title = title
    }

    func getNavBarButtons(buttons: [NavigationBarButton], viewController: UIViewController) {
        var leftNavigationButtons = [NavigationBarButton]()
        var rightNavigationButtons = [NavigationBarButton]()
        for button in buttons {
            if button.location == "left" {
                leftNavigationButtons.append(button)
            } else {
                rightNavigationButtons.append(button)
            }
        }
        assert(leftNavigationButtons.count <= 1 && rightNavigationButtons.count <= 3, "cannot have more than one left navigation button or three right navigation buttons")
        if leftNavigationButtons.count == 1 {
            viewController.navigationItem.leftBarButtonItem = self.getUIBarButtonItem(navigationButton: leftNavigationButtons[0])
        }
        var rightButtons = [ENBarButtonItem]()
        for rightButton in rightNavigationButtons {
            rightButtons.append(self.getUIBarButtonItem(navigationButton: rightButton))
        }
        viewController.navigationItem.rightBarButtonItems = rightButtons
    }

    func getUIBarButtonItem(navigationButton: NavigationBarButton) -> ENBarButtonItem {
        var button = ENBarButtonItem()
        if let icon = navigationButton.icon, let url = URL(string: icon) {
            do {
                let imageData = try Data(contentsOf: url, options: [])
                let image = UIImage(data: imageData)
                button = ENBarButtonItem(image: image, style: .plain, target: self, action: #selector(clickButtonWithButtonId(_:)))
            } catch {
                NSLog("Cannot get image data")
            }
        } else {
            button = ENBarButtonItem(title: navigationButton.title, style: .plain, target: self, action: #selector(clickButtonWithButtonId(_:)))
        }
        button.stringTag = navigationButton.id
        return button
    }

    @objc func clickButtonWithButtonId(_ sender: ENBarButtonItem) {
        if let stringTag = sender.stringTag {
            ENNavigationAPIImpl.shared.navigationAPI.events.emitEventOnNavButtonClick(buttonId: stringTag)
        }
    }

    private func convertStringToDictionary(jsonPayLoad: String) -> [String: Any]? {
        do {
            if let data = jsonPayLoad.data(using: String.Encoding.utf8) {
                let json = try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any]
                return json
            }
        } catch let error as NSError {
            NSLog(error.description)
        }
        return nil
    }
}
