package com.ern.api.impl.navigation;

import androidx.annotation.NonNull;

import com.ern.api.impl.core.LaunchConfig;

public class OverlayFragment extends MiniAppNavigationFragment implements ComponentAsOverlay {

    @Override
    public void navigate(@NonNull Route route) {
        mElectrodeReactFragmentDelegate.navigate(route);
    }

    @Override
    public void update(@NonNull Route route) {
        mElectrodeReactFragmentDelegate.update(route);
    }

    @Override
    public void back(@NonNull Route route) {
        mElectrodeReactFragmentDelegate.back(route);
    }

    @Override
    public void finish(@NonNull Route route) {
        mElectrodeReactFragmentDelegate.finish(route);
    }

    @Override
    public void updateNextPageLaunchConfig(@NonNull String nextPageName, @NonNull LaunchConfig defaultLaunchConfig) {
        if (defaultLaunchConfig.isShowAsOverlay()) {
            defaultLaunchConfig.setFragmentClass(OverlayFragment.class);
        }
    }
}
