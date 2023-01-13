package com.ern.api.impl.core;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.ern.api.impl.navigation.NavUtils;
import com.ernnavigation.ern.model.ErnNavRoute;
import com.facebook.react.ReactRootView;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;

import java.util.UUID;

public class ElectrodeBaseFragmentDelegate<T extends ElectrodeBaseFragmentDelegate.ElectrodeActivityListener, C extends ElectrodeFragmentConfig> implements LifecycleObserver {
    private static final String TAG = ElectrodeBaseFragmentDelegate.class.getSimpleName();
    public static final String KEY_UNIQUE_VIEW_IDENTIFIER = "viewId";
    protected static final String NAME_NOT_SET_YET = "NAME_NOT_SET_YET";

    protected final Fragment mFragment;

    @VisibleForTesting
    public T electrodeActivityListener;

    @Nullable
    protected final C mFragmentConfig;

    @Nullable
    private View mMiniAppView;

    private String mMiniAppComponentName = NAME_NOT_SET_YET;

    private ViewGroup previousParent;

    @SuppressWarnings("unused")
    public ElectrodeBaseFragmentDelegate(@NonNull Fragment fragment) {
        this(fragment, null);
    }

    /**
     * @param fragment       Hosting fragment
     * @param fragmentConfig Optional config that can be passed if your fragment needs to have a custom layout, etc.
     */
    public ElectrodeBaseFragmentDelegate(@NonNull Fragment fragment, @Nullable C fragmentConfig) {
        mFragment = fragment;
        mFragmentConfig = fragmentConfig;
    }

    @SuppressWarnings("unused")
    public void onAttach(Context context) {
        if (context instanceof ElectrodeBaseFragmentDelegate.ElectrodeActivityListener) {
            //noinspection unchecked
            electrodeActivityListener = (T) context;
        } else {
            Logger.e(TAG, "Activity must implement a ElectrodeActivityListener. If not for tests this should never happen.");
        }
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
    }

    /**
     * Returns a ReactRootView of the passed MiniApp component (component name provided inside arguments under #KEY_MINI_APP_COMPONENT_NAME)
     * <p> Or
     * Returns a View hierarchy if a valid {@link ElectrodeFragmentConfig#mFragmentLayoutId} layout xml resource is passed.
     * Pass a valid {@link ElectrodeFragmentConfig#mReactViewContainerId} for the MiniApp component(provided inside arguments under #KEY_MINI_APP_COMPONENT_NAME) to be inflated properly inside the view hierarchy.
     *
     * @param inflater           The LayoutInflater object that can be used to inflates
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return View
     * <p>
     * Throws {@link IllegalStateException} when either a MiniApp component name is not passed as KEY_MINI_APP_COMPONENT_NAME in arguments or a valid lauout xml is not provided via {@link ElectrodeFragmentConfig#mFragmentLayoutId}
     */
    @SuppressWarnings("unused")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle args = mFragment.getArguments();
        if (args != null) {
            mMiniAppComponentName = args.getString(ActivityDelegateConstants.KEY_MINI_APP_COMPONENT_NAME);
        }

        Logger.d(TAG, "onCreateView() called. MiniApp component name: " + mMiniAppComponentName);

        if (mMiniAppView == null) {
            if (isValidMiniAppName(mMiniAppComponentName)) {
                mMiniAppView = electrodeActivityListener.createReactNativeView(mMiniAppComponentName, initialProps(savedInstanceState != null));
            } else {
                Logger.i(TAG, "Missing miniAppComponentName inside arguments, will not create a MiniApp view.");
            }
        } else if (previousParent != null && mMiniAppView.getParent() == previousParent) {{
            previousParent.removeView(mMiniAppView);
            previousParent = null;
        }}

