package com.walmartlabs.moviesreloaded.demo.customview;

import androidx.annotation.NonNull;

import com.ern.api.impl.core.LaunchConfig;
import com.ern.api.impl.navigation.ElectrodeNavigationFragmentConfig;
import com.ern.api.impl.navigation.ElectrodeNavigationFragmentDelegate;
import com.ern.api.impl.navigation.MiniAppNavigationFragment;
import com.walmartlabs.moviesreloaded.R;

public class RootFragment extends MiniAppNavigationFragment {
    @NonNull
    @Override
    protected ElectrodeNavigationFragmentDelegate createFragmentDelegate() {
        return new ElectrodeNavigationFragmentDelegate(this, createSelfLaunchConfig());
    }

    private ElectrodeNavigationFragmentConfig createSelfLaunchConfig() {
        ElectrodeNavigationFragmentConfig fragmentConfig = new ElectrodeNavigationFragmentConfig();
        fragmentConfig.setFragmentLayoutId(R.layout.fragment_root);
        return fragmentConfig;
    }

    @Override
    public void updateNextPageLaunchConfig(@NonNull String nextPageName, @NonNull LaunchConfig defaultLaunchConfig) {
        //From root view every navigation request should be hosted inside a ChildFragment
        defaultLaunchConfig.setFragmentClass(ChildFragment.class);
    }
}
