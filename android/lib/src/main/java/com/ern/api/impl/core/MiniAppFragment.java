package com.ern.api.impl.core;

import androidx.annotation.NonNull;

public class MiniAppFragment extends ElectrodeBaseFragment<ElectrodeBaseFragmentDelegate<ElectrodeBaseFragmentDelegate.ElectrodeActivityListener, ElectrodeFragmentConfig>> {
    @NonNull
    @Override
    protected ElectrodeBaseFragmentDelegate<ElectrodeBaseFragmentDelegate.ElectrodeActivityListener, ElectrodeFragmentConfig> createFragmentDelegate() {
        return new ElectrodeBaseFragmentDelegate<>(this);
    }
}
