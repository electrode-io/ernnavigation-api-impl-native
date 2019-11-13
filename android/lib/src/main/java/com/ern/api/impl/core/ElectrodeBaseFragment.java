package com.ern.api.impl.core;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class ElectrodeBaseFragment<T extends ElectrodeBaseFragmentDelegate> extends Fragment {
    protected T mElectrodeReactFragmentDelegate;

    @NonNull
    protected abstract T createFragmentDelegate();

    public ElectrodeBaseFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mElectrodeReactFragmentDelegate = createFragmentDelegate();
        mElectrodeReactFragmentDelegate.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLifecycle().addObserver(mElectrodeReactFragmentDelegate);
        mElectrodeReactFragmentDelegate.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return mElectrodeReactFragmentDelegate.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mElectrodeReactFragmentDelegate.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDetach() {
        mElectrodeReactFragmentDelegate.onDetach();
        mElectrodeReactFragmentDelegate = null;
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        mElectrodeReactFragmentDelegate.onDestroyView();
        super.onDestroyView();
    }
}
