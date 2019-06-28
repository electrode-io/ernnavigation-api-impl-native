package com.ern.api.impl.core;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Fragment that is used by the electrode navigation api native implementation.
 * <p>
 * This fragment when instantiated listens for navigation requests initiated through the navigation api.
 */
public abstract class ElectrodeReactCoreFragment<T extends ElectrodeReactFragmentDelegate> extends Fragment implements ElectrodeReactFragmentDelegate.DataProvider {
    private static final String TAG = ElectrodeReactCoreFragment.class.getSimpleName();

    private T electrodeReactFragmentDelegate;

    @NonNull
    protected abstract T createFragmentDelegate();

    public ElectrodeReactCoreFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        electrodeReactFragmentDelegate = createFragmentDelegate();
        electrodeReactFragmentDelegate.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLifecycle().addObserver(electrodeReactFragmentDelegate);
        electrodeReactFragmentDelegate.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return electrodeReactFragmentDelegate.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        electrodeReactFragmentDelegate.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDetach() {
        electrodeReactFragmentDelegate.onDetach();
        electrodeReactFragmentDelegate = null;
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        electrodeReactFragmentDelegate.onDestroyView();
        super.onDestroyView();
    }
}
