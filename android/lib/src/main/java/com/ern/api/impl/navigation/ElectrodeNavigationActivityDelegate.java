package com.ern.api.impl.navigation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModelProvider;

import com.ern.api.impl.core.ElectrodeBaseActivityDelegate;
import com.ern.api.impl.core.LaunchConfig;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;

public class ElectrodeNavigationActivityDelegate extends ElectrodeBaseActivityDelegate<NavigationLaunchConfig> {

    private static final String TAG = ElectrodeNavigationActivityDelegate.class.getSimpleName();

    @Nullable
    private ReactNavigationViewModel mNavViewModel;

    /**
     * @param activity            Hosting activity
     * @param rootComponentName   First react native component to be launched.
     * @param defaultLaunchConfig : {@link LaunchConfig} that acts as the the initial configuration to load the rootComponent as well as the default launch config for subsequent navigation flows.
     *                            This configuration will also be used as a default configuration when the root component tries to navigate to a new pages if a proper launch config is passed inside {@link #startMiniAppFragment(String, LaunchConfig)}.
     */
    public ElectrodeNavigationActivityDelegate(@NonNull FragmentActivity activity, @Nullable String rootComponentName, @NonNull NavigationLaunchConfig defaultLaunchConfig) {
        super(activity, rootComponentName, defaultLaunchConfig);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (mDefaultLaunchConfig.mUseActivityScopedNavigation) {
            registerNavRequestHandler();
        }
        super.onCreate(savedInstanceState);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    @Override
    public void onResume() {
        super.onResume();
        if (mDefaultLaunchConfig.mUseActivityScopedNavigation && mNavViewModel != null) {
            mNavViewModel.registerNavRequestHandler();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDefaultLaunchConfig.mUseActivityScopedNavigation && mNavViewModel != null) {
            mNavViewModel.unRegisterNavRequestHandler();
        }
    }

    private void registerNavRequestHandler() {
        mNavViewModel = new ViewModelProvider(mFragmentActivity, new ViewModelProvider.NewInstanceFactory()).get(ReactNavigationViewModel.class);
        mNavViewModel.getRouteLiveData().observe(mFragmentActivity, new Observer<Route>() {
            @Override
            public void onChanged(Route route) {
                if (route != null && !route.isCompleted()) {
                    Logger.d(TAG, "Navigation request handled by Activity(%s)", mFragmentActivity);
                    NavigationRouteHandler routeHandler = null;
                    if (mDefaultLaunchConfig.mRouteHandlerProvider != null && mDefaultLaunchConfig.mRouteHandlerProvider.getRouteHandler() != null) {
                        Logger.v(TAG, "Getting route handler via RouteHandlerProvider");
                        routeHandler = mDefaultLaunchConfig.mRouteHandlerProvider.getRouteHandler();
                    } else {
                        Fragment f = mFragmentActivity.getSupportFragmentManager().findFragmentById(mDefaultLaunchConfig.getFragmentContainerId());
                        if (f instanceof NavigationRouteHandler) {
                            Logger.v(TAG, "Getting fragment(route handler) hosted inside getFragmentContainer of activity");
                            routeHandler = (NavigationRouteHandler) f;
                        } else {
                            Logger.w(TAG, "Fragment: %s is not a NavigationRouteHandler", f);
                        }
                    }

                    if (routeHandler != null) {
                        routeHandler.handleRoute(route);
                    } else {
                        Logger.w(TAG, "Request handler is not able to find a RouteHandler for this request: " + route.getArguments());
                        route.setResult(false, "Failed to handle request, missing route handler");
                    }
                } else {
                    Logger.d(TAG, "Delegate: %s has ignored an already handled route: %s, ", ElectrodeNavigationActivityDelegate.this, route != null ? route.getArguments() : null);
                }
            }
        });
        mNavViewModel.registerNavRequestHandler();
    }

    protected boolean fragmentScopedNavModel() {
        return !mDefaultLaunchConfig.mUseActivityScopedNavigation;
    }
}
