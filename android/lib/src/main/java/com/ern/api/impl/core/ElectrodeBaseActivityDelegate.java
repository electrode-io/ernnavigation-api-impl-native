package com.ern.api.impl.core;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.ern.api.impl.util.AnimUtil;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;
import com.walmartlabs.ern.container.ElectrodeReactActivityDelegate;

import java.util.List;

import static com.ern.api.impl.core.ActivityDelegateConstants.KEY_REGISTER_NAV_VIEW_MODEL;
import static com.ern.api.impl.core.ElectrodeReactFragmentDelegate.MiniAppRequestListener.ADD_TO_BACKSTACK;
import static com.ern.api.impl.core.LaunchConfig.NONE;

public class ElectrodeBaseActivityDelegate<T extends LaunchConfig> extends ElectrodeReactActivityDelegate implements LifecycleObserver {
    private static final String TAG = ElectrodeBaseActivityDelegate.class.getSimpleName();

    @SuppressWarnings("WeakerAccess")
    protected FragmentActivity mFragmentActivity;
    protected final T mDefaultLaunchConfig;
    private final String mRootComponentName;

    /**
     * @param activity            Hosting activity
     * @param rootComponentName   First react native component to be launched.
     * @param defaultLaunchConfig : {@link LaunchConfig} that acts as the the initial configuration to load the rootComponent as well as the default launch config for subsequent navigation flows.
     *                            This configuration will also be used as a default configuration when the root component tries to navigate to a new pages if a proper launch config is passed inside {@link #startMiniAppFragment(String, LaunchConfig)}.
     */
    public ElectrodeBaseActivityDelegate(@NonNull FragmentActivity activity, @Nullable String rootComponentName, @NonNull T defaultLaunchConfig) {
        super(activity, null);

        mFragmentActivity = activity;
        mRootComponentName = rootComponentName;
        mDefaultLaunchConfig = defaultLaunchConfig;
        if (mFragmentActivity instanceof BackKeyHandler) {
            setBackKeyHandler((BackKeyHandler) mFragmentActivity);
        }
    }