        View rootView;
        if (mFragmentConfig != null && mFragmentConfig.mFragmentLayoutId != ElectrodeFragmentConfig.NONE) {
            View inflatedView = inflater.inflate(mFragmentConfig.mFragmentLayoutId, container, false);

            setupToolbarIfPresent();

            if (mFragmentConfig.mReactViewContainerId != ElectrodeFragmentConfig.NONE && mMiniAppView != null) {
                View view = inflatedView.findViewById(mFragmentConfig.mReactViewContainerId);
                if (view instanceof ViewGroup) {
                    ((ViewGroup) view).addView(mMiniAppView);
                } else {
                    throw new IllegalStateException("ElectrodeFragmentConfig reactViewContainerId is not a ViewGroup element");
                }
            } else {
                Logger.i(TAG, "Missing reactViewContainerId() or mMiniAppView is null. Will not add MiniApp view explicitly. Do you have a MiniAppView component defined in your layout xml resource file?.");
            }
            Logger.d(TAG, "Returning custom view.");
            rootView = inflatedView;
        } else {
            if (mMiniAppView == null) {
                throw new IllegalStateException("Should never reach here. onCreateView() should return a non-null view. The Fragment neither has a MiniAppView or a custom layout passed via ElectrodeFragmentConfig.");
            }
            Logger.d(TAG, "Returning a ReactRootView.");
            rootView = mMiniAppView;
        }

