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
    var navBarState: ENNavigationBarState?
    var hideNavBar: Bool?

    override public func viewDidLoad(viewController: UIViewController) {
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
            NotificationCenter.default.addObserver(self, selector: #selector(self.didBlur), name: UIApplication.didEnterBackgroundNotification, object: nil)
            NotificationCenter.default.addObserver(self, selector: #selector(self.didFocus), name: UIApplication.didBecomeActiveNotification, object: nil)
            super.viewDidLoad(viewController: viewController)
        }
    }

    func viewWillAppear() {
        if let vc = self.viewController {
            hideNavigationBarIfNeeded()
            ENNavigationAPIImpl.shared.currentViewController = vc
        }
    }

    func viewDidAppear() {
        if let vc = self.viewController {
            if vc.isMovingToParent {
                if let navigationBarDict = vc.properties?["navigationBar"] as? [AnyHashable: Any] {
                    let navBar = NavigationBar(dictionary: navigationBarDict)
                    updateNavBarTitleAndButtons(navBar: navBar)
                }
            }
        }
    }

    func viewWillDisappear() {
        // This scenario only applies to overlay views that are presented
        if let presentingVC = self.viewController?.presentingViewController as? ENOverlayProtocol {
            presentingVC.onDismissOverlay()
        }
    }

    func viewDidDisappear() {
        if self.viewController?.isMovingToParent ?? false || self.viewController?.isBeingDismissed ?? false {
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

    func popToViewController(ernNavRoute: [AnyHashable : Any]?, completion: @escaping ERNNavigationCompletionBlock) {
        let overlay = viewController?.properties?["overlay"] as? Bool ?? false
        let componentName = ernNavRoute?["path"] as? String
        let refresh = ernNavRoute?["refresh"] as? Bool ?? false
        if let navCallBack = viewController?.navigationController as? ENMiniAppNavDataProvider {
            if let backToMiniApp = navCallBack.backToMiniApp, let componentName = componentName, let ernNavRoute = ernNavRoute {
                if backToMiniApp(componentName, ernNavRoute) {
                    return completion("success")
                }
            }
        }
        if overlay {
            if let vc = viewController, let topVC = getTopViewControllerWithNavigation(viewController: vc) {
                topVC.dismiss(animated: false) {
                    self.backTo(componentName: componentName, refresh: refresh, ernNavRoute: ernNavRoute, completion: completion)
                }
            }
        } else {
            backTo(componentName: componentName, refresh: refresh, ernNavRoute: ernNavRoute, completion: completion)
        }
    }

    func backTo(componentName: String?, refresh: Bool, ernNavRoute: [AnyHashable : Any]?, completion: ERNNavigationCompletionBlock) {
        if let componentName = componentName, let viewControllers = viewController?.navigationController?.viewControllers {
            var deinitViews = [MiniAppNavViewController]()
            for vc in viewControllers.reversed() {
                if let miniappVC = vc as? MiniAppNavViewController {
                    if componentName == miniappVC.miniAppName {
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
            return completion("cannot find path from viewController stack")
        } else {
            if let navigationController = viewController?.navigationController {
                if navigationController.viewControllers.count == 1 {
                    navigationController.dismiss(animated: true, completion: nil)
                } else {
                    navigationController.popViewController(animated: true)
                    if refresh, let lastVC = navigationController.viewControllers.last, let miniAppVC = lastVC as? MiniAppNavViewController {
                        miniAppVC.reloadView(ernNavRoute: ernNavRoute)
                    }
                }
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
        } else if ((self.viewController?.finishedCallback) != nil) {
            self.finishedCallBack(finalPayLoad: finalPayload)
        } else {
            // No overrides, so perform default finish implementation to dismiss navigation stack
            if let navController = self.viewController?.navigationController {
                navController.dismiss(animated: true)
            }
        }
        var presentingVC: UIViewController? = self.viewController
        while presentingVC?.presentingViewController != nil {
            presentingVC = presentingVC?.presentingViewController
            if let nc = presentingVC as? UINavigationController {
                if let miniappVC = nc.viewControllers.last as? MiniAppNavViewController {
                    ENNavigationAPIImpl.shared.currentViewController = miniappVC
                    break;
                }
            }
        }
        return completion("success")
    }

    func onDismissOverlay() {
        if let vc = viewController {
            vc.navigationController?.navigationBar.topItem?.title = vc.delegate?.navBarState?.title
            vc.navigationController?.navigationBar.topItem?.leftBarButtonItem = vc.delegate?.navBarState?.leftBarButtonItem
            vc.navigationController?.navigationBar.topItem?.rightBarButtonItems = vc.delegate?.navBarState?.rightBarButtonItems
            ENNavigationAPIImpl.shared.currentViewController = vc
        }
    }

    func updateNavigationBar(navBar: NavigationBar, completion: @escaping ERNNavigationCompletionBlock) {
        if viewController != nil {
            updateNavBarTitleAndButtons(navBar: navBar)
            return completion("success")
        } else {
            return completion("failed due to no viewcontroller, should never reach here")
        }
    }

    func updateNavBarTitleAndButtons(navBar: NavigationBar) {
        setNavBarTitle(title: navBar.title)
        if let rightButtons = navBar.buttons {
            setRightBarButtons(buttons: rightButtons)
        } else {
            clearRightBarButtons()
        }
        if let leftButton = navBar.leftButton {
            setLeftBarButton(leftButton: leftButton)
        }
        hideNavBar = navBar.hide
        hideNavigationBarIfNeeded()
    }

    func hideNavigationBarIfNeeded() {
        if let vc = viewController, let topVC = getTopViewControllerWithNavigation(viewController: vc),
            let navigationVC = topVC.navigationController {
            if hideNavBar != nil {
                if hideNavBar! {
                    if !navigationVC.isNavigationBarHidden {
                        ENNavigationDelegate.hiddenByRn = true
                        navigationVC.setNavigationBarHidden(true, animated: false)
                    }
                    return
                } else if ENNavigationDelegate.hiddenByRn && navigationVC.isNavigationBarHidden {
                    navigationVC.setNavigationBarHidden(false, animated: false)
                }
                ENNavigationDelegate.hiddenByRn = false
            }
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
                // We are checking overlay here to get navigation bar state before update nav bar is called
                if overlay {
                    if let vc = viewController, let topVC = getTopViewControllerWithNavigation(viewController: vc),
                        let navigationItem = topVC.navigationController?.navigationBar.topItem {
                        navBarState = ENNavigationBarState(navigationItem: navigationItem)
                    }
                }
                let combinedRouteData = self.combineRouteData(dictionary1: routeData, dictionary2: self.viewController?.globalProperties)
                let newVC = MiniAppNavViewController(properties: combinedRouteData, miniAppName: path)
                newVC.pushToExistingViewController = false
                if let finish = self.viewController?.finish {
                    newVC.finish = finish
                } else if let finishedCallback = self.viewController?.finishedCallback {
                    newVC.finishedCallback = finishedCallback
                }
                if let navigationBarDict = routeData["navigationBar"] as? [AnyHashable: Any] {
                    let navBar = NavigationBar(dictionary: navigationBarDict)
                    newVC.delegate?.hideNavBar = navBar.hide
                }
                newVC.navigateWithRoute = self.viewController?.navigateWithRoute
                newVC.globalProperties = self.viewController?.globalProperties
                if overlay {
                    // If replace is true, dismiss and then present new overlay
                    if replace {
                        if let presentingVC = self.viewController?.presentingViewController as? ENOverlayProtocol {
                            self.viewController?.dismiss(animated: false, completion: {
                                presentingVC.presentOverlay(viewToPresent: newVC)
                            })
                        }
                    } else {
                        presentOverlay(viewToPresent: newVC)
                    }
                } else if let navigationController = self.viewController?.navigationController {
                    pushViewController(viewToPush: newVC, navigationController: navigationController, replace: replace)
                } else {
                    // This should only be executed if current viewcontroller is an overlay
                    if let vc = viewController, let topVC = getTopViewControllerWithNavigation(viewController: vc), let navigationController = topVC.navigationController {
                        topVC.dismiss(animated: false) {
                            // In overlay scenario, replace need not be considered, because dismissing the overlay acts as the replace
                            self.pushViewController(viewToPush: newVC, navigationController: navigationController, replace: false)
                        }
                    }
                }
            }
        }
        return completion("success")
    }

    func presentOverlay(viewToPresent: UIViewController) {
        self.viewController?.definesPresentationContext = true
        viewToPresent.modalPresentationStyle = .overCurrentContext
        self.viewController?.present(viewToPresent, animated: false)
    }

    func pushViewController(viewToPush: UIViewController, navigationController: UINavigationController, replace: Bool) {
        let viewControllers = navigationController.viewControllers
        if replace && viewControllers.count > 0 {
            var vcs = viewControllers
            _ = vcs.popLast()
            vcs.append(viewToPush)
            navigationController.setViewControllers(vcs, animated: true)
        } else {
            navigationController.pushViewControllerWithoutBackButtonTitle(viewToPush, animated: true)
        }
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

    func setNavBarTitle(title: String) {
        if let vc = viewController, let topVC = getTopViewControllerWithNavigation(viewController: vc) {
            topVC.navigationController?.navigationBar.topItem?.title = title
        }
    }

    func setRightBarButtons(buttons: [NavigationBarButton]) {
        if let vc = viewController, let topVC = getTopViewControllerWithNavigation(viewController: vc) {
            var rightButtons = [ENBarButtonItem]()
            for rightButton in buttons {
                rightButtons.insert(getUIBarButtonItem(navigationButton: rightButton, vc: vc), at: 0)
            }
            topVC.navigationController?.navigationBar.topItem?.rightBarButtonItems = rightButtons
        }
    }

    func clearRightBarButtons() {
        if let vc = viewController, let topVC = getTopViewControllerWithNavigation(viewController: vc) {
            topVC.navigationController?.navigationBar.topItem?.rightBarButtonItems = nil
        }
    }

    func setLeftBarButton(leftButton: NavigationBarLeftButton) {
        if let vc = viewController, let topVC = getTopViewControllerWithNavigation(viewController: vc) {
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
            button.viewController = vc
            topVC.navigationController?.navigationBar.topItem?.leftBarButtonItem = button
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
            if let image = getImage(icon: icon) {
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
        button.viewController = vc
        return button
    }

    private func emitERNEvent(sender: ENBarButtonItem, stringTag: String) {
        var viewId = "NOT_SET"
        if let vc = sender.viewController as? MiniAppNavViewController, let viewIdentifier = vc.delegate?.viewIdentifier {
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
            if let vc = sender.viewController {
                let viewControllers = self.viewController?.navigationController?.viewControllers
                if viewControllers?.count == 1 {
                    vc.navigationController?.dismiss(animated: true, completion: nil)
                } else {
                    if vc.presentingViewController != nil {
                        vc.dismiss(animated: false)
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