    //Not putting this under the OnLifecycleEvent since we need the savedInstanceState
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null && mDefaultLaunchConfig.mStartRootInOnCreate) {
            launchRootComponent();
        }
    }

    /**
     * Replaces the current fragment with the root component.
     */
    public void launchRootComponent() {
        Logger.d(TAG, "Starting react native root component(%s). Loading the react view inside a fragment.", mRootComponentName);
        startMiniAppFragment(mRootComponentName, mDefaultLaunchConfig);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        Logger.v(TAG, "onStart(): " + mRootComponentName);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    @Override
    public void onResume() {
        if (getReactNativeHost() != null) {
            super.onResume();
        }
        Logger.v(TAG, "onResume()");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    @Override
    public void onPause() {
        if (getReactNativeHost() != null) {
            super.onPause();
        }
        Logger.v(TAG, "onPause()");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        Logger.v(TAG, "onStop()");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    @Override
    public void onDestroy() {
        mFragmentActivity = null;
        if (getReactNativeHost() != null) {
            super.onDestroy();
        }
        Logger.v(TAG, "onDestroy()");
    }

    @SuppressWarnings("unused")
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            return onBackPressed();
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (getReactNativeHost() != null) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onBackPressed() {
        if (mDefaultLaunchConfig.isRootBackPressHandledByRN() && mFragmentActivity.getOnBackPressedDispatcher().hasEnabledCallbacks()) {
            return false;
        }

        int backStackEntryCount = mFragmentActivity.getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount == 1) {
            Logger.d(TAG, "Last item in the back stack, will finish the activity.");
            mFragmentActivity.finish();
            return true;
        } else {
            return false;
        }
    }

    public void startMiniAppFragment(@NonNull String componentName, @NonNull LaunchConfig launchConfig) {
        Logger.d(TAG, "entering startMiniAppFragment for component: %s", componentName);
        Fragment fragment;
        Class<? extends Fragment> fClazz = launchConfig.mFragmentClass != null ? launchConfig.mFragmentClass : mDefaultLaunchConfig.mFragmentClass;
        try {
            if (fClazz == null) {
                throw new RuntimeException("Missing fragment class in both launchConfig and defaultLaunchConfig. This needs to be set in one of these configurations.");
            }
            fragment = fClazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create " + fClazz + " fragment: " + e.getMessage(), e);
        }

        Bundle props = launchConfig.mInitialProps != null ? launchConfig.mInitialProps : new Bundle();
        props.putString(ActivityDelegateConstants.KEY_MINI_APP_COMPONENT_NAME, componentName);
        props.putBoolean(ActivityDelegateConstants.KEY_MINI_APP_FRAGMENT_SHOW_UP_ENABLED, shouldShowUpEnabled(launchConfig.mForceUpEnabled, launchConfig.mReplace));
        if (!launchConfig.mForceUpEnabled) {
            props.putBoolean(ActivityDelegateConstants.KEY_MINI_APP_FRAGMENT_HIDE_UP_INDICATOR, launchConfig.mHideUpIndicatorIcon);
        }
        fragment.setArguments(props);

        Logger.d(TAG, "starting fragment: fragmentClass->%s, props->%s", fragment.getClass().getSimpleName(), props);
        switchToFragment(fragment, launchConfig, componentName);
    }

    protected boolean fragmentScopedNavModel() {
        return true;
    }

    private void switchToFragment(@NonNull Fragment fragment, @NonNull LaunchConfig launchConfig, @Nullable String tag) {
        if (fragment instanceof DialogFragment) {
            Logger.d(TAG, "Showing dialog fragment");
            ((DialogFragment) fragment).show(getFragmentManager(launchConfig), tag);
        } else {
            final FragmentManager fragmentManager = getFragmentManager(launchConfig);
            int fragmentContainerId = (launchConfig.mFragmentContainerId != NONE) ? launchConfig.mFragmentContainerId : mDefaultLaunchConfig.mFragmentContainerId;

            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            manageTransition(transaction);

            if (ADD_TO_BACKSTACK == launchConfig.mAddToBackStack) {
                Logger.d(TAG, "fragment(%s) added to back stack", tag);
                transaction.addToBackStack(tag);
            }

            if (fragmentContainerId != NONE) {
                if (launchConfig.mShowAsOverlay) {
                    Logger.d(TAG, "performing ADD fragment inside fragment container");
                    transaction.add(fragmentContainerId, fragment, tag);
                } else {
                    Logger.d(TAG, "performing REPLACE fragment inside fragment container");
                    if (fragment.getArguments() != null) {
                        fragment.getArguments().putBoolean(KEY_REGISTER_NAV_VIEW_MODEL, fragmentScopedNavModel());
                    }
                    transaction.replace(fragmentContainerId, fragment, tag);
                }
            } else {
                throw new RuntimeException("Missing fragmentContainerId to add the " + fragment.getClass().getSimpleName() + ". Should never reach here.");
            }
            if (launchConfig.mReplace && fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStackImmediate();
            }
            transaction.commit();
            Logger.d(TAG, "startMiniAppFragment completed successfully.");
        }
    }

    /**
     * This is to help hosting activities in varying applications to customize the fragment transition animations.
     * To customize, create your own delegate class that extends {@link ElectrodeBaseActivityDelegate} and override this method.
     * Make sure to pass the custom delegate instance by overriding {@link ElectrodeBaseActivity#createElectrodeDelegate()} method.
     *
     * @param transaction {@link FragmentTransaction} used for the current fragment transaction
     */
    protected void manageTransition(@NonNull FragmentTransaction transaction) {
        if (mDefaultLaunchConfig.navigationTransition == LaunchConfig.TRANSITION.FADE) {
            AnimUtil.fade(transaction);
        } else if (mDefaultLaunchConfig.navigationTransition == LaunchConfig.TRANSITION.SLIDE) {
            AnimUtil.slide(transaction);
        }
    }

    private FragmentManager getFragmentManager(@NonNull LaunchConfig launchConfig) {
        if (launchConfig.mFragmentManager != null) {
            return launchConfig.mFragmentManager;
        }

        if (mDefaultLaunchConfig.mFragmentManager != null) {
            return mDefaultLaunchConfig.mFragmentManager;
        }

        return mFragmentActivity.getSupportFragmentManager();
    }

    private boolean shouldShowUpEnabled(boolean forceUpEnabled, boolean isReplace) {
        int backStackCount = mFragmentActivity.getSupportFragmentManager().getBackStackEntryCount();
        final int MIN_COUNT = isReplace ? 1 : 0;
        return forceUpEnabled || backStackCount > MIN_COUNT;
    }

    @SuppressWarnings("unused")
    public boolean switchBackToFragment(@Nullable String tag, @Nullable Bundle data) {
        Logger.d(TAG, "switchBackToFragment, tag:  %s", tag);
        final FragmentManager manager = mFragmentActivity.getSupportFragmentManager();
        int backStackCount = manager.getBackStackEntryCount();
        if (backStackCount == 1) {
            if (tag == null || tag.equals(manager.getBackStackEntryAt(0).getName())) {
                Logger.d(TAG, "Last fragment in the stack, will finish the activity.");
                mFragmentActivity.finish();
                return true;
            }
        }
        boolean result = true;
        try {
            result = manager.popBackStackImmediate(tag, 0);
        } catch (IllegalStateException e) {
            // java.lang.IllegalStateException: FragmentManager is already executing transactions.
            // This error occurs when a navigation live data event gets triggered before the current fragment comes into Resumed state.
            // For this use case we need to perform popBackStack asynchronously.
            if (tag != null) {
                result = manager.findFragmentByTag(tag) != null;
            }
            //If there is no tag provided assume that the user is trying to go back to the previous screen
            if (result) {
                manager.popBackStack(tag, 0);
            }
        }
        if (result && data != null && data.getBoolean("refresh", true)) {
            List<Fragment> fragments = mFragmentActivity.getSupportFragmentManager().getFragments();
            if (fragments.size() > 0) {
                Fragment currentFragment = fragments.get(fragments.size() - 1);
                if (currentFragment instanceof UpdatePropsListener) {
                    ((UpdatePropsListener) currentFragment).refresh(data);
                }
            }
        }
        return result;
    }
}