        return rootView;
    }

    private boolean isValidMiniAppName(String miniAppName) {
        return !TextUtils.isEmpty(miniAppName) && !NAME_NOT_SET_YET.equals(miniAppName);
    }

    private void setupToolbarIfPresent() {
        if (mFragmentConfig != null && mFragmentConfig.mToolbarId != ElectrodeFragmentConfig.NONE) {
            if (mFragment.getView() == null) {
                throw new IllegalStateException("Should never reach here. mRootView should have been populated before calling this method");
            }
            Toolbar toolbar = mFragment.getView().findViewById(mFragmentConfig.mToolbarId);
            if (toolbar != null && mFragment.getActivity() instanceof AppCompatActivity) {
                AppCompatActivity appCompatActivity = (AppCompatActivity) mFragment.getActivity();
                if (appCompatActivity.getSupportActionBar() == null) {
                    appCompatActivity.setSupportActionBar(toolbar);
                } else {
                    Logger.w(TAG, "Hiding fragment layout toolbar. The Activity already has an action bar setup, consider removing the toolbar from your fragment layout.");
                    toolbar.setVisibility(View.GONE);
                }
            } else {
                Logger.w(TAG, "Ignoring toolbar, looks like the activity is not an AppCompatActivity. Make sure you configure thr toolbar in your fragments onCreateView()");
            }
        }
    }

    @NonNull
    private Bundle initialProps(boolean isFragmentBeingReconstructed) {
        final Bundle props = getDefaultProps();

        //NOTE: If/When the system re-constructs a fragment from a previous state a stored Bundle is getting converted to a ParcelableData.
        //When this bundle is send across React native , RN frameworks WritableArray does not support parcelable conversion.
        //To avoid this issue we recreate the ErnNavRoute object from the bundle and regenerate a new bundle which again replaces the  ParcelableData with proper bundle object.
        //Checking for the existence of "path" key since that is the only required property to successfully build an ErnNavRoute object.
        if (isFragmentBeingReconstructed && props.containsKey("path")) {
            props.putAll(new ErnNavRoute(props).toBundle());
        }

        addGlobalProps(props);
        return props;
    }

    public void refresh(@Nullable Bundle data) {
        if (mMiniAppView instanceof ReactRootView) {
            Bundle props = getDefaultProps();
            addGlobalProps(props);
            NavUtils.mergeBundleWithJsonPayloads(props, data);
            ((ReactRootView) mMiniAppView).setAppProperties(props);
        } else {
            Logger.w(TAG, "Refresh called on a null mMiniAppView. Should never reach here");
        }
    }

    @NonNull
    private Bundle getDefaultProps() {
        Bundle props = mFragment.getArguments() == null ? new Bundle() : mFragment.getArguments();
        if (!props.containsKey(KEY_UNIQUE_VIEW_IDENTIFIER)) {
            String id = UUID.randomUUID().toString();
            props.putString(KEY_UNIQUE_VIEW_IDENTIFIER, id);
        }
        return props;
    }

    @Nullable
    public String getMiniAppViewIdentifier() {
        String id = null;
        if (mFragment.getArguments() != null) {
            id = mFragment.getArguments().getString(KEY_UNIQUE_VIEW_IDENTIFIER);
        }
        return id != null ? id : "NOT_SET";
    }

    private void addGlobalProps(@NonNull Bundle props) {
        Bundle globalProps = electrodeActivityListener.globalProps();
        if (globalProps != null) {
            props.putAll(globalProps);
        }
    }

    @SuppressWarnings("unused")
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    }

    @SuppressWarnings("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        Logger.v(TAG, "onStart(): " + getReactComponentName());
    }

    @SuppressWarnings("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        Logger.v(TAG, "onResume(): " + getReactComponentName());
    }

    @SuppressWarnings("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        Logger.v(TAG, "onPause(): " + getReactComponentName());
    }

    @SuppressWarnings("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        Logger.v(TAG, "onStop(): " + getReactComponentName());
    }

    @SuppressWarnings("unused")
    public void onDestroyView() {
        Logger.v(TAG, "onDestroyView(): " + getReactComponentName());
        storeOldParent();
    }

    private void storeOldParent() {
        // If the ReactNative view was inflated inside a container make sure to remove the ReactRootView from the parent
        // since we are reusing the same MiniApp view instance when the view is recreated.
        View rootView = mFragment.getView();
        if (rootView != null && mMiniAppView != null && rootView != mMiniAppView && mFragmentConfig != null) {
            final View parentView = rootView.findViewById(mFragmentConfig.mReactViewContainerId);
            if (parentView instanceof ViewGroup) {
                previousParent = (ViewGroup) parentView;
            }
        }
    }

    @SuppressWarnings("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    @CallSuper
    public void onDestroy() {
        Logger.v(TAG, "onDestroy(): " + getReactComponentName());
        if (mMiniAppView instanceof ReactRootView) {
            electrodeActivityListener.removeReactNativeView(mMiniAppComponentName, (ReactRootView) mMiniAppView);
            mMiniAppView = null;
        }
        if (mFragment.getArguments() != null) {
            mFragment.getArguments().remove(KEY_UNIQUE_VIEW_IDENTIFIER);
        }
        previousParent = null;
    }

    @SuppressWarnings("unused")
    public void onDetach() {
        Logger.v(TAG, "onDetach(): " + getReactComponentName());
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString() + mMiniAppComponentName;
    }

    @SuppressWarnings("unused")
    protected String getReactComponentName() {
        if (mFragment.getArguments() != null && mFragment.getArguments().getString(ActivityDelegateConstants.KEY_MINI_APP_COMPONENT_NAME) != null) {
            return mFragment.getArguments().getString(ActivityDelegateConstants.KEY_MINI_APP_COMPONENT_NAME);
        }
        return "NAME_NOT_SET_YET";
    }

    public interface ElectrodeActivityListener {
        /**
         * Returns a react root view for the given mini app.
         *
         * @param appName React native root component name
         * @param props   Optional properties for the component
         * @return View returns a {@link ReactRootView} for the given component.
         */
        View createReactNativeView(@NonNull String appName, @Nullable Bundle props);

        /**
         * Un-mounts a given React Native view component. Typically done when your fragment is destroyed.
         *
         * @param componentName viewComponentName
         * @param reactRootView {@link ReactRootView} instance
         */
        void removeReactNativeView(@NonNull String componentName, @NonNull ReactRootView reactRootView);

        /**
         * Starts a new fragment and inflate it with the given react component.
         *
         * @param componentName react view component name.
         * @param launchConfig  {@link LaunchConfig} to allow custom launch options for a fragment.
         */
        @SuppressWarnings("unused")
        void startMiniAppFragment(@NonNull String componentName, @NonNull LaunchConfig launchConfig);

        /**
         * Utilize this api to pass in global props that is required by all components involved in a feature.
         *
         * @return Bundle common props required for all the RN components for a specific flow.
         */
        @Nullable
        Bundle globalProps();

        /**
         * Call this to intercept React Native dev menu
         *
         * @param event {@link KeyEvent}
         * @return true if the menu was shown false otherwise
         */
        @SuppressWarnings("unused")
        boolean showDevMenuIfDebug(KeyEvent event);
    }
}
