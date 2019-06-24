#if swift(>=4.0)
@objcMembers public class NavigationBar: ElectrodeObject, Bridgeable {

    private static let tag = String(describing: type(of: self))

    /**
     Title of Nav Bar
     */
    public let title: String
    /**
     Use to hide the navigation bar.
     */
    public let hide: Bool?
    /**
     Right button properties
     */
    public let buttons: [NavigationBarButton]?

    public init(title: String, hide: Bool?, buttons: [NavigationBarButton]?) {
        self.title = title
        self.hide = hide
        self.buttons = buttons
        super.init()
    }

    public override init() {
        self.title = String()
        self.hide = nil
        self.buttons = nil
        super.init()
    }

    required public init(dictionary:[AnyHashable:Any]) {
        

        if let title = dictionary["title"] as? String  {
                  self.title = title
        } else {
            assertionFailure("\(NavigationBar.tag) missing one or more required properties [title] ")
            self.title = dictionary["title"] as! String
        }

         


        if let hide = dictionary["hide"] as? Bool {
            self.hide = hide
        } else {
            self.hide = nil
        }
        
        if let validButtons = try? NSObject.generateObject(data: dictionary["buttons"], classType: Array<Any>.self, itemType: NavigationBarButton.self),
           let buttonsList = validButtons as? [NavigationBarButton] {
            self.buttons = buttonsList
        } else {
            self.buttons = nil
        }

        super.init(dictionary: dictionary)
    }

    public func toDictionary() -> NSDictionary {

         var dict = [:] as [AnyHashable : Any]

         dict["title"] =  self.title

        if let nonNullHide = self.hide {
                dict["hide"] = nonNullHide
        }
        if let nonNullButtons = self.buttons {
                dict["buttons"] = nonNullButtons.map{$0.toDictionary()}
        }
        return dict as NSDictionary
    }
}
#else

public class NavigationBar: ElectrodeObject, Bridgeable {

    private static let tag = String(describing: type(of: self))

    /**
     Title of Nav Bar
     */
    public let title: String
    /**
     Use to hide the navigation bar.
     */
    public let hide: Bool?
    /**
     Right button properties
     */
    public let buttons: [NavigationBarButton]?

    public init(title: String, hide: Bool?, buttons: [NavigationBarButton]?) {
        self.title = title
        self.hide = hide
        self.buttons = buttons
        super.init()
    }

    public override init() {
        self.title = String()
        self.hide = nil
        self.buttons = nil
        super.init()
    }

    required public init(dictionary:[AnyHashable:Any]) {
        

        if let title = dictionary["title"] as? String  {
                  self.title = title
        } else {
            assertionFailure("\(NavigationBar.tag) missing one or more required properties [title] ")
            self.title = dictionary["title"] as! String
        }

         


        if let hide = dictionary["hide"] as? Bool {
            self.hide = hide
        } else {
            self.hide = nil
        }
        
        if let validButtons = try? NSObject.generateObject(data: dictionary["buttons"], classType: Array<Any>.self, itemType: NavigationBarButton.self),
           let buttonsList = validButtons as? [NavigationBarButton] {
            self.buttons = buttonsList
        } else {
            self.buttons = nil
        }

        super.init(dictionary: dictionary)
    }

    public func toDictionary() -> NSDictionary {

         var dict = [:] as [AnyHashable : Any]

         dict["title"] =  self.title

        if let nonNullHide = self.hide {
                dict["hide"] = nonNullHide
        }
        if let nonNullButtons = self.buttons {
                dict["buttons"] = nonNullButtons.map{$0.toDictionary()}
        }
        return dict as NSDictionary
    }
}
#endif
