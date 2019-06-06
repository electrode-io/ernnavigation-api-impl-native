package com.ern.api.impl.navigation;

import androidx.annotation.NonNull;

public interface RoutingNotifier {
    /**
     * Used to indicate if the routing request was handled
     *
     * @param result {@link RoutingResult}
     */
    void routingComplete(@NonNull RoutingResult result);
}