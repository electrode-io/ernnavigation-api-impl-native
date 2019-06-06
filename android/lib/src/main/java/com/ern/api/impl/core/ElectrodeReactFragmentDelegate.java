package com.ern.api.impl.core;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.facebook.react.ReactRootView;
import com.walmartlabs.ern.navigation.BuildConfig;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;

public class ElectrodeReactFragmentDelegate<T extends ElectrodeReactFragmentDelegate.MiniAppRequestListener> {
    private static final String TAG = ElectrodeReactFragmentDelegate.class.getSimpleName();

    protected final Fragment mFragment;
    protected T mMiniAppRequestListener;
    private ReactRootView miniAppView;
    private DataProvider mDataProvider;

    protected ElectrodeReactFragmentDelegate(@NonNull Fragment fragment) {
        mFragment = fragment;
        if (mFragment instanceof ElectrodeReactFragmentDelegate.DataProvider) {
            mDataProvider = (DataProvider) mFragment;
        } else {
            throw new IllegalStateException("Fragment should implement ElectrodeReactFragmentDelegate.DataProvider.");
        }
    }

    @SuppressWarnings("WeakerAccess")
    public void onAttach(Context context) {
        if (context instanceof MiniAppRequestListener) {
            //noinspection unchecked
            mMiniAppRequestListener = (T) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement a MiniAppRequestListener");
        }
    }

    @SuppressWarnings("WeakerAccess")
    public void onCreate(@Nullable Bundle savedInstanceState) {
        //PlaceHolder
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mFragment.getArguments() == null) {
            throw new IllegalStateException("Looks like the the fragment arguments are not set. \"miniAppComponentName\" is a required property");
        }

        String miniAppComponentName = mFragment.getArguments().getString(ActivityDelegateConstants.KEY_MINI_APP_COMPONENT_NAME);
        if (TextUtils.isEmpty(miniAppComponentName)) {
            throw new IllegalStateException("Missing key \"miniAppComponentName\" in args");
        }

        if (BuildConfig.DEBUG)
            Log.d(TAG, "delegate.onCreateView() called. Component name: " + miniAppComponentName);
        assert miniAppComponentName != null;
        miniAppView = (ReactRootView) mMiniAppRequestListener.createReactNativeView(miniAppComponentName, initialProps());
        return miniAppView;
    }

    @NonNull
    private Bundle initialProps() {
        final Bundle initialProps = mFragment.getArguments() == null ? new Bundle() : mFragment.getArguments();

        Bundle props = mDataProvider.initialProps();
        if (props != null) {
            initialProps.putAll(props);
        }

        props = mMiniAppRequestListener.globalProps();
        if (props != null) {
            initialProps.putAll(props);
        }

        return initialProps;
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        //PlaceHolder
    }

    @SuppressWarnings("WeakerAccess")
    public void onStart() {
        //PlaceHolder
    }

    public void onResume() {
        //PlaceHolder
    }

    public void onPause() {
        //PlaceHolder
    }

    public void onStop() {
        //PlaceHolder
    }

    @SuppressWarnings("WeakerAccess")
    public void onDestroyView() {
        if (miniAppView != null) {
            assert mFragment.getArguments() != null;
            mMiniAppRequestListener.removeReactNativeView(Objects.requireNonNull(mFragment.getArguments().getString(ActivityDelegateConstants.KEY_MINI_APP_COMPONENT_NAME)));
            miniAppView = null;
        }
    }

    public void onDestroy() {
        //PlaceHolder
    }

    @SuppressWarnings("WeakerAccess")
    public void onDetach() {
        mMiniAppRequestListener = null;
    }

    protected String getReactComponentName() {
        if (mFragment.getArguments() != null && mFragment.getArguments().getString(ActivityDelegateConstants.KEY_MINI_APP_COMPONENT_NAME) != null) {
            return mFragment.getArguments().getString(ActivityDelegateConstants.KEY_MINI_APP_COMPONENT_NAME);
        }
        return "NAME_NOT_SET_YET";
    }

    /**
     * This needs to be implemented by the fragment. The APIs in this interface should ask for data that needs to be provided by the fragment.
     */
    public interface DataProvider {
        /**
         * Initial properties needed for rendering the react component
         *
         * @return Bundle
         */
        @Nullable
        Bundle initialProps();
    }

    /***
     * Interface that connects the fragment delegate to the hosting activity.
     */
    public interface MiniAppRequestListener {

        int ADD_TO_BACKSTACK = 0;
        int DO_NOT_ADD_TO_BACKSTACK = 1;

        @IntDef({ADD_TO_BACKSTACK, DO_NOT_ADD_TO_BACKSTACK})
        @Retention(RetentionPolicy.SOURCE)
        @interface AddToBackStackState {
        }

        /**
         * Returns a react root view for the given mini app.
         *
         * @param appName React native root component name
         * @param props   Optional properties for the component
         * @return
         */
        View createReactNativeView(@NonNull String appName, @Nullable Bundle props);

        /**
         * Un mounts a given react native view component. Typically done when your fragment is destroyed.
         *
         * @param appName React native root component name
         */
        void removeReactNativeView(@NonNull String appName);

        /**
         * starts a new fragment and inflate it with the given react component.
         *
         * @param componentName react view component name.
         * @param props         optional properties for the component rendering.
         */
        void startMiniAppFragment(@NonNull String componentName, @Nullable Bundle props);

        /**
         * starts a new fragment and inflate it with the given react component.
         *
         * @param componentName react view component name.
         * @param props         optional properties for the component rendering.
         */
        void startMiniAppFragment(@NonNull Class<? extends Fragment> fragmentClass, @NonNull String componentName, @Nullable Bundle props);

        /**
         * Utilize this api to pass in global props that is required by all components involved in a feature.
         *
         * @return Bundle common props required for all the RN components for a specific flow.
         */
        @Nullable
        Bundle globalProps();

        /**
         * Cal this to intercept react-native dev menu
         *
         * @param event
         * @return true if the menu was shown false otherwise
         */
        boolean showDevMenuIfDebug(KeyEvent event);
    }
}