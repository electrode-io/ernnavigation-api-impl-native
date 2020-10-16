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

import org.json.JSONObject;

/**
 * Default fragment for Electrode Native Navigation
 * <p>
 * Use this fragment to host a React Native component that uses ern-navigation library to navigate between pages.
 */
public class MiniAppNavigationFragment extends ElectrodeBaseFragment<ElectrodeNavigationFragmentDelegate<ElectrodeNavigationActivityListener, ElectrodeNavigationFragmentConfig>> implements
        ElectrodeNavigationFragmentDelegate.FragmentNavigator,
        ElectrodeNavigationFragmentDelegate.OnUpdateNextPageLaunchConfigListener,
        UpdatePropsListener,
        NavigationRouteHandler,
        ElectrodeNavigationFragmentDelegate.OnOptionsMenuUpdatedListener {
    private static final String TAG = MiniAppNavigationFragment.class.getSimpleName();

    @NonNull
    @Override
    protected ElectrodeNavigationFragmentDelegate<ElectrodeNavigationActivityListener, ElectrodeNavigationFragmentConfig> createFragmentDelegate() {
        return new ElectrodeNavigationFragmentDelegate<>(this);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        mElectrodeReactFragmentDelegate.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
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

    @Override
    public void onOptionsMenuUpdated(@NonNull Menu menu, @NonNull MenuInflater inflater) {
    }

    /**
     * This will send an event to the current component's `onAppData()` method
     *
     * @param jsonPayload: event payload if any
     */
    public void emitOnAppData(@Nullable JSONObject jsonPayload) {
        mElectrodeReactFragmentDelegate.emitOnAppData(jsonPayload);
    }

}
