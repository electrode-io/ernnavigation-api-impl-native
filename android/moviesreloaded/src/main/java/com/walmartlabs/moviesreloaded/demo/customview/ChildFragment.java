package com.walmartlabs.moviesreloaded.demo.customview;

import androidx.annotation.NonNull;

import com.ern.api.impl.core.LaunchConfig;
import com.ern.api.impl.navigation.MiniAppNavigationFragment;

public class ChildFragment extends MiniAppNavigationFragment {
    @Override
    public void updateNextPageLaunchConfig(@NonNull String nextPageName, @NonNull LaunchConfig defaultLaunchConfig) {
        defaultLaunchConfig.setFragmentClass(ChildFragment.class);
    }
}
