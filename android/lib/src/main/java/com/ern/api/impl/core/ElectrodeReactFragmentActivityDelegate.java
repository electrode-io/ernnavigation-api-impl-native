/*
 * Copyright 2019 Walmart Labs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ern.api.impl.core;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.ern.api.impl.core.ElectrodeReactFragmentDelegate.MiniAppRequestListener.AddToBackStackState;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;
import com.walmartlabs.ern.container.ElectrodeReactActivityDelegate;

import static com.ern.api.impl.core.ElectrodeReactFragmentDelegate.MiniAppRequestListener.ADD_TO_BACKSTACK;

/**
 * @deprecated use {@link ElectrodeBaseActivityDelegate}
 */
@Deprecated
public class ElectrodeReactFragmentActivityDelegate extends ElectrodeReactActivityDelegate implements LifecycleObserver {

    private static final String TAG = ElectrodeReactFragmentActivityDelegate.class.getSimpleName();

    protected FragmentActivity mFragmentActivity;
    private DataProvider dataProvider;

    private boolean mUpEnabledForRoot;

    /**
     * Set this to true if you want to enable up navigation for the root component.
     *
     * PS: This method needs to be called before {@link #onCreate(Bundle)} is called.
     */
    public void setUpEnabledForRoot(boolean upEnabledForRoot) {
        mUpEnabledForRoot = upEnabledForRoot;
    }

    public ElectrodeReactFragmentActivityDelegate(@NonNull FragmentActivity activity) {
        super(activity, null);
        if (activity instanceof DataProvider) {
            dataProvider = (DataProvider) activity;

        } else {
            throw new IllegalStateException("The activity should implement the ElectrodeReactFragmentActivityDelegate.DataProvider.");
        }
        this.mFragmentActivity = activity;
        if (mFragmentActivity instanceof BackKeyHandler) {
            setBackKeyHandler((BackKeyHandler) mFragmentActivity);
        }
    }

