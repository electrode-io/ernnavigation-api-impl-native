package com.ern.api.impl;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ern.api.impl.navigation.ElectrodeBaseActivity;
import com.walmartlabs.ern.container.ElectrodeReactContainer;

import org.json.JSONObject;

import java.util.Objects;

public class SampleActivity extends ElectrodeBaseActivity {
    public static final String ROOT_COMPONENT_NAME = "dummyRootComponent";
    public boolean isBackgrounded;
    public boolean isForegrounded;

    boolean didFinishFlow = false;
    JSONObject finishFlowPayload = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ElectrodeReactContainer.initialize(getApplication(), new ElectrodeReactContainer.Config());
    }

    @Override
    protected void onResume() {
        super.onResume();
        isBackgrounded = false;
        isForegrounded = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isBackgrounded = true;
        isForegrounded = false;
    }

    @Override
    protected int title() {
        return com.walmartlabs.ern.navigation.test.R.string.root_page_title;
    }

    @Override
    protected int mainLayout() {
        return com.walmartlabs.ern.navigation.test.R.layout.activity_sample_main;
    }

    @NonNull
    @Override
    protected String getRootComponentName() {
        return ROOT_COMPONENT_NAME;
    }

    @Override
    protected int getFragmentContainerId() {
        return com.walmartlabs.ern.navigation.test.R.id.sample_fragment_container;
    }

    @Override
    public View createReactNativeView(@NonNull String componentName, @Nullable Bundle props) {
        //Returns a dummy view.
        return ((LayoutInflater) Objects.requireNonNull(getSystemService(Context.LAYOUT_INFLATER_SERVICE))).inflate(com.walmartlabs.ern.navigation.test.R.layout.activity_sample_main, null);
    }

    @Override
    public boolean backToMiniApp(@Nullable String tag, @Nullable Bundle data) {
        //Set back the title when going back to root fragment
        if (ROOT_COMPONENT_NAME.equals(tag)) {
            Objects.requireNonNull(getSupportActionBar()).setTitle(getString(title()));
        }
        return super.backToMiniApp(tag, data);
    }

    @Override
    public void finishFlow(@Nullable JSONObject finalPayload) {
        didFinishFlow = true;
        finishFlowPayload = finalPayload;
    }
}
