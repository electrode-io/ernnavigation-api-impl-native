package com.walmartlabs.moviesreloaded.demo.customfragment;

import androidx.annotation.NonNull;

import com.ern.api.impl.navigation.ElectrodeBaseActivity;
import com.ern.api.impl.navigation.NavigationLaunchConfig;
import com.walmartlabs.moviesreloaded.R;

public class CustomFragmentActivity extends ElectrodeBaseActivity {
    @Override
    protected int mainLayout() {
        return R.layout.activity_default;
    }

    @NonNull
    @Override
    protected String getRootComponentName() {
        return "MoviesReloaded";
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.fragment_container;
    }

    @Override
    protected NavigationLaunchConfig createNavigationLaunchConfig() {
        NavigationLaunchConfig config = super.createNavigationLaunchConfig();
        config.setFragmentClass(CustomFragment.class);
        config.setForceUpEnabled(true);
        return config;
    }
}
