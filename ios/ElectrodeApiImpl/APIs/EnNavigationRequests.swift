#if swift(>=4.0)
@objcMembers public class EnNavigationRequests: EnNavigationAPIRequests {

    public override func registerBackRequestHandler(handler:  @escaping ElectrodeBridgeRequestCompletionHandler) -> UUID?{
        let requestHandlerProcessor = ElectrodeRequestHandlerProcessor(requestName: EnNavigationAPI.kRequestBack,
    reqClass: ErnNavRoute.self, 
    respClass: None.self,
    requestCompletionHandler: handler)
        return requestHandlerProcessor.execute()
    }

    public override func registerFinishRequestHandler(handler:  @escaping ElectrodeBridgeRequestCompletionHandler) -> UUID?{
        let requestHandlerProcessor = ElectrodeRequestHandlerProcessor(requestName: EnNavigationAPI.kRequestFinish,
    reqClass: String.self, 
    respClass: None.self,
    requestCompletionHandler: handler)
        return requestHandlerProcessor.execute()
    }

    public override func registerNavigateRequestHandler(handler:  @escaping ElectrodeBridgeRequestCompletionHandler) -> UUID?{
        let requestHandlerProcessor = ElectrodeRequestHandlerProcessor(requestName: EnNavigationAPI.kRequestNavigate,
    reqClass: ErnNavRoute.self, 
    respClass: None.self,
    requestCompletionHandler: handler)
        return requestHandlerProcessor.execute()
    }

    public override func registerUpdateRequestHandler(handler:  @escaping ElectrodeBridgeRequestCompletionHandler) -> UUID?{
        let requestHandlerProcessor = ElectrodeRequestHandlerProcessor(requestName: EnNavigationAPI.kRequestUpdate,
    reqClass: ErnNavRoute.self, 
    respClass: None.self,
    requestCompletionHandler: handler)
        return requestHandlerProcessor.execute()
    }


    public override func unregisterBackRequestHandler(uuid: UUID) -> ElectrodeBridgeRequestCompletionHandler? {
        return ElectrodeBridgeHolder.unregisterRequestHandler(with: uuid)
    }

    public override func unregisterFinishRequestHandler(uuid: UUID) -> ElectrodeBridgeRequestCompletionHandler? {
        return ElectrodeBridgeHolder.unregisterRequestHandler(with: uuid)
    }

    public override func unregisterNavigateRequestHandler(uuid: UUID) -> ElectrodeBridgeRequestCompletionHandler? {
        return ElectrodeBridgeHolder.unregisterRequestHandler(with: uuid)
    }

    public override func unregisterUpdateRequestHandler(uuid: UUID) -> ElectrodeBridgeRequestCompletionHandler? {
        return ElectrodeBridgeHolder.unregisterRequestHandler(with: uuid)
    }

    //------------------------------------------------------------------------------------------------------------------------------------


    public override func back(route: ErnNavRoute, responseCompletionHandler: @escaping ElectrodeBridgeResponseCompletionHandler) {
        let requestProcessor = ElectrodeRequestProcessor<ErnNavRoute, None, Any>(
            requestName: EnNavigationAPI.kRequestBack,
            requestPayload: route,
            respClass: None.self,
            responseItemType: nil,
            responseCompletionHandler: responseCompletionHandler)

        requestProcessor.execute()
    }

    public override func finish(finalPayload: String, responseCompletionHandler: @escaping ElectrodeBridgeResponseCompletionHandler) {
        let requestProcessor = ElectrodeRequestProcessor<String, None, Any>(
            requestName: EnNavigationAPI.kRequestFinish,
            requestPayload: finalPayload,
            respClass: None.self,
            responseItemType: nil,
            responseCompletionHandler: responseCompletionHandler)

        requestProcessor.execute()
    }

    public override func navigate(route: ErnNavRoute, responseCompletionHandler: @escaping ElectrodeBridgeResponseCompletionHandler) {
        let requestProcessor = ElectrodeRequestProcessor<ErnNavRoute, None, Any>(
            requestName: EnNavigationAPI.kRequestNavigate,
            requestPayload: route,
            respClass: None.self,
            responseItemType: nil,
            responseCompletionHandler: responseCompletionHandler)

        requestProcessor.execute()
    }

