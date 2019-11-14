package com.walmartlabs.moviesreloaded.demo.customview;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ern.api.impl.core.LaunchConfig;
import com.ern.api.impl.navigation.ElectrodeBaseActivity;
import com.ern.api.impl.navigation.views.MiniAppView;
import com.walmartlabs.moviesreloaded.R;

public class CustomActivity extends ElectrodeBaseActivity implements MiniAppView.OnSetInitialPropsListener {
    @Override
    protected int mainLayout() {
        return R.layout.activity_default;
    }

    @NonNull
    @Override
    protected String getRootComponentName() {
        //Pass empty component since the first view is loaded inside the RootFragment layout xml.
        return "";
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.fragment_container;
    }

    @NonNull
    @Override
    protected Class<? extends Fragment> miniAppFragmentClass() {
        return RootFragment.class;
    }

    @Override
    protected LaunchConfig createDefaultLaunchConfig() {
        LaunchConfig config = super.createDefaultLaunchConfig();
        config.setForceUpEnabled(true);
        return config;
    }

    @Nullable
    @Override
    public Bundle getProps(@NonNull String componentName) {
        if ("MoviesReloaded".equals(componentName)) {
            Bundle props = new Bundle();
            props.putString("sampleProp", "test data");
            return props;
        }
        return null;
    }
}
