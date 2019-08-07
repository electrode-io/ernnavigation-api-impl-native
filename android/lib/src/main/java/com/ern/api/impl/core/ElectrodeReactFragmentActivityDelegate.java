package com.ern.api.impl.core;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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
import com.walmartlabs.ern.container.ElectrodeReactContainer;

import static com.ern.api.impl.core.ElectrodeReactFragmentDelegate.MiniAppRequestListener.ADD_TO_BACKSTACK;


public class ElectrodeReactFragmentActivityDelegate extends ElectrodeReactActivityDelegate implements LifecycleObserver {

    private static final String TAG = ElectrodeReactFragmentActivityDelegate.class.getSimpleName();

    protected FragmentActivity mFragmentActivity;
    private DataProvider dataProvider;

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
        startMiniAppFragment(dataProvider.miniAppFragmentClass(), componentName, props);
    }

    public void startMiniAppFragment(@NonNull Class<? extends Fragment> fragmentClass, @NonNull String componentName, @Nullable Bundle props) {
        if (props == null) {
            props = new Bundle();
        }
        props.putString(ActivityDelegateConstants.KEY_MINI_APP_COMPONENT_NAME, componentName);
        Logger.d(TAG, "startMiniAppFragment: fragmentClass->%s, componentName->%s, props->%s", fragmentClass.getSimpleName(), componentName, props);
        switchToFragment(fragmentClass, props);
    }

    private void switchToFragment(@NonNull Class<?> fragmentClass, @NonNull Bundle bundle) {
        switchToFragment(fragmentClass, bundle, ADD_TO_BACKSTACK);
    }

    private void switchToFragment(@NonNull Class<?> fragmentClass, @NonNull Bundle bundle,
                                  @AddToBackStackState int addToBackStackState) {
        try {
            Fragment fragment = (Fragment) fragmentClass.newInstance();

            String tag = (bundle.containsKey(ActivityDelegateConstants.KEY_MINI_APP_FRAGMENT_TAG)) ? bundle.getString(ActivityDelegateConstants.KEY_MINI_APP_FRAGMENT_TAG) : bundle.getString(ActivityDelegateConstants.KEY_MINI_APP_COMPONENT_NAME);
            Logger.d(TAG, "Switching to a new fragment, tag: %s ", tag);

            final FragmentTransaction transaction = mFragmentActivity.getSupportFragmentManager().beginTransaction();
            if (ADD_TO_BACKSTACK == addToBackStackState) {
                transaction.addToBackStack(tag);
            }
            bundle.putBoolean(ActivityDelegateConstants.KEY_MINI_APP_FRAGMENT_SHOW_UP_ENABLED, mFragmentActivity.getSupportFragmentManager().getBackStackEntryCount() > 0);
            fragment.setArguments(bundle);
            transaction.replace(dataProvider.getFragmentContainerId(), fragment, tag);
            transaction.commit();
        } catch (Exception e) {
            Logger.e(TAG, "Failed to create " + fragmentClass.getName() + " fragment", e);
        }
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

        boolean result = manager.popBackStackImmediate(tag, 0);
        return result;
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
}
