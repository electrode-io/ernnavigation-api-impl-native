package com.ern.api.impl.navigation;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ern.api.impl.core.ElectrodeBaseFragment;
import com.ern.api.impl.core.LaunchConfig;
import com.ern.api.impl.core.UpdatePropsListener;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;

/**
 * Default fragment for Electrode Native Navigation
 * <p>
 * Use this fragment to host a React Native component that uses ern-navigation library to navigate between pages.
 */
public class MiniAppNavigationFragment extends ElectrodeBaseFragment<ElectrodeNavigationFragmentDelegate> implements ElectrodeNavigationFragmentDelegate.FragmentNavigator, ElectrodeNavigationFragmentDelegate.OnUpdateNextPageLaunchConfigListener, UpdatePropsListener, NavigationRouteHandler {
    private static final String TAG = MiniAppNavigationFragment.class.getSimpleName();

    @NonNull
    @Override
    protected ElectrodeNavigationFragmentDelegate createFragmentDelegate() {
        return new ElectrodeNavigationFragmentDelegate(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mElectrodeReactFragmentDelegate.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mElectrodeReactFragmentDelegate.onOptionsItemSelected(item);
    }

    @Override
    public boolean navigate(@Nullable String pageName, @NonNull Bundle data) {
        return false;
    }

    @Override
    public void updateNextPageLaunchConfig(@NonNull String nextPageName, @NonNull LaunchConfig defaultLaunchConfig) {
    }

    @Override
    public void refresh(@Nullable Bundle data) {
        mElectrodeReactFragmentDelegate.refresh(data);
    }

    @Override
    public void handleRoute(@NonNull Route route) {
        Logger.v(TAG, "Delegating handleRoute request for route: %s", route);
        mElectrodeReactFragmentDelegate.handleRoute(route, this);
    }
}
