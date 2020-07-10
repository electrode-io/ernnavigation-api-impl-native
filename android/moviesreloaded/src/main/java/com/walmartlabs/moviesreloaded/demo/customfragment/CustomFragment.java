package com.walmartlabs.moviesreloaded.demo.customfragment;

import androidx.annotation.NonNull;

import com.ern.api.impl.navigation.ElectrodeNavigationFragmentConfig;
import com.ern.api.impl.navigation.ElectrodeNavigationFragmentDelegate;
import com.ern.api.impl.navigation.MiniAppNavigationFragment;
import com.walmartlabs.moviesreloaded.R;

public class CustomFragment extends MiniAppNavigationFragment {

    @NonNull
    @Override
    protected ElectrodeNavigationFragmentDelegate createFragmentDelegate() {
        ElectrodeNavigationFragmentConfig config = new ElectrodeNavigationFragmentConfig();
        config.setFragmentLayoutId(R.layout.fragment_custom);
        config.setReactViewContainerId(R.id.child_fragment_holder);
        return new ElectrodeNavigationFragmentDelegate(this, config);
    }
}
