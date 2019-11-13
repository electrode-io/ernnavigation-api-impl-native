package com.ern.api.impl.core;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;

/**
 * Configuration used by {@link ElectrodeBaseFragmentDelegate} to host a React Native view component.
 * This config can be used by a fragment to add custom Layouts, React Native view container view groups, toolbar, etc.
 */
public class ElectrodeFragmentConfig {

    public static final int NONE = 0;

    /**
     * The layout xml that will be used by the fragment to create the view.
     * This is the layout where you can place your toolbar(optional) and an empty view group to inflate the React Native view.
     */
    @LayoutRes
    int mFragmentLayoutId;

    /**
     * The container ViewGroup id to which a React Native view can be added.
     */
    @IdRes
    int mReactViewContainerId;

    /**
     * id of the toolbar if tool bar is part of the fragment layout, return NONE otherwise.
     */
    @IdRes
    int mToolbarId;

    public ElectrodeFragmentConfig() {
        mFragmentLayoutId = NONE;
        mReactViewContainerId = NONE;
        mToolbarId = NONE;
    }

    public int getFragmentLayoutId() {
        return mFragmentLayoutId;
    }

    /**
     * The layout xml that will be used by the fragment to create the view.
     * This is the layout where you can place your toolbar(optional) and an empty view group to inflate the React Native view.
     */
    public void setFragmentLayoutId(int fragmentLayoutId) {
        mFragmentLayoutId = fragmentLayoutId;
    }

    public int getReactViewContainerId() {
        return mReactViewContainerId;
    }

    /**
     * The container ViewGroup id to which a React Native view can be added.
     */
    public void setReactViewContainerId(int reactViewContainerId) {
        mReactViewContainerId = reactViewContainerId;
    }

    public int getToolbarId() {
        return mToolbarId;
    }

    /**
     * Provide the id of the toolbar if tool bar is part of the fragment layout, return NONE otherwise.
     */
    public void setToolbarId(int toolbarId) {
        mToolbarId = toolbarId;
    }
}
