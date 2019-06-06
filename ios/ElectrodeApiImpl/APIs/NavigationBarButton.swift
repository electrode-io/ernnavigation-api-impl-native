#if swift(>=4.0)
@objcMembers public class NavigationBarButton: ElectrodeObject, Bridgeable {

    private static let tag = String(describing: type(of: self))

    /**
     Name of button
     */
    public let name: String
    /**
     Id of the button
     */
    public let identifier: String
    /**
     Orientation LEFT|RIGHT|CENTER etc.
     */
    public let orientation: String

    public init(name: String, identifier: String, orientation: String) {
        self.name = name
        self.identifier = identifier
        self.orientation = orientation
        super.init()
    }

    public override init() {
        self.name = String()
        self.identifier = String()
        self.orientation = String()
        super.init()
    }

    required public init(dictionary:[AnyHashable:Any]) {
        

        if let name = dictionary["name"] as? String  {
                  self.name = name
        } else {
            assertionFailure("\(NavigationBarButton.tag) missing one or more required properties [name] ")
            self.name = dictionary["name"] as! String
        }

                 

        if let identifier = dictionary["identifier"] as? String  {
                  self.identifier = identifier
        } else {
            assertionFailure("\(NavigationBarButton.tag) missing one or more required properties [identifier] ")
            self.identifier = dictionary["identifier"] as! String
        }

                 

        if let orientation = dictionary["orientation"] as? String  {
                  self.orientation = orientation
        } else {
            assertionFailure("\(NavigationBarButton.tag) missing one or more required properties [orientation] ")
            self.orientation = dictionary["orientation"] as! String
        }

         

        super.init(dictionary: dictionary)
    }

    public func toDictionary() -> NSDictionary {

         var dict = [:] as [AnyHashable : Any]

         dict["name"] =  self.name
dict["identifier"] =  self.identifier
dict["orientation"] =  self.orientation

        return dict as NSDictionary
    }
}
#else

public class NavigationBarButton: ElectrodeObject, Bridgeable {

    private static let tag = String(describing: type(of: self))

    /**
     Name of button
     */
    public let name: String
    /**
     Id of the button
     */
    public let identifier: String
    /**
     Orientation LEFT|RIGHT|CENTER etc.
     */
    public let orientation: String

    public init(name: String, identifier: String, orientation: String) {
        self.name = name
        self.identifier = identifier
        self.orientation = orientation
        super.init()
    }

    public override init() {
        self.name = String()
        self.identifier = String()
        self.orientation = String()
        super.init()
    }

    required public init(dictionary:[AnyHashable:Any]) {
        

        if let name = dictionary["name"] as? String  {
                  self.name = name
        } else {
            assertionFailure("\(NavigationBarButton.tag) missing one or more required properties [name] ")
            self.name = dictionary["name"] as! String
        }

                 

        if let identifier = dictionary["identifier"] as? String  {
                  self.identifier = identifier
        } else {
            assertionFailure("\(NavigationBarButton.tag) missing one or more required properties [identifier] ")
            self.identifier = dictionary["identifier"] as! String
        }

                 

        if let orientation = dictionary["orientation"] as? String  {
                  self.orientation = orientation
        } else {
            assertionFailure("\(NavigationBarButton.tag) missing one or more required properties [orientation] ")
            self.orientation = dictionary["orientation"] as! String
        }

         

        super.init(dictionary: dictionary)
    }

    public func toDictionary() -> NSDictionary {

         var dict = [:] as [AnyHashable : Any]

         dict["name"] =  self.name
dict["identifier"] =  self.identifier
dict["orientation"] =  self.orientation

        return dict as NSDictionary
    }
}
#endif
