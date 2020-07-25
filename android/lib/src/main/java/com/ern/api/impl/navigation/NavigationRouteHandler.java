package com.ern.api.impl.navigation;

import androidx.annotation.NonNull;

/**
 * This allows a fragment to receive a route from external sources.
 * <p>
 * Find how an activity delegates the routing to it's fragment in [{@link ElectrodeNavigationActivityDelegate}]
 */
public interface NavigationRouteHandler {
    void handleRoute(@NonNull Route route);
}
