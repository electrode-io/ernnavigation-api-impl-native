package com.ern.api.impl.navigation;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.ern.api.impl.core.ActivityDelegateConstants;
import com.ern.api.impl.core.ElectrodeReactFragmentDelegate;
import com.ernnavigationApi.ern.api.EnNavigationApi;
import com.ernnavigationApi.ern.model.NavigationBar;
import com.ernnavigationApi.ern.model.NavigationBarButton;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;

public class ElectrodeReactFragmentNavDelegate extends ElectrodeReactFragmentDelegate<MiniAppNavRequestListener> {
    private static final String TAG = ElectrodeReactFragmentNavDelegate.class.getSimpleName();

    private ReactNavigationViewModel mNavViewModel;
    private FragmentNavigator mFragmentNavigator;
    private OnNavBarItemClickListener navBarButtonClickListener;

    @Nullable
    private Menu mMenu;

    private final Observer<Route> routeObserver = new Observer<Route>() {
        @Override
        public void onChanged(@Nullable Route route) {
            if (route != null && !route.isCompleted()) {
                Logger.d(TAG, "Delegate:%s received a new navigation route: %s",  ElectrodeReactFragmentNavDelegate.this, route.getArguments());

                if (!route.getArguments().containsKey(ReactNavigationViewModel.KEY_NAV_TYPE)) {
                    throw new IllegalStateException("Missing NAV_TYPE in route arguments");
                }

                //NOTE: We can't put KEY_NAV_TYPE as a parcelable since ReactNative side does not support Parcelable deserialization yet.
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
                Logger.d(TAG, "Nav request handling completed by delegate: %s", ElectrodeReactFragmentNavDelegate.this);
            } else {
                Logger.d(TAG, "Delegate:%s has ignored an already handled route: %s, ", ElectrodeReactFragmentNavDelegate.this, route != null ? route.getArguments() : null);
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

        if (fragment instanceof OnNavBarItemClickListener) {
            navBarButtonClickListener = (OnNavBarItemClickListener) fragment;
        } else {
            navBarButtonClickListener = new DefaultNavBarButtonClickListener();
        }
    }

    @Override
    @CallSuper
    public void onAttach(Context context) {
        if (!(context instanceof MiniAppNavRequestListener)) {
            throw new RuntimeException(context.toString()
                    + " must implement a MiniAppNavRequestListener");
        }
        super.onAttach(context);
    }

    @Override
    @CallSuper
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragment.setHasOptionsMenu(true);
    }

    @CallSuper
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mNavViewModel = ViewModelProviders.of(mFragment).get(ReactNavigationViewModel.class);
        mNavViewModel.getRouteLiveData().observe(mFragment.getViewLifecycleOwner(), routeObserver);

    }

    @CallSuper
    public void onResume() {
        super.onResume();
        mNavViewModel.registerNavRequestHandler();
    }

    @CallSuper
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mMenu = menu;
        updateNavBar(mFragment.getArguments());
    }

    @CallSuper
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mFragment.getActivity() != null) {
                mFragment.getActivity().onBackPressed();
                return true;
            }
        }
        return false;
    }

    @CallSuper
    public void onPause() {
        super.onPause();
        mNavViewModel.unRegisterNavRequestHandler();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMenu != null) {
            mMenu = null;
        }
        mFragmentNavigator = null;
        mMiniAppRequestListener = null;
    }

    private void back(@NonNull Route route) {
        //Manage fragment back-stack popping. If the given route.path is not in the stack pop a new fragment.
        boolean result = mMiniAppRequestListener.backToMiniApp(route.getArguments().getString("path"));
        route.setResult(result, !result ? "back navigation failed. component not found in the back stack" : null);
    }

    private void update(@NonNull Route route) {
        if (mFragment.getArguments() != null) {
            mFragment.getArguments().putAll(route.getArguments());
        }
        boolean result = updateNavBar(route.getArguments());
        route.setResult(true, !result ? "failed to update nav bar." : null);
    }

    private void finish(@NonNull Route route) {
        Logger.d(TAG, "finish triggered by RN. Hosting activity will be notified.");
        mMiniAppRequestListener.finishFlow(NavUtils.getPayload(route.getArguments()));
        route.setResult(true, null);
    }

    private void navigate(@NonNull Route route) {
        final String path = NavUtils.getPath(route.getArguments());
        Logger.d(TAG, "navigating to: " + path);

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
                updateNavBar(navigationBar);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private class DefaultNavBarButtonClickListener implements OnNavBarItemClickListener {

        @Override
        public void onNavBarButtonClicked(@NonNull NavigationBarButton button, @NonNull MenuItem item) {
            EnNavigationApi.events().emitOnNavButtonClick(button.getId());
        }
    }

    public interface FragmentNavigator {
        /**
         * Use to delegate a navigate call to the fragment before it is being handled by the delegate.
         *
         * @param route Route object
         * @return true | false
         */
        boolean navigate(Route route);
    }


    private void updateNavBar(@NonNull NavigationBar navigationBar) {
        updateTitle(navigationBar);

        if (mMenu != null && mFragment.getActivity() != null) {
            MenuUtil.updateMenuItems(mMenu, navigationBar, navBarButtonClickListener, null/*FIXME*/, mFragment.getActivity());
        }
    }

    private void updateTitle(@NonNull NavigationBar navigationBar) {
        Activity activity = mFragment.getActivity();
        if (activity != null)
            if (activity instanceof AppCompatActivity) {
                ActionBar actionBar;
                actionBar = ((AppCompatActivity) activity).getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setTitle("");
                    actionBar.setTitle(navigationBar.getTitle());
                }
            } else {
                android.app.ActionBar actionBar = activity.getActionBar();
                if (actionBar != null) {
                    actionBar.setTitle("");
                    actionBar.setTitle(navigationBar.getTitle());
                }
            }
    }
}


