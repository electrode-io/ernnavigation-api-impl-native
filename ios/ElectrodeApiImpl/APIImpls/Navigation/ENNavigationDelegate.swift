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
    var navigationAPI: EnNavigationAPI?
    var delegate: ENNavigationProtocol?

    override public func viewDidLoad(viewController: UIViewController) {
        if let navigationVC = viewController as? UINavigationController {
            if let vc = navigationVC as? ENMiniAppNavDataProvider {
                let miniAppName = vc.rootComponentName
                let properties = vc.properties
                let miniappVC = MiniAppNavViewController(properties: properties, miniAppName: miniAppName)
                miniappVC.view.frame = UIScreen.main.bounds
                if let finish = vc.finish {
                    miniappVC.finish = finish
                } else if let finishedCallback = vc.finishedCallback {
                    miniappVC.finishedCallback = finishedCallback
                }
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
        let payloadDict = finalPayload?.convertStringToDict()
        if ((self.viewController?.finish) != nil) {
            self.viewController?.finish?(payloadDict)
        } else {
            self.finishedCallBack(finalPayLoad: finalPayload)
        }
        return completion("Finished status")
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

    private func finishedCallBack(finalPayLoad: String?) {
        if ((self.viewController?.finishedCallback) != nil) {
            self.viewController?.finishedCallback?(finalPayLoad)
        }
    }

    func handleNavigationRequestWithPath(routeData: [AnyHashable : Any], completion: ERNNavigationCompletionBlock) {
        let path = routeData["path"] as? String ?? ""
        if path == "finishFlow" {
            let jsonPayLoad = routeData["jsonPayload"] as? String
            self.finishedCallBack(finalPayLoad: jsonPayLoad)
        } else {
            let vc = MiniAppNavViewController(properties: routeData, miniAppName: path)
            if let navigationBarDict = routeData["navigationBar"] as? [AnyHashable: Any] {
                let navBar = NavigationBar(dictionary: navigationBarDict)
                vc.title = navBar.title
                if let buttons = navBar.buttons {
                    self.getNavBarButtons(buttons: buttons, viewController: vc)
                }
            }
            if let finish = self.viewController?.finish {
                vc.finish = finish
            } else if let finishedCallback = self.viewController?.finishedCallback {
                vc.finishedCallback = finishedCallback
            }
            self.viewController?.navigationController?.pushViewController(vc, animated: true)
        }
        return completion("success")
    }

    func getNavBarTitle(title: String, viewController: UIViewController) {
        viewController.navigationController?.navigationBar.topItem?.title = title
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
            rightButtons.insert(self.getUIBarButtonItem(navigationButton: rightButton), at: 0)
        }
        viewController.navigationItem.rightBarButtonItems = rightButtons
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
            UIGraphicsBeginImageContextWithOptions(newSize, false, 1.0)
            img.draw(in: rect)
            let newImage = UIGraphicsGetImageFromCurrentImageContext()
            UIGraphicsEndImageContext()
            return newImage
        }
        return nil
    }

    func getUIBarButtonItem(navigationButton: NavigationBarButton) -> ENBarButtonItem {
        var button = ENBarButtonItem()
        if let icon = navigationButton.icon, let url = URL(string: icon) {
            do {
                let imageData = try Data(contentsOf: url, options: [])
                let image = UIImage(data: imageData)
                let resizedImage = self.resizeImage(image: image, targetSize: CGSize(width: ENNavigationDelegate.buttonWidth, height: ENNavigationDelegate.buttonWidth))
                button = ENBarButtonItem(image: resizedImage, style: .plain, target: self, action: #selector(clickButtonWithButtonId(_:)))
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
