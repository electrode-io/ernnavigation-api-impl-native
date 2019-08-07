package com.ern.api.impl.navigation;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ern.api.impl.core.ElectrodeReactCoreFragment;

public class MiniAppNavFragment extends ElectrodeReactCoreFragment<ElectrodeReactFragmentNavDelegate> implements ElectrodeReactFragmentNavDelegate.FragmentNavigator {


    public MiniAppNavFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    protected ElectrodeReactFragmentNavDelegate createFragmentDelegate() {
        return new ElectrodeReactFragmentNavDelegate(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        electrodeReactFragmentDelegate.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return electrodeReactFragmentDelegate.onOptionsItemSelected(item);
    }

    @Override
    public boolean navigate(Route route) {
        return false;
    }

    @Nullable
    @Override
    public Bundle initialProps() {
        return null;
    }
}
