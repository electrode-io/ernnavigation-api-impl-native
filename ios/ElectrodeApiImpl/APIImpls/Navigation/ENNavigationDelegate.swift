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

@objcMembers public class ENNavigationDelegate: ENCoreDelegate {

    static let buttonWidth: CGFloat = 22
    static var hiddenByRn: Bool = false
    var navigationAPI: EnNavigationAPI?
    weak var delegate: ENNavigationProtocol?

    override public func viewDidLoad(viewController: UIViewController) {
        self.delegate = viewController as? ENNavigationProtocol
        if let navigationVC = viewController as? UINavigationController {
            if let vc = navigationVC as? ENMiniAppNavDataProvider {
                let miniAppName = vc.rootComponentName
                let properties = combineRouteData(dictionary1: vc.properties, dictionary2: vc.globalProperties ?? nil)
                let miniappVC = MiniAppNavViewController(properties: properties, miniAppName: miniAppName)
                miniappVC.view.frame = UIScreen.main.bounds
                if let finish = vc.finish {
                    miniappVC.finish = finish
                } else if let finishedCallback = vc.finishedCallback {
                    miniappVC.finishedCallback = finishedCallback
                }
                miniappVC.navigateWithRoute = vc.navigateWithRoute
                miniappVC.globalProperties = vc.globalProperties ?? nil
                navigationVC.navigationBar.isTranslucent = false
                navigationVC.pushViewControllerWithoutBackButtonTitle(miniappVC, animated: false)
            }
        } else {
            NotificationCenter.default.addObserver(self, selector: #selector(self.didBlur), name: NSNotification.Name.UIApplicationDidEnterBackground, object: nil)
            NotificationCenter.default.addObserver(self, selector: #selector(self.didFocus), name: NSNotification.Name.UIApplicationDidBecomeActive, object: nil)
            super.viewDidLoad(viewController: viewController)
        }
    }

    func viewWillAppear() {
        if let vc = self.viewController {
            vc.hideNavigationBarIfNeeded()
            ENNavigationAPIImpl.shared.delegate = vc
        }
    }

    func viewWillDisappear() {
        if let presentingVC = self.viewController?.presentingViewController as? MiniAppNavViewController {
            if let props = presentingVC.properties, let navBarDict = props["navigationBar"] as? [AnyHashable : Any] {
                let navBar = NavigationBar(dictionary: navBarDict)
                updateNavBarTitleAndButtons(navBar: navBar)
            }
            ENNavigationAPIImpl.shared.delegate = presentingVC
        }
    }

    func viewDidDisapper() {
        if self.viewController?.isMovingFromParentViewController ?? false {
            self.deinitRNView()
        }
    }

    @objc func didBlur() {
        if viewController?.navigationController?.topViewController === viewController {
            ENNavigationAPIImpl.shared.navigationAPI.events.emitEventNavEvent(eventData: NavEventData(eventType: NavEventType.DID_BLUR.rawValue, viewId: viewIdentifier, jsonPayload: nil))
        }
    }

    @objc func didFocus() {
        if viewController?.navigationController?.topViewController === viewController {
            ENNavigationAPIImpl.shared.navigationAPI.events.emitEventNavEvent(eventData: NavEventData(eventType: NavEventType.DID_FOCUS.rawValue, viewId: viewIdentifier, jsonPayload: nil))
        }
    }

    func popToViewController(ernNavRoute: [AnyHashable : Any]?, completion: ERNNavigationCompletionBlock) {
        let refresh = ernNavRoute?["refresh"] as? Bool ?? false
        var deinitViews = [MiniAppNavViewController]()
        if let p = ernNavRoute?["path"] as? String, let viewControllers = self.viewController?.navigationController?.viewControllers {
            for vc in viewControllers.reversed() {
                if let miniappVC = vc as? MiniAppNavViewController {
                    if p == miniappVC.miniAppName {
                        self.viewController?.navigationController?.popToViewController(miniappVC, animated: true)
                        if refresh {
                            miniappVC.reloadView(ernNavRoute: ernNavRoute)
                        }
                        for vc in deinitViews {
                            vc.delegate?.deinitRNView()
                        }
                        return completion("success")
                    } else {
                        deinitViews.append(miniappVC)
                    }
                }
            }
            return completion("cannot find path from view Controllet stack")
        } else {
            self.viewController?.navigationController?.popViewController(animated: true)
            if refresh, let lastVC = self.viewController?.navigationController?.viewControllers.last, let miniAppVC = lastVC as? MiniAppNavViewController {
                miniAppVC.reloadView(ernNavRoute: ernNavRoute)
            }
            return completion("success")
        }
    }

    func handleFinishFlow(finalPayload: String?, completion: @escaping ERNNavigationCompletionBlock) {
        let payloadDict = finalPayload?.convertStringToDict()
        if let navController = self.viewController?.navigationController {
            for vc in navController.viewControllers {
                (vc as? MiniAppNavViewController)?.delegate?.deinitRNView()
                (vc as? MiniAppNavViewController)?.delegate = nil
            }
        } else {
            if var currentVC = self.viewController {
                var isVCOverlay = currentVC.properties?["overlay"] as? Bool ?? false
                while isVCOverlay == true {
                    if let miniappvc = currentVC.presentingViewController as? MiniAppNavViewController {
                        currentVC = miniappvc
                        isVCOverlay = currentVC.properties?["overlay"] as? Bool ?? false
                    } else {
                        isVCOverlay = false
                    }
                }
                currentVC.dismiss(animated: false)
            }
        }
        if ((self.viewController?.finish) != nil) {
            self.viewController?.finish?(payloadDict)
        } else {
            self.finishedCallBack(finalPayLoad: finalPayload)
        }
        return completion("Finished status")
    }

    func updateNavigationBar(navBar: NavigationBar, completion: @escaping ERNNavigationCompletionBlock) {
        if self.viewController != nil {
            updateNavBarTitleAndButtons(navBar: navBar)
            return completion("success")
        }
    }

    func updateNavBarTitleAndButtons(navBar: NavigationBar) {
        if let vc = viewController {
            getNavBarTitle(title: navBar.title, viewController: vc)
            if let buttons = navBar.buttons {
                getNavBarButtons(buttons: buttons, viewController: vc)
            } else {
                clearNavBarButtons(viewController: vc)
            }
            if let leftButton = navBar.leftButton {
                manageLeftButton(leftButton: leftButton, viewController: vc)
            } else {
                clearLeftButton(viewController: vc)
            }
            if let hide = navBar.hide {
                vc.hide = hide
            }
            vc.hideNavigationBarIfNeeded()
        }
    }

    private func finishedCallBack(finalPayLoad: String?) {
        if ((self.viewController?.finishedCallback) != nil) {
            self.viewController?.finishedCallback?(finalPayLoad)
        }
    }

    func handleNavigationRequestWithPath(routeData: [AnyHashable : Any], completion: ERNNavigationCompletionBlock) {
        let path = routeData["path"] as? String ?? ""
        let replace = routeData["replace"] as? Bool ?? false
        let overlay = routeData["overlay"] as? Bool ?? false
        if path == "finishFlow" {
            let jsonPayLoad = routeData["jsonPayload"] as? String
            self.finishedCallBack(finalPayLoad: jsonPayLoad)
        } else {
            if !(self.viewController?.navigateWithRoute?(routeData) ?? false) {
                let combinedRouteData = self.combineRouteData(dictionary1: routeData, dictionary2: self.viewController?.globalProperties)
                let vc = MiniAppNavViewController(properties: combinedRouteData, miniAppName: path)
                vc.pushToExistingViewController = false
                if let navigationBarDict = routeData["navigationBar"] as? [AnyHashable: Any] {
                    let navBar = NavigationBar(dictionary: navigationBarDict)
                    vc.title = navBar.title
                    if let buttons = navBar.buttons {
                        self.getNavBarButtons(buttons: buttons, viewController: vc)
                    }
                    if let leftButton = navBar.leftButton {
                        self.manageLeftButton(leftButton: leftButton, viewController: vc)
                    }
                }
                if let finish = self.viewController?.finish {
                    vc.finish = finish
                } else if let finishedCallback = self.viewController?.finishedCallback {
                    vc.finishedCallback = finishedCallback
                }
                vc.navigateWithRoute = self.viewController?.navigateWithRoute
                vc.globalProperties = self.viewController?.globalProperties
                if overlay {
                    self.viewController?.definesPresentationContext = true
                    vc.modalPresentationStyle = .overCurrentContext
                    self.viewController?.present(vc, animated: true)
                } else if let viewControllers = self.viewController?.navigationController?.viewControllers {
                    if replace && viewControllers.count > 0 {
                        var vcs = viewControllers
                        _ = vcs.popLast()
                        vcs.append(vc)
                        self.viewController?.navigationController?.setViewControllers(vcs, animated: true)
                    } else {
                        self.viewController?.navigationController?.pushViewControllerWithoutBackButtonTitle(vc, animated: true)
                    }
                }
            }
        }
        return completion("success")
    }

    func getTopViewControllerWithNavigation(viewController: UIViewController) -> UIViewController? {
        if viewController.navigationController != nil {
            return viewController
        } else {
            var currentVC: UIViewController? = viewController.presentingViewController
            while currentVC != nil {
                if currentVC!.navigationController != nil {
                    return currentVC
                } else {
                    currentVC = currentVC?.presentingViewController
                }
            }
            return nil
        }
    }

    func getNavBarTitle(title: String, viewController: UIViewController) {
        let vc = getTopViewControllerWithNavigation(viewController: viewController)
        vc?.navigationController?.navigationBar.topItem?.title = title
    }

    func getNavBarButtons(buttons: [NavigationBarButton], viewController: UIViewController) {
        var leftNavigationButtons = [NavigationBarButton]()
        var rightNavigationButtons = [NavigationBarButton]()
        for button in buttons {
            //left button is deprecated
            if button.location == "left" {
                leftNavigationButtons.append(button)
            } else {
                rightNavigationButtons.append(button)
            }
        }
        assert(leftNavigationButtons.count <= 1 && rightNavigationButtons.count <= 3, "cannot have more than one left navigation button or three right navigation buttons")
        if let topVC = getTopViewControllerWithNavigation(viewController: viewController) {
            if leftNavigationButtons.count == 1 {
                topVC.navigationController?.navigationBar.topItem?.leftBarButtonItem = self.getUIBarButtonItem(navigationButton: leftNavigationButtons[0], vc: topVC)
            }
            var rightButtons = [ENBarButtonItem]()
            for rightButton in rightNavigationButtons {
                rightButtons.insert(self.getUIBarButtonItem(navigationButton: rightButton, vc: topVC), at: 0)
            }
            topVC.navigationController?.navigationBar.topItem?.rightBarButtonItems = rightButtons
        }
    }

    func manageLeftButton(leftButton: NavigationBarLeftButton, viewController: UIViewController) {
        if let topVC = getTopViewControllerWithNavigation(viewController: viewController) {
            var button = ENBarButtonItem()
            if let icon = leftButton.icon {
                if let image = self.getImage(icon: icon) {
                    button = ENBarButtonItem(image: image, style: .plain, target: self, action: #selector(clickLeftButtonWithButtonId(_:)))
                } else {
                    NSLog("Cannot get image data")
                }
            } else {
                button = ENBarButtonItem(title: leftButton.title, style: .plain, target: self, action: #selector(clickLeftButtonWithButtonId(_:)))
            }
            button.accessibilityLabel = leftButton.adaLabel
            button.isEnabled = !(leftButton.disabled ?? false)
            button.stringTag = leftButton.id
            button.currViewController = topVC
            topVC.navigationController?.navigationBar.topItem?.leftBarButtonItem = button
        }
    }

    func clearNavBarButtons(viewController: UIViewController) {
        if let topVC = getTopViewControllerWithNavigation(viewController: viewController) {
            topVC.navigationController?.navigationBar.topItem?.leftBarButtonItem = nil
            topVC.navigationController?.navigationBar.topItem?.rightBarButtonItems = nil
        }
    }

    func clearLeftButton(viewController: UIViewController) {
        if let topVC = getTopViewControllerWithNavigation(viewController: viewController) {
            topVC.navigationController?.navigationBar.topItem?.leftBarButtonItem = nil
        }
    }

    private func getImage(icon: String) -> UIImage? {
        guard let url = URL(string: icon) else {
            return nil
        }
        do {
            let imageData = try Data(contentsOf: url, options: [])
            let image = UIImage(data: imageData)
            return self.resizeImage(image: image, targetSize: CGSize(width: ENNavigationDelegate.buttonWidth, height: ENNavigationDelegate.buttonWidth))
        } catch {
            // Attempt to read from image assets
            guard let image = UIImage(named: icon) else {
                return nil
            }
            return self.resizeImage(image: image, targetSize: CGSize(width: ENNavigationDelegate.buttonWidth, height: ENNavigationDelegate.buttonWidth))
        }
    }

    private func resizeImage(image: UIImage?, targetSize: CGSize) -> UIImage? {
        if let img = image {
            let size = img.size
            let widthRatio  = targetSize.width  / size.width
            let heightRatio = targetSize.height / size.height
            var newSize: CGSize
            if(widthRatio > heightRatio) {
                newSize = CGSize(width: size.width * heightRatio, height: size.height * heightRatio)
            } else {
                newSize = CGSize(width: size.width * widthRatio,  height: size.height * widthRatio)
            }
            let rect = CGRect(x: 0, y: 0, width: newSize.width, height: newSize.height)
            UIGraphicsBeginImageContextWithOptions(newSize, false, 0)
            img.draw(in: rect)
            let newImage = UIGraphicsGetImageFromCurrentImageContext()
            UIGraphicsEndImageContext()
            return newImage
        }
        return nil
    }

    func getUIBarButtonItem(navigationButton: NavigationBarButton, vc: UIViewController) -> ENBarButtonItem {
        var button = ENBarButtonItem()
        if let icon = navigationButton.icon {
            if let image = self.getImage(icon: icon) {
                button = ENBarButtonItem(image: image, style: .plain, target: self, action: #selector(clickButtonWithButtonId(_:)))
            } else {
                NSLog("Cannot get image data")
            }
        } else {
            button = ENBarButtonItem(title: navigationButton.title, style: .plain, target: self, action: #selector(clickButtonWithButtonId(_:)))
        }
        button.accessibilityLabel = navigationButton.adaLabel
        button.isEnabled = !(navigationButton.disabled ?? false)
        button.stringTag = navigationButton.id
        button.currViewController = vc
        return button
    }

    private func emitERNEvent(sender: ENBarButtonItem, stringTag: String) {
        var viewId = "NOT_SET"
        if let vc = sender.currViewController as? MiniAppNavViewController, let viewIdentifier = vc.delegate?.viewIdentifier {
            viewId = viewIdentifier
        }
        //emitEventOnNavButtonClick is deprecated
        ENNavigationAPIImpl.shared.navigationAPI.events.emitEventOnNavButtonClick(buttonId: stringTag)
        ENNavigationAPIImpl.shared.navigationAPI.events.emitEventNavEvent(eventData: NavEventData(eventType: NavEventType.BUTTON_CLICK.rawValue, viewId: viewId, jsonPayload: getButtonJsonPayload(buttonId: stringTag)))
    }

    @objc func clickButtonWithButtonId(_ sender: ENBarButtonItem) {
        if let stringTag = sender.stringTag {
            self.emitERNEvent(sender: sender, stringTag: stringTag)
        }
    }

    @objc func clickLeftButtonWithButtonId(_ sender: ENBarButtonItem) {
        if let stringTag = sender.stringTag {
            self.emitERNEvent(sender: sender, stringTag: stringTag)
        } else {
            if let vc = sender.currViewController {
                let viewControllers = self.viewController?.navigationController?.viewControllers
                if viewControllers?.count == 1 {
                    vc.navigationController?.dismiss(animated: true, completion: nil)
                } else {
                    if (vc.presentedViewController != nil) {
                        vc.dismiss(animated: true) {
                            vc.navigationController?.popViewController(animated: true)
                        }
                    } else {
                        vc.navigationController?.popViewController(animated: true)
                    }
                }
            }
        }
    }

    private func getButtonJsonPayload(buttonId: String) -> String {
        return "{\"id\": \"" + buttonId + "\"}"
    }
}

extension String {
    func convertStringToDict() -> [AnyHashable: Any]? {
        do {
            if let data = self.data(using: String.Encoding.utf8), let dict = try JSONSerialization.jsonObject(with: data, options: [.allowFragments]) as? [AnyHashable: Any] {
                return dict
            } else {
                return nil
            }
        } catch let error as NSError {
            let logger = ElectrodeConsoleLogger.sharedInstance()
            logger.log(.error, message: error.description)
        }
        return nil
    }
}