    public override func update(updatedRoute: ErnNavRoute, responseCompletionHandler: @escaping ElectrodeBridgeResponseCompletionHandler) {
        let requestProcessor = ElectrodeRequestProcessor<ErnNavRoute, None, Any>(
            requestName: EnNavigationAPI.kRequestUpdate,
            requestPayload: updatedRoute,
            respClass: None.self,
            responseItemType: nil,
            responseCompletionHandler: responseCompletionHandler)

        requestProcessor.execute()
    }
}
#else
public class EnNavigationRequests: EnNavigationAPIRequests {

    public override func registerBackRequestHandler(handler:  @escaping ElectrodeBridgeRequestCompletionHandler) -> UUID?{
        let requestHandlerProcessor = ElectrodeRequestHandlerProcessor(requestName: EnNavigationAPI.kRequestBack,
    reqClass: ErnNavRoute.self, 
    respClass: None.self,
    requestCompletionHandler: handler)
        return requestHandlerProcessor.execute()
    }

    public override func registerFinishRequestHandler(handler:  @escaping ElectrodeBridgeRequestCompletionHandler) -> UUID?{
        let requestHandlerProcessor = ElectrodeRequestHandlerProcessor(requestName: EnNavigationAPI.kRequestFinish,
    reqClass: String.self, 
    respClass: None.self,
    requestCompletionHandler: handler)
        return requestHandlerProcessor.execute()
    }

    public override func registerNavigateRequestHandler(handler:  @escaping ElectrodeBridgeRequestCompletionHandler) -> UUID?{
        let requestHandlerProcessor = ElectrodeRequestHandlerProcessor(requestName: EnNavigationAPI.kRequestNavigate,
    reqClass: ErnNavRoute.self, 
    respClass: None.self,
    requestCompletionHandler: handler)
        return requestHandlerProcessor.execute()
    }

    public override func registerUpdateRequestHandler(handler:  @escaping ElectrodeBridgeRequestCompletionHandler) -> UUID?{
        let requestHandlerProcessor = ElectrodeRequestHandlerProcessor(requestName: EnNavigationAPI.kRequestUpdate,
    reqClass: ErnNavRoute.self, 
    respClass: None.self,
    requestCompletionHandler: handler)
        return requestHandlerProcessor.execute()
    }

    //------------------------------------------------------------------------------------------------------------------------------------



    public override func unregisterBackRequestHandler(uuid: UUID) -> ElectrodeBridgeRequestCompletionHandler? {
      return ElectrodeBridgeHolder.unregisterRequestHandler(with: uuid)
    }

    public override func unregisterFinishRequestHandler(uuid: UUID) -> ElectrodeBridgeRequestCompletionHandler? {
      return ElectrodeBridgeHolder.unregisterRequestHandler(with: uuid)
    }

    public override func unregisterNavigateRequestHandler(uuid: UUID) -> ElectrodeBridgeRequestCompletionHandler? {
      return ElectrodeBridgeHolder.unregisterRequestHandler(with: uuid)
    }

    public override func unregisterUpdateRequestHandler(uuid: UUID) -> ElectrodeBridgeRequestCompletionHandler? {
      return ElectrodeBridgeHolder.unregisterRequestHandler(with: uuid)
    }

    public override func back(route: ErnNavRoute, responseCompletionHandler: @escaping ElectrodeBridgeResponseCompletionHandler) {
        let requestProcessor = ElectrodeRequestProcessor<ErnNavRoute, None, Any>(
            requestName: EnNavigationAPI.kRequestBack,
            requestPayload: route,
            respClass: None.self,
            responseItemType: nil,
            responseCompletionHandler: responseCompletionHandler)

        requestProcessor.execute()
    }


