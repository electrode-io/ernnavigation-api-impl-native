#if swift(>=4.0)
@objcMembers public class EnNavigationEvents:  EnNavigationAPIEvents {
    public override func addOnNavButtonClickEventListener(eventListener: @escaping ElectrodeBridgeEventListener) -> UUID?{
        let listenerProcessor = EventListenerProcessor(
                                eventName: EnNavigationAPI.kEventOnNavButtonClick,
                                eventPayloadClass: String.self,
                                eventListener: eventListener)

        return listenerProcessor.execute()
    }


    public override func removeOnNavButtonClickEventListener(uuid: UUID) -> ElectrodeBridgeEventListener? {
        return ElectrodeBridgeHolder.removeEventListener(uuid)
    }


    public override func emitEventOnNavButtonClick(buttonId: String) {
        let eventProcessor = EventProcessor(
                                eventName: EnNavigationAPI.kEventOnNavButtonClick,
                                eventPayload: buttonId)

        eventProcessor.execute()
    }

}
#else
public class EnNavigationEvents:  EnNavigationAPIEvents {
    public override func addOnNavButtonClickEventListener(eventListener: @escaping ElectrodeBridgeEventListener) -> UUID?{
        let listenerProcessor = EventListenerProcessor(
                                eventName: EnNavigationAPI.kEventOnNavButtonClick,
                                eventPayloadClass: String.self,
                                eventListener: eventListener)

        return listenerProcessor.execute()
    }

    public override func removeOnNavButtonClickEventListener(uuid: UUID) -> ElectrodeBridgeEventListener? {
        return ElectrodeBridgeHolder.removeEventListener(uuid)
    }

    public override func emitEventOnNavButtonClick(buttonId: String) {
        let eventProcessor = EventProcessor(
                                eventName: EnNavigationAPI.kEventOnNavButtonClick,
                                eventPayload: buttonId)

        eventProcessor.execute()
    }
}
#endif
