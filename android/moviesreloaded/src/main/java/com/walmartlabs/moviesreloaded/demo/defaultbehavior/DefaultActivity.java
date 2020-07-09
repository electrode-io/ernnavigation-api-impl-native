package com.walmartlabs.moviesreloaded.demo.defaultbehavior;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ern.api.impl.navigation.ElectrodeBaseActivity;
import com.ern.api.impl.navigation.MiniAppNavigationFragment;
import com.ern.api.impl.navigation.NavigationLaunchConfig;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;
import com.walmartlabs.moviesreloaded.R;

public class DefaultActivity extends ElectrodeBaseActivity {
    private static final String TAG = DefaultActivity.class.getSimpleName();

    @NonNull
    @Override
    public String getRootComponentName() {
        return "MoviesReloaded";
    }

    @Override
    protected int mainLayout() {
        return R.layout.activity_default;
    }

    @Override
    public int getFragmentContainerId() {
        return R.id.fragment_container;
    }

    @NonNull
    @Override
    protected Class<? extends Fragment> miniAppFragmentClass() {
        return MiniAppNavigationFragment.class;
    }

    @Override
    protected NavigationLaunchConfig createNavigationLaunchConfig() {
        NavigationLaunchConfig config = super.createNavigationLaunchConfig();
        config.setForceUpEnabled(true);
        config.useActivityScopeForNavigation(false, (NavigationLaunchConfig.RouteHandlerProvider<MiniAppNavigationFragment>) () -> {
            Logger.v(TAG, "Entering getRouteHandler() implementation");
            Fragment f = getSupportFragmentManager().findFragmentById(getFragmentContainerId());
            if (f instanceof MiniAppNavigationFragment) {
                Logger.v(TAG, "Getting fragment(route handler) hosted inside getFragmentContainer of activity");
                return (MiniAppNavigationFragment) f;
            } else {
                return null;
            }
        });
        return config;
    }
}
