package com.walmartlabs.moviesreloaded.demo.bottomsheet.modal;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ern.api.impl.core.ElectrodeBaseFragment;
import com.ern.api.impl.navigation.ElectrodeNavigationActivityListener;
import com.ern.api.impl.navigation.ElectrodeNavigationFragmentConfig;
import com.ern.api.impl.navigation.ElectrodeNavigationFragmentDelegate;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.walmartlabs.moviesreloaded.R;

public class ModalDialogFragment extends BottomSheetDialogFragment {
    private static final String TAG = ElectrodeBaseFragment.class.getSimpleName();

    @SuppressWarnings("WeakerAccess")
    protected ElectrodeNavigationFragmentDelegate mElectrodeReactFragmentDelegate;

    public ModalDialogFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ElectrodeNavigationFragmentConfig config = new ElectrodeNavigationFragmentConfig();
        config.setFragmentLayoutId(R.layout.fragment_bottom_sheet_dialog);
        config.setReactViewContainerId(R.id.react_view_container);

        mElectrodeReactFragmentDelegate = new ElectrodeNavigationFragmentDelegate(this, config);
        mElectrodeReactFragmentDelegate.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLifecycle().addObserver(mElectrodeReactFragmentDelegate);
        mElectrodeReactFragmentDelegate.onCreate(savedInstanceState);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mElectrodeReactFragmentDelegate.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mElectrodeReactFragmentDelegate.onOptionsItemSelected(item);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (getActivity() instanceof ElectrodeNavigationActivityListener) {
            ((ElectrodeNavigationActivityListener) getActivity()).finishFlow(null);
        }
    }
}
