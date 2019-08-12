#if swift(>=4.0)
@objcMembers public class ErnNavRoute: ElectrodeObject, Bridgeable {

    private static let tag = String(describing: type(of: self))

    /**
     Path of the Route. Mostly the name of the container(defined by the native app) or the miniapp name. The content of the path is mainly determined by the native implemenation of the API
     */
    public let path: String
    /**
     Optional Payload (respresented as JSON String) needed by the screen you are trying to navigate to.
     */
    public let jsonPayload: String?
    public let navigationBar: NavigationBar?

    public init(path: String, jsonPayload: String?, navigationBar: NavigationBar?) {
        self.path = path
        self.jsonPayload = jsonPayload
        self.navigationBar = navigationBar
        super.init()
    }

    public override init() {
        self.path = String()
        self.jsonPayload = nil
        self.navigationBar = nil
        super.init()
    }

    required public init(dictionary:[AnyHashable:Any]) {
        

        if let path = dictionary["path"] as? String  {
                  self.path = path
        } else {
            assertionFailure("\(ErnNavRoute.tag) missing one or more required properties [path] ")
            self.path = dictionary["path"] as! String
        }

         


        if let jsonPayload = dictionary["jsonPayload"] as? String {
            self.jsonPayload = jsonPayload
        } else {
            self.jsonPayload = nil
        }
        

        if let navigationBarDict = dictionary["navigationBar"] as? [AnyHashable: Any] {
            self.navigationBar = NavigationBar(dictionary: navigationBarDict)
        } else {
            self.navigationBar = nil
        }
        
        super.init(dictionary: dictionary)
    }

    public func toDictionary() -> NSDictionary {

         var dict = [:] as [AnyHashable : Any]

         dict["path"] =  self.path

        if let nonNullJsonPayload = self.jsonPayload {
                dict["jsonPayload"] = nonNullJsonPayload
        }
        if let nonNullNavigationBar = self.navigationBar {
                dict["navigationBar"] = nonNullNavigationBar.toDictionary()
        }
        return dict as NSDictionary
    }
}
#else

public class ErnNavRoute: ElectrodeObject, Bridgeable {

    private static let tag = String(describing: type(of: self))

    /**
     Path of the Route. Mostly the name of the container(defined by the native app) or the miniapp name. The content of the path is mainly determined by the native implemenation of the API
     */
    public let path: String
    /**
     Optional Payload (respresented as JSON String) needed by the screen you are trying to navigate to.
     */
    public let jsonPayload: String?
    public let navigationBar: NavigationBar?

    public init(path: String, jsonPayload: String?, navigationBar: NavigationBar?) {
        self.path = path
        self.jsonPayload = jsonPayload
        self.navigationBar = navigationBar
        super.init()
    }

    public override init() {
        self.path = String()
        self.jsonPayload = nil
        self.navigationBar = nil
        super.init()
    }

    required public init(dictionary:[AnyHashable:Any]) {
        

        if let path = dictionary["path"] as? String  {
                  self.path = path
        } else {
            assertionFailure("\(ErnNavRoute.tag) missing one or more required properties [path] ")
            self.path = dictionary["path"] as! String
        }

         


        if let jsonPayload = dictionary["jsonPayload"] as? String {
            self.jsonPayload = jsonPayload
        } else {
            self.jsonPayload = nil
        }
        

        if let navigationBarDict = dictionary["navigationBar"] as? [AnyHashable: Any] {
            self.navigationBar = NavigationBar(dictionary: navigationBarDict)
        } else {
            self.navigationBar = nil
        }
        
        super.init(dictionary: dictionary)
    }

    public func toDictionary() -> NSDictionary {

         var dict = [:] as [AnyHashable : Any]

         dict["path"] =  self.path

        if let nonNullJsonPayload = self.jsonPayload {
                dict["jsonPayload"] = nonNullJsonPayload
        }
        if let nonNullNavigationBar = self.navigationBar {
                dict["navigationBar"] = nonNullNavigationBar.toDictionary()
        }
        return dict as NSDictionary
    }
}
#endif