    public override func unregisterBackRequestHandler(uuid: UUID) -> ElectrodeBridgeRequestCompletionHandler? {
      return ElectrodeBridgeHolder.unregisterRequestHandler(with: uuid)
    }

    public override func unregisterFinishRequestHandler(uuid: UUID) -> ElectrodeBridgeRequestCompletionHandler? {
      return ElectrodeBridgeHolder.unregisterRequestHandler(with: uuid)
    }

    public override func unregisterNavigateRequestHandler(uuid: UUID) -> ElectrodeBridgeRequestCompletionHandler? {
      return ElectrodeBridgeHolder.unregisterRequestHandler(with: uuid)
    }

    public override func unregisterUpdateRequestHandler(uuid: UUID) -> ElectrodeBridgeRequestCompletionHandler? {
      return ElectrodeBridgeHolder.unregisterRequestHandler(with: uuid)
    }

    public override func finish(finalPayload: String, responseCompletionHandler: @escaping ElectrodeBridgeResponseCompletionHandler) {
        let requestProcessor = ElectrodeRequestProcessor<String, None, Any>(
            requestName: EnNavigationAPI.kRequestFinish,
            requestPayload: finalPayload,
            respClass: None.self,
            responseItemType: nil,
            responseCompletionHandler: responseCompletionHandler)

        requestProcessor.execute()
    }


    public override func unregisterBackRequestHandler(uuid: UUID) -> ElectrodeBridgeRequestCompletionHandler? {
      return ElectrodeBridgeHolder.unregisterRequestHandler(with: uuid)
    }

    public override func unregisterFinishRequestHandler(uuid: UUID) -> ElectrodeBridgeRequestCompletionHandler? {
      return ElectrodeBridgeHolder.unregisterRequestHandler(with: uuid)
    }

    public override func unregisterNavigateRequestHandler(uuid: UUID) -> ElectrodeBridgeRequestCompletionHandler? {
      return ElectrodeBridgeHolder.unregisterRequestHandler(with: uuid)
    }

    public override func unregisterUpdateRequestHandler(uuid: UUID) -> ElectrodeBridgeRequestCompletionHandler? {
      return ElectrodeBridgeHolder.unregisterRequestHandler(with: uuid)
    }

    public override func navigate(route: ErnNavRoute, responseCompletionHandler: @escaping ElectrodeBridgeResponseCompletionHandler) {
        let requestProcessor = ElectrodeRequestProcessor<ErnNavRoute, None, Any>(
            requestName: EnNavigationAPI.kRequestNavigate,
            requestPayload: route,
            respClass: None.self,
            responseItemType: nil,
            responseCompletionHandler: responseCompletionHandler)

        requestProcessor.execute()
    }


    public override func unregisterBackRequestHandler(uuid: UUID) -> ElectrodeBridgeRequestCompletionHandler? {
      return ElectrodeBridgeHolder.unregisterRequestHandler(with: uuid)
    }

    public override func unregisterFinishRequestHandler(uuid: UUID) -> ElectrodeBridgeRequestCompletionHandler? {
      return ElectrodeBridgeHolder.unregisterRequestHandler(with: uuid)
    }

    public override func unregisterNavigateRequestHandler(uuid: UUID) -> ElectrodeBridgeRequestCompletionHandler? {
      return ElectrodeBridgeHolder.unregisterRequestHandler(with: uuid)
    }

    public override func unregisterUpdateRequestHandler(uuid: UUID) -> ElectrodeBridgeRequestCompletionHandler? {
      return ElectrodeBridgeHolder.unregisterRequestHandler(with: uuid)
    }

    public override func update(updatedRoute: ErnNavRoute, responseCompletionHandler: @escaping ElectrodeBridgeResponseCompletionHandler) {
        let requestProcessor = ElectrodeRequestProcessor<ErnNavRoute, None, Any>(
            requestName: EnNavigationAPI.kRequestUpdate,
            requestPayload: updatedRoute,
            respClass: None.self,
            responseItemType: nil,
            responseCompletionHandler: responseCompletionHandler)

        requestProcessor.execute()
    }
}
#endif
