#if swift(>=4.0)
@objcMembers public class NavigationBarButton: ElectrodeObject, Bridgeable {

    private static let tag = String(describing: type(of: self))

    /**
     Button title if any.
     */
    public let title: String?
    /**
     Icon resource identifier that can be used find the icon
     */
    public let icon: String?
    /**
     Id of the button, this namespace will be used as an identifier when a button click event is emitted.
     */
    public let id: String
    /**
     Allowed enums: left, right
     */
    public let location: String
    /**
     Default to false. If set to true the button will be disabled(non-clickable)
     */
    public let disabled: Bool?
    /**
     Accessibility label
     */
    public let adaLabel: String?

    public init(title: String?, icon: String?, id: String, location: String, disabled: Bool?, adaLabel: String?) {
        self.title = title
        self.icon = icon
        self.id = id
        self.location = location
        self.disabled = disabled
        self.adaLabel = adaLabel
        super.init()
    }

    public override init() {
        self.id = String()
        self.location = String()
        self.title = nil
        self.icon = nil
        self.disabled = nil
        self.adaLabel = nil
        super.init()
    }

    required public init(dictionary:[AnyHashable:Any]) {
        

        if let id = dictionary["id"] as? String  {
                  self.id = id
        } else {
            assertionFailure("\(NavigationBarButton.tag) missing one or more required properties [id] ")
            self.id = dictionary["id"] as! String
        }

                 

        if let location = dictionary["location"] as? String  {
                  self.location = location
        } else {
            assertionFailure("\(NavigationBarButton.tag) missing one or more required properties [location] ")
            self.location = dictionary["location"] as! String
        }

         


        if let title = dictionary["title"] as? String {
            self.title = title
        } else {
            self.title = nil
        }
        

        if let icon = dictionary["icon"] as? String {
            self.icon = icon
        } else {
            self.icon = nil
        }
        

        if let disabled = dictionary["disabled"] as? Bool {
            self.disabled = disabled
        } else {
            self.disabled = nil
        }
        

        if let adaLabel = dictionary["adaLabel"] as? String {
            self.adaLabel = adaLabel
        } else {
            self.adaLabel = nil
        }
        
        super.init(dictionary: dictionary)
    }

    public func toDictionary() -> NSDictionary {

         var dict = [:] as [AnyHashable : Any]

         dict["id"] =  self.id
dict["location"] =  self.location

        if let nonNullTitle = self.title {
                dict["title"] = nonNullTitle
        }
        if let nonNullIcon = self.icon {
                dict["icon"] = nonNullIcon
        }
        if let nonNullDisabled = self.disabled {
                dict["disabled"] = nonNullDisabled
        }
        if let nonNullAdaLabel = self.adaLabel {
                dict["adaLabel"] = nonNullAdaLabel
        }
        return dict as NSDictionary
    }
}
#else

public class NavigationBarButton: ElectrodeObject, Bridgeable {

    private static let tag = String(describing: type(of: self))

    /**
     Button title if any.
     */
    public let title: String?
    /**
     Icon resource identifier that can be used find the icon
     */
    public let icon: String?
    /**
     Id of the button, this namespace will be used as an identifier when a button click event is emitted.
     */
    public let id: String
    /**
     Allowed enums: left, right
     */
    public let location: String
    /**
     Default to false. If set to true the button will be disabled(non-clickable)
     */
    public let disabled: Bool?
    /**
     Accessibility label
     */
    public let adaLabel: String?

    public init(title: String?, icon: String?, id: String, location: String, disabled: Bool?, adaLabel: String?) {
        self.title = title
        self.icon = icon
        self.id = id
        self.location = location
        self.disabled = disabled
        self.adaLabel = adaLabel
        super.init()
    }

    public override init() {
        self.id = String()
        self.location = String()
        self.title = nil
        self.icon = nil
        self.disabled = nil
        self.adaLabel = nil
        super.init()
    }

    required public init(dictionary:[AnyHashable:Any]) {
        

        if let id = dictionary["id"] as? String  {
                  self.id = id
        } else {
            assertionFailure("\(NavigationBarButton.tag) missing one or more required properties [id] ")
            self.id = dictionary["id"] as! String
        }

                 

        if let location = dictionary["location"] as? String  {
                  self.location = location
        } else {
            assertionFailure("\(NavigationBarButton.tag) missing one or more required properties [location] ")
            self.location = dictionary["location"] as! String
        }

         


        if let title = dictionary["title"] as? String {
            self.title = title
        } else {
            self.title = nil
        }
        

        if let icon = dictionary["icon"] as? String {
            self.icon = icon
        } else {
            self.icon = nil
        }
        

        if let disabled = dictionary["disabled"] as? Bool {
            self.disabled = disabled
        } else {
            self.disabled = nil
        }
        

        if let adaLabel = dictionary["adaLabel"] as? String {
            self.adaLabel = adaLabel
        } else {
            self.adaLabel = nil
        }
        
        super.init(dictionary: dictionary)
    }

    public func toDictionary() -> NSDictionary {

         var dict = [:] as [AnyHashable : Any]

         dict["id"] =  self.id
dict["location"] =  self.location

        if let nonNullTitle = self.title {
                dict["title"] = nonNullTitle
        }
        if let nonNullIcon = self.icon {
                dict["icon"] = nonNullIcon
        }
        if let nonNullDisabled = self.disabled {
                dict["disabled"] = nonNullDisabled
        }
        if let nonNullAdaLabel = self.adaLabel {
                dict["adaLabel"] = nonNullAdaLabel
        }
        return dict as NSDictionary
    }
}
#endif
