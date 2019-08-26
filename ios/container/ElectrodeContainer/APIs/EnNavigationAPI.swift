#if swift(>=4.0)
@objcMembers public class EnNavigationAPI: NSObject  {

    static let kRequestBack = "com.ernnavigationApi.ern.api.request.back";

    static let kRequestFinish = "com.ernnavigationApi.ern.api.request.finish";

    static let kRequestNavigate = "com.ernnavigationApi.ern.api.request.navigate";
    static let kEventOnNavButtonClick = "com.ernnavigationApi.ern.api.event.onNavButtonClick";


    static let kRequestUpdate = "com.ernnavigationApi.ern.api.request.update";

    public lazy var events: EnNavigationAPIEvents = {
        return EnNavigationEvents()
    }()


    public lazy var requests: EnNavigationAPIRequests = {
        return EnNavigationRequests()
    }()
}

@objcMembers public class EnNavigationAPIEvents: NSObject {
    public func addOnNavButtonClickEventListener(eventListener: @escaping ElectrodeBridgeEventListener) -> UUID?{
        assertionFailure("should override")
        return UUID()
    }

    public func removeOnNavButtonClickEventListener(uuid: UUID) -> ElectrodeBridgeEventListener? {
        assertionFailure("should override")
        return nil
    }

    public func emitEventOnNavButtonClick(buttonId: String) {
        assertionFailure("should override")

    }
}

@objcMembers public class EnNavigationAPIRequests: NSObject {
    public func registerBackRequestHandler(handler: @escaping ElectrodeBridgeRequestCompletionHandler) -> UUID?{
        assertionFailure("should override")
        return UUID()
    }

    public func registerFinishRequestHandler(handler: @escaping ElectrodeBridgeRequestCompletionHandler) -> UUID?{
        assertionFailure("should override")
        return UUID()
    }

    public func registerNavigateRequestHandler(handler: @escaping ElectrodeBridgeRequestCompletionHandler) -> UUID?{
        assertionFailure("should override")
        return UUID()
    }

    public func registerUpdateRequestHandler(handler: @escaping ElectrodeBridgeRequestCompletionHandler) -> UUID?{
        assertionFailure("should override")
        return UUID()
    }


    public func unregisterBackRequestHandler(uuid: UUID) -> ElectrodeBridgeRequestCompletionHandler? {
        assertionFailure("should override")
        return nil
    }

    public func unregisterFinishRequestHandler(uuid: UUID) -> ElectrodeBridgeRequestCompletionHandler? {
        assertionFailure("should override")
        return nil
    }

    public func unregisterNavigateRequestHandler(uuid: UUID) -> ElectrodeBridgeRequestCompletionHandler? {
        assertionFailure("should override")
        return nil
    }

    public func unregisterUpdateRequestHandler(uuid: UUID) -> ElectrodeBridgeRequestCompletionHandler? {
        assertionFailure("should override")
        return nil
    }


    public func back(route: ErnNavRoute, responseCompletionHandler: @escaping ElectrodeBridgeResponseCompletionHandler) {
        assertionFailure("should override")
    }

    public func finish(finalPayload: String, responseCompletionHandler: @escaping ElectrodeBridgeResponseCompletionHandler) {
        assertionFailure("should override")
    }

    public func navigate(route: ErnNavRoute, responseCompletionHandler: @escaping ElectrodeBridgeResponseCompletionHandler) {
        assertionFailure("should override")
    }

    public func update(updatedRoute: ErnNavRoute, responseCompletionHandler: @escaping ElectrodeBridgeResponseCompletionHandler) {
        assertionFailure("should override")
    }

}
#else
public class EnNavigationAPI: NSObject  {

    static let kRequestBack = "com.ernnavigationApi.ern.api.request.back";

    static let kRequestFinish = "com.ernnavigationApi.ern.api.request.finish";

    static let kRequestNavigate = "com.ernnavigationApi.ern.api.request.navigate";
    static let kEventOnNavButtonClick = "com.ernnavigationApi.ern.api.event.onNavButtonClick";


    static let kRequestUpdate = "com.ernnavigationApi.ern.api.request.update";

    public lazy var events: EnNavigationAPIEvents = {
        return EnNavigationEvents()
    }()


    public lazy var requests: EnNavigationAPIRequests = {
        return EnNavigationRequests()
    }()
}

public class EnNavigationAPIEvents: NSObject {
    public func addOnNavButtonClickEventListener(eventListener: @escaping ElectrodeBridgeEventListener) -> UUID?{
        assertionFailure("should override")
        return UUID()
    }

    public func removeOnNavButtonClickEventListener(uuid: UUID) -> ElectrodeBridgeEventListener? {
        assertionFailure("should override")
        return nil
    }

    public func emitEventOnNavButtonClick(buttonId: String) {
        assertionFailure("should override")

    }
}

public class EnNavigationAPIRequests: NSObject {
    public func registerBackRequestHandler(handler: @escaping ElectrodeBridgeRequestCompletionHandler) -> UUID?{
        assertionFailure("should override")
        return UUID()
    }

    public func registerFinishRequestHandler(handler: @escaping ElectrodeBridgeRequestCompletionHandler) -> UUID?{
        assertionFailure("should override")
        return UUID()
    }

    public func registerNavigateRequestHandler(handler: @escaping ElectrodeBridgeRequestCompletionHandler) -> UUID?{
        assertionFailure("should override")
        return UUID()
    }

    public func registerUpdateRequestHandler(handler: @escaping ElectrodeBridgeRequestCompletionHandler) -> UUID?{
        assertionFailure("should override")
        return UUID()
    }


    public func unregisterBackRequestHandler(uuid: UUID) -> ElectrodeBridgeRequestCompletionHandler? {
        assertionFailure("should override")
        return nil
    }

    public func unregisterFinishRequestHandler(uuid: UUID) -> ElectrodeBridgeRequestCompletionHandler? {
        assertionFailure("should override")
        return nil
    }

    public func unregisterNavigateRequestHandler(uuid: UUID) -> ElectrodeBridgeRequestCompletionHandler? {
        assertionFailure("should override")
        return nil
    }

    public func unregisterUpdateRequestHandler(uuid: UUID) -> ElectrodeBridgeRequestCompletionHandler? {
        assertionFailure("should override")
        return nil
    }


    public func back(route: ErnNavRoute, responseCompletionHandler: @escaping ElectrodeBridgeResponseCompletionHandler) {
        assertionFailure("should override")
    }

    public func finish(finalPayload: String, responseCompletionHandler: @escaping ElectrodeBridgeResponseCompletionHandler) {
        assertionFailure("should override")
    }

    public func navigate(route: ErnNavRoute, responseCompletionHandler: @escaping ElectrodeBridgeResponseCompletionHandler) {
        assertionFailure("should override")
    }

    public func update(updatedRoute: ErnNavRoute, responseCompletionHandler: @escaping ElectrodeBridgeResponseCompletionHandler) {
        assertionFailure("should override")
    }

}

#endif
