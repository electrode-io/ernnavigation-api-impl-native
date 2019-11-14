package com.ern.api.impl.navigation;

import androidx.fragment.app.Fragment;

import com.ern.api.impl.core.ElectrodeFragmentConfig;

/**
 * Additional fragment configuration needed for {@link ElectrodeNavigationFragmentDelegate}
 */
public class ElectrodeNavigationFragmentConfig extends ElectrodeFragmentConfig {

    /**
     * Flag that indicates the new fragment needs to be added through the childFragmentManager instead of the activity fragment manager.
     * Setting this to true will pass {@link Fragment#getChildFragmentManager()} inside the {@link com.ern.api.impl.core.LaunchConfig} while starting a miniapp.
     */
    public boolean mUseChildFragmentManager = false;

    public ElectrodeNavigationFragmentConfig() {
    }
}
