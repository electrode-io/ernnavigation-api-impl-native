package com.ern.api.impl.navigation;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * Calling {@link androidx.fragment.app.FragmentTransaction#add(Fragment, String)} results in adding a new fragment to the stack with out putting the current fragment in stopped state.
 * ComponentAsOverlay is introduced to handle this use case, where the currentFragment that starts an overlay will delegate the navigation requests using this interface.
 *
 * @see OverlayFragment, {@link ElectrodeNavigationFragmentDelegate#routeObserver}
 */
public interface ComponentAsOverlay {
    /**
     * Implementation should invoke {@link ElectrodeNavigationFragmentDelegate#navigate(Route)}
     */
    void navigate(@NonNull Route route);

    /**
     * Implementation should invoke {@link ElectrodeNavigationFragmentDelegate#update(Route)}
     */
    void update(@NonNull Route route);

    /**
     * Implementation should invoke {@link ElectrodeNavigationFragmentDelegate#back(Route)}
     */
    void back(@NonNull Route route);

    /**
     * Implementation should invoke {@link ElectrodeNavigationFragmentDelegate#finish(Route)}
     */
    void finish(@NonNull Route route);
}
