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

@available(*, deprecated, message: "MiniAppFinishedCallback is deprecated, use Payload")
public typealias MiniAppFinishedCallback = (String?) -> Void

public typealias Payload = ([AnyHashable: Any]?) -> Void
public typealias NavigateWithRoute = ([AnyHashable: Any]) -> Bool
public typealias BackToRoute = (_ componentName: String, _ properties: [AnyHashable: Any]) -> Bool

@objc public protocol ENMiniAppNavDataProvider: ENMiniAppDataProvider {
    // Ideally this protocol was named ENNavCallback, as it should be a call back handler

    @available(*, deprecated, message: "finishedCallback is deprecated, use finish")
    @objc optional var finishedCallback: MiniAppFinishedCallback? { get set }

    @objc optional var finish: Payload? { get set }
    @objc optional var navigateWithRoute: NavigateWithRoute { get set }
    @objc optional var backToMiniApp: BackToRoute { get set }
}
