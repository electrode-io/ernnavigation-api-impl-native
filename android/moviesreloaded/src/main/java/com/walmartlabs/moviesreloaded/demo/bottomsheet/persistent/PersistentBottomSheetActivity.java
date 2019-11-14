package com.walmartlabs.moviesreloaded.demo.bottomsheet.persistent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ern.api.impl.core.LaunchConfig;
import com.ern.api.impl.navigation.ElectrodeBaseActivity;
import com.walmartlabs.moviesreloaded.R;

import org.json.JSONObject;

public class PersistentBottomSheetActivity extends ElectrodeBaseActivity {
    @Override
    protected int mainLayout() {
        return R.layout.activity_default;
    }

    @NonNull
    @Override
    protected String getRootComponentName() {
        return "MoviesReloaded.About";
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.fragment_container;
    }

    @Override
    protected LaunchConfig createDefaultLaunchConfig() {
        LaunchConfig config = super.createDefaultLaunchConfig();
        config.setForceUpEnabled(true);
        config.setFragmentClass(PersistentBottomSheetFragment.class);
        return config;
    }

    @Override
    protected boolean hideNavBar() {
        return true;
    }

    @Override
    public void finishFlow(@Nullable JSONObject finalPayload) {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
