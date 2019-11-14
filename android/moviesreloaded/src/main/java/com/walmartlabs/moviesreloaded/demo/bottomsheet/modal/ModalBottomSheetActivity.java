package com.walmartlabs.moviesreloaded.demo.bottomsheet.modal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ern.api.impl.core.LaunchConfig;
import com.ern.api.impl.navigation.ElectrodeBaseActivity;

import org.json.JSONObject;

public class ModalBottomSheetActivity extends ElectrodeBaseActivity {
    @Override
    protected int mainLayout() {
        return NONE;
    }

    @NonNull
    @Override
    protected String getRootComponentName() {
        return "MoviesReloaded.About";
    }

    @Override
    protected int getFragmentContainerId() {
        return NONE;
    }

    @Override
    protected LaunchConfig createDefaultLaunchConfig() {
        LaunchConfig config = super.createDefaultLaunchConfig();
        config.setForceUpEnabled(true);
        config.setFragmentClass(ModalDialogFragment.class);
        return config;
    }

    @Override
    protected int title() {
        return DEFAULT_TITLE;
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

    @Override
    protected boolean hideNavBar() {
        return true;
    }
}