    //Not putting this under the OnLifecycleEvent sine we need the savedInstanceState
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            startReactNative();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        //PlaceHolder
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    @Override
    public void onResume() {
        super.onResume();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    @Override
    public void onPause() {
        super.onPause();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        //PlaceHolder
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    @Override
    public void onDestroy() {
        mFragmentActivity = null;
        dataProvider = null;
        super.onDestroy();
    }

    /***
     *
     * @param menu
     * @return
     * @deprecated This delegate is no longer needed. remove from your activity.
     */
    @Deprecated
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mFragmentActivity.onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public boolean onBackPressed() {
        Logger.d(TAG, "Handling back press");
        int backStackEntryCount = mFragmentActivity.getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount == 1) {
            Logger.d(TAG, "Last item in the back stack, will finish the activity.");
            mFragmentActivity.finish();
            return true;
        } else {
            return false;
        }
    }

    private void startReactNative() {
        String appName = dataProvider.getRootComponentName();
        Logger.d(TAG, "Starting react native root component. Loading the react view inside a fragment.");
        startMiniAppFragment(appName, dataProvider.getProps());
    }

    public void startMiniAppFragment(@NonNull String componentName, @Nullable Bundle props) {
        StartMiniAppConfig config = new StartMiniAppConfig.Builder().fragmentClass(dataProvider.miniAppFragmentClass()).build();
        startMiniAppFragment(componentName, props, config);
    }

    /**
     * @deprecated Start using {@link #startMiniAppFragment(String, Bundle, StartMiniAppConfig)}
     */
    @Deprecated
    public void startMiniAppFragment(@NonNull Class<? extends Fragment> fragmentClass, @NonNull String componentName, @Nullable Bundle props) {
        StartMiniAppConfig config = new StartMiniAppConfig.Builder().fragmentClass(fragmentClass).build();
        startMiniAppFragment(componentName, props, config);
    }

    public void startMiniAppFragment(@NonNull String componentName, @Nullable Bundle props, @NonNull StartMiniAppConfig startMiniAppConfig) {
        if (props == null) {
            props = new Bundle();
        }
        props.putString(ActivityDelegateConstants.KEY_MINI_APP_COMPONENT_NAME, componentName);

        if (startMiniAppConfig.fragmentClass == null) {
            throw new IllegalStateException("Should never reach here. At this point startMiniAppConfig should have a fragment class defined.");
        }

        Logger.d(TAG, "startMiniAppFragment: fragmentClass->%s, componentName->%s, props->%s", startMiniAppConfig.fragmentClass.getSimpleName(), componentName, props);

        switchToFragment(props, ADD_TO_BACKSTACK, startMiniAppConfig);
    }

    private void switchToFragment(@NonNull Bundle bundle,
                                  @AddToBackStackState int addToBackStackState, @NonNull StartMiniAppConfig startMiniAppConfig) {
        try {
            Fragment fragment = startMiniAppConfig.fragmentClass.newInstance();

            String tag = (bundle.containsKey(ActivityDelegateConstants.KEY_MINI_APP_FRAGMENT_TAG)) ? bundle.getString(ActivityDelegateConstants.KEY_MINI_APP_FRAGMENT_TAG) : bundle.getString(ActivityDelegateConstants.KEY_MINI_APP_COMPONENT_NAME);
            Logger.d(TAG, "Switching to a new fragment, tag: %s ", tag);

            final FragmentManager fragmentManager = startMiniAppConfig.fragmentManager != null ? startMiniAppConfig.fragmentManager : mFragmentActivity.getSupportFragmentManager();
            final FragmentTransaction transaction = fragmentManager.beginTransaction();

            if (ADD_TO_BACKSTACK == addToBackStackState) {
                transaction.addToBackStack(tag);
            }
            bundle.putBoolean(ActivityDelegateConstants.KEY_MINI_APP_FRAGMENT_SHOW_UP_ENABLED, shouldShowUpEnabled());
            fragment.setArguments(bundle);
            int fragmentContainerId = (startMiniAppConfig.fragmentContainerId != 0) ? startMiniAppConfig.fragmentContainerId : dataProvider.getFragmentContainerId();
            transaction.replace(fragmentContainerId, fragment, tag);
            transaction.commit();
        } catch (Exception e) {
            Logger.e(TAG, "Failed to create " + startMiniAppConfig.fragmentClass.getName() + " fragment", e);
        }
    }

    private boolean shouldShowUpEnabled() {
        int backStackCount = mFragmentActivity.getSupportFragmentManager().getBackStackEntryCount();
        return backStackCount > 0 || (backStackCount == 0 && mUpEnabledForRoot);
    }

    public boolean switchBackToFragment(@Nullable String tag) {
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

        return manager.popBackStackImmediate(tag, 0);
    }

    public interface DataProvider {

        /**
         * React native component name that will be rendered when the activity is first launched.
         *
         * @return String
         */
        @NonNull
        String getRootComponentName();


        /**
         * Id for the fragment container.
         *
         * @return IdRes of the fragment holder in your layout xml.
         */
        @IdRes
        int getFragmentContainerId();

        /**
         * Props that needs to be passed to the root component.
         *
         * @return Bundle
         */
        @Nullable
        Bundle getProps();


        /***
         * Return the fragment class that needs to be instantiated to render react native component.
         *
         * Note: Default available fragments: {@link DefaultMiniAppFragment}
         * @return Class
         */
        @NonNull
        Class<? extends Fragment> miniAppFragmentClass();
    }

    /**
     * Class that defines the custom configurations that can be passed while starting a new MiniApp fragment.
     */
    public static class StartMiniAppConfig {
        @Nullable
        final FragmentManager fragmentManager;
        @Nullable
        final Class<? extends Fragment> fragmentClass;

        /**
         * Id for the fragment container.
         *
         * @return IdRes of the fragment holder in your layout xml.
         */
        @IdRes
        final int fragmentContainerId;

        private StartMiniAppConfig(Builder builder) {
            fragmentManager = builder.fragmentManager;
            fragmentClass = builder.fragmentClass;
            fragmentContainerId = builder.fragmentContainerId;
        }

        public static class Builder {
            FragmentManager fragmentManager;
            Class<? extends Fragment> fragmentClass;
            @IdRes
            int fragmentContainerId = 0;

            public Builder fragmentManager(@Nullable FragmentManager fragmentManager) {
                this.fragmentManager = fragmentManager;
                return this;
            }

            public Builder fragmentClass(@NonNull Class<? extends Fragment> fragmentClass) {
                this.fragmentClass = fragmentClass;
                return this;
            }

            public Builder fragmentContainerId(@IdRes int fragmentContainerId) {
                this.fragmentContainerId = fragmentContainerId;
                return this;
            }

            public StartMiniAppConfig build() {
                return new StartMiniAppConfig(this);
            }
        }
    }
}
