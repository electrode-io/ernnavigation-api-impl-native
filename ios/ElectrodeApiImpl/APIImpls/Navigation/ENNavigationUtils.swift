/*
 * Copyright 2021 Walmart Labs
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

class ENNavigationUtils {

    static func jsonToString(json: [AnyHashable: Any]) -> String {
        do {
            let data1 =  try JSONSerialization.data(withJSONObject: json, options: JSONSerialization.WritingOptions.fragmentsAllowed) // first of all convert json to the data
            let convertedString = String(data: data1, encoding: String.Encoding.utf8) // the data will be converted to the string
            return convertedString ?? ""
        } catch let error as NSError {
            let logger = ElectrodeConsoleLogger.sharedInstance()
            logger.log(.error, message: error.description)
        }
        return ""
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
