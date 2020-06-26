package com.ern.api.impl.navigation;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ern.api.impl.core.ElectrodeBaseActivityDelegate;
import com.ern.api.impl.core.LaunchConfig;

public class NavigationLaunchConfig extends LaunchConfig {
    boolean mUseActivityScopedNavigation;

    RouteHandlerProvider<? extends Fragment> mRouteHandlerProvider;

    /**
     * Use this when you don't need each fragment to register it's own navigation request handlers.
     * When true: {@link com.ern.api.impl.navigation.ReactNavigationViewModel} will be created with activity scope instead of fragment scope.
     * The activity will then delegate the navigation requests to the corresponding fragments via {@link com.ern.api.impl.navigation.NavigationRouteHandler} implementation.
     * The {@link ElectrodeBaseActivityDelegate} will take the current instance of the {@link Fragment} that is loaded inside {@link LaunchConfig#getFragmentContainerId()}. Assumption is that this fragment implements {@link NavigationRouteHandler}
     * Pass {@link RouteHandlerProvider} If you would like to provide your own implementation of providing the fragment instance, valid only if #value is true.
     */
    public void useActivityScopeForNavigation(boolean value, @Nullable RouteHandlerProvider<? extends Fragment> routeHandlerProvider) {
        mUseActivityScopedNavigation = value;
        mRouteHandlerProvider = routeHandlerProvider;
    }

    public interface RouteHandlerProvider<T extends Fragment & NavigationRouteHandler> {
        /**
         * Provide the current visible fragment that is hosting the React Native view component which needs to handle the navigation request
         *
         * @return Fragment implements {@link NavigationRouteHandler}
         */
        @Nullable
        T getRouteHandler();
    }
}
