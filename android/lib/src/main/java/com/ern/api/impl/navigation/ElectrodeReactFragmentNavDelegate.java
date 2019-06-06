package com.ern.api.impl.navigation;


import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.ern.api.impl.core.ElectrodeReactFragmentDelegate;
import com.ernnavigationApi.ern.model.NavigationBar;
import com.walmartlabs.ern.navigation.BuildConfig;

public class ElectrodeReactFragmentNavDelegate extends ElectrodeReactFragmentDelegate<MiniAppNavRequestListener> {

    private static final String TAG = ElectrodeReactFragmentNavDelegate.class.getSimpleName();

    private ReactNavigationViewModel mNavViewModel;
    private FragmentNavigator mFragmentNavigator;

    private final Observer<Route> routeObserver = new Observer<Route>() {
        @Override
        public void onChanged(@Nullable Route route) {
            if (route != null) {
                if (BuildConfig.DEBUG)
                    Log.d(TAG, "Received a new navigation route: " + route.getArguments());

                if (!route.getArguments().containsKey(ReactNavigationViewModel.KEY_NAV_TYPE)) {
                    throw new IllegalStateException("Missing NAV_TYPE in route arguments");
                }

                //NOTE: We can't put KEY_NAV_TYPE as a parcelable since ReactNative side is not looking for Parcelable deserialization yet.
                ReactNavigationViewModel.Type navType = ReactNavigationViewModel.Type.valueOf(route.getArguments().getString(ReactNavigationViewModel.KEY_NAV_TYPE));
                switch (navType) {
                    case NAVIGATE:
                        navigate(route);
                        break;
                    case UPDATE:
                        update(route);
                        break;
                    case BACK:
                        back(route);
                        break;
                    case FINISH:
                        finish(route);
                        break;
                }
                if (!route.isCompleted()) {
                    throw new IllegalStateException("Should never reach here. A result should be set for the route at this point. Make sure a setResult is called on the route object after the appropriate action is taken on a nav type.");
                }
            }
        }
    };

    @SuppressWarnings("WeakerAccess")
    public ElectrodeReactFragmentNavDelegate(@NonNull Fragment fragment) {
        super(fragment);
        if (mFragment instanceof FragmentNavigator) {
            mFragmentNavigator = (FragmentNavigator) mFragment;
        } else {
            throw new RuntimeException(mFragment
                    + " must implement ElectrodeReactFragmentNavDelegate.FragmentNavigator");
        }
    }

    @Override
    public void onAttach(Context context) {
        if (!(context instanceof MiniAppNavRequestListener)) {
            //noinspection unchecked
            throw new RuntimeException(context.toString()
                    + " must implement a MiniAppNavRequestListener");
        }
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        updateNavBar(mFragment.getArguments());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mNavViewModel = ViewModelProviders.of(mFragment).get(ReactNavigationViewModel.class);
        mNavViewModel.getRouteLiveData().observe(mFragment, routeObserver);
    }

    public void onResume() {
        super.onResume();
        mNavViewModel.registerNavRequestHandler();
    }

    public void onPause() {
        super.onPause();
        mNavViewModel.unRegisterNavRequestHandler();
    }

    private void back(Route route) {
        //Manage fragment back-stack popping. If the given route.path is not in the stack pop a new fragment.
        boolean result = mMiniAppRequestListener.backToMiniApp(route.getArguments().getString("path"));
        route.setResult(result, !result ? "back navigation failed. component not found in the back stack" : null);
    }

    private void update(Route route) {
        boolean result = updateNavBar(route.getArguments());
        route.setResult(result, !result ? "failed to update nav bar." : null);
    }

    private void finish(@Nullable Route route) {
        if (BuildConfig.DEBUG)
            Log.d(TAG, "finish triggered by RN. Hosting activity will be notified.");
        mMiniAppRequestListener.finishFlow(NavUtils.getPayload(route.getArguments()));
        route.setResult(true, null);
    }

    private void navigate(@NonNull Route route) {
        final String path = NavUtils.getPath(route.getArguments());
        if (BuildConfig.DEBUG) Log.d(TAG, "navigating to: " + path);

        if (!TextUtils.isEmpty(path)) {
            //If the hosting activity or fragment has not handled the navigation fall back to the default.
            if (!mMiniAppRequestListener.navigate(route) && !mFragmentNavigator.navigate(route)) {
                Bundle arguments = route.getArguments();
                assert path != null;
                mMiniAppRequestListener.startMiniAppFragment(path, arguments);
            }
            route.setResult(true, "Navigation completed.");
        } else {
            route.setResult(false, "Navigation failed. Received empty/null path");
        }
    }

    private boolean updateNavBar(@Nullable Bundle arguments) {
        if (arguments != null) {
            NavigationBar navigationBar = NavUtils.getNavBar(arguments);
            if (navigationBar != null) {
                mMiniAppRequestListener.updateNavBar(navigationBar);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public interface FragmentNavigator {
        /**
         * Use to delegate a navigate call to the fragment before it is being handled by the delegate.
         *
         * @param route
         * @return true | false
         */
        boolean navigate(Route route);
    }

}
