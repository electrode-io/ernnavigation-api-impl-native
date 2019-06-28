package com.ern.api.impl.core;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * Fragment that hosts a react native view component.
 */
public class DefaultMiniAppFragment extends ElectrodeReactCoreFragment<ElectrodeReactFragmentDelegate> {

    public DefaultMiniAppFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    protected ElectrodeReactFragmentDelegate createFragmentDelegate() {
        return new ElectrodeReactFragmentDelegate(this);
    }

    @Nullable
    @Override
    public Bundle initialProps() {
        return null;
    }
}
