package com.ern.api.impl.navigation;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * Calling {@link androidx.fragment.app.FragmentTransaction#add(Fragment, String)} results in adding a new fragment to the stack with out putting the current fragment in stopped state.
 * ComponentAsOverlay is introduced to handle this use case, where the currentFragment that starts an overlay will delegate the navigation requests using this interface.
 *
 * @see OverlayFragment, {@link ElectrodeNavigationFragmentDelegate#routeObserver}
 */
interface ComponentAsOverlay {
    void navigate(@NonNull Route route);

    void update(@NonNull Route route);

    void back(@NonNull Route route);

    void finish(@NonNull Route route);
}
