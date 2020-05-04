package com.ern.api.impl.navigation;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.ern.api.impl.core.ActivityDelegateConstants;
import com.ern.api.impl.core.ElectrodeBaseFragmentDelegate;
import com.ern.api.impl.core.ElectrodeFragmentConfig;
import com.ern.api.impl.core.LaunchConfig;
import com.ernnavigationApi.ern.api.EnNavigationApi;
import com.ernnavigationApi.ern.model.NavEventData;
import com.ernnavigationApi.ern.model.NavigationBar;
import com.ernnavigationApi.ern.model.NavigationBarButton;
import com.ernnavigationApi.ern.model.NavigationBarLeftButton;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;

import java.io.IOException;
import java.util.List;

import static com.ern.api.impl.navigation.ReactNavigationViewModel.KEY_NAV_TYPE;

public class ElectrodeNavigationFragmentDelegate<T extends ElectrodeBaseFragmentDelegate.ElectrodeActivityListener, C extends ElectrodeFragmentConfig> extends ElectrodeBaseFragmentDelegate<ElectrodeNavigationActivityListener, ElectrodeNavigationFragmentConfig> {
    private static final String TAG = ElectrodeNavigationFragmentDelegate.class.getSimpleName();

    private ReactNavigationViewModel mNavViewModel;
    @Nullable
    protected FragmentNavigator mFragmentNavigator;
    @Nullable
    private OnUpdateNextPageLaunchConfigListener mOnUpdateNextPageLaunchConfigListener;

    @NonNull
    private OnNavBarItemClickListener mNavBarButtonClickListener;

    @Nullable
    private MenuItemDataProvider mMenuItemDataProvider;

    @Nullable
    private Menu mMenu;

    private BackPressedCallback mBackPressedCallback;

    private final Observer<Route> routeObserver = new Observer<Route>() {
        @Override
        public void onChanged(@Nullable Route route) {
            if (route != null && !route.isCompleted()) {
                Logger.d(TAG, "Delegate: %s received a new navigation route: %s", ElectrodeNavigationFragmentDelegate.this, route.getArguments());

                if (!route.getArguments().containsKey(KEY_NAV_TYPE)) {
                    throw new IllegalStateException("Missing NAV_TYPE in route arguments");
                }
                Fragment topOfTheStackFragment = getTopOfTheStackFragment();

                //NOTE: We can't put KEY_NAV_TYPE as a parcelable since ReactNative side does not support Parcelable deserialization yet.
                ReactNavigationViewModel.Type navType = ReactNavigationViewModel.Type.valueOf(route.getArguments().getString(KEY_NAV_TYPE));
                if (topOfTheStackFragment == mFragment) {
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
                } else if (topOfTheStackFragment instanceof ComponentAsOverlay) {
                    // When the top of the stack fragment is an overlay, the non-overlay fragment below it acts as the parent who is handling it's overlay children and it's navigation flows.
                    // This is because, overlay fragments are added to the stack by calling FragmentTransactionManager's add() method and not replace().
                    // When added, the new fragment's onResume() state is reached by keeping the parent fragment also in a resumed state. Hence the parent need to delegate the navigation calls to the child overlays.
                    Logger.i(TAG, "Delegating %s request to an overlay component.", navType);
                    ComponentAsOverlay overlayFragment = (ComponentAsOverlay) topOfTheStackFragment;
                    switch (navType) {
                        case NAVIGATE:
                            overlayFragment.navigate(route);
                            break;
                        case UPDATE:
                            overlayFragment.update(route);
                            break;
                        case BACK:
                            overlayFragment.back(route);
                            break;
                        case FINISH:
                            overlayFragment.finish(route);
                            break;
                    }
                } else {
                    throw new RuntimeException("Should never reach here. The fragment handling a navigation api request should be either the current fragment or the top of the stack fragment should implement ComponentAsOverlay. topOfTheStackFragment:" + topOfTheStackFragment);
                }

                if (!route.isCompleted()) {
                    throw new RuntimeException("Should never reach here. A result should be set for the route at this point. Make sure a setResult is called on the route object after the appropriate action is taken on a navigation request");
                }
                Logger.d(TAG, "Nav request handling completed by: %s", topOfTheStackFragment);
            } else {
                Logger.d(TAG, "Delegate: %s has ignored an already handled route: %s, ", ElectrodeNavigationFragmentDelegate.this, route != null ? route.getArguments() : null);
            }
        }
    };

    /**
     * @param fragment {@link Fragment} current Fragment
     */
    public ElectrodeNavigationFragmentDelegate(@NonNull Fragment fragment) {
        this(fragment, null);
    }

    /**
     * @param fragment       {@link Fragment} current Fragment
     * @param fragmentConfig {@link ElectrodeFragmentConfig} Configuration used by the fragment delegate while creating the view.
     */
    public ElectrodeNavigationFragmentDelegate(@NonNull Fragment fragment, @Nullable ElectrodeNavigationFragmentConfig fragmentConfig) {
        super(fragment, fragmentConfig);
        if (mFragment instanceof ElectrodeNavigationFragmentDelegate.FragmentNavigator) {
            mFragmentNavigator = (ElectrodeNavigationFragmentDelegate.FragmentNavigator) mFragment;
        }

        if (mFragment instanceof OnUpdateNextPageLaunchConfigListener) {
            mOnUpdateNextPageLaunchConfigListener = (OnUpdateNextPageLaunchConfigListener) mFragment;
        }

        mNavBarButtonClickListener = new ElectrodeNavigationFragmentDelegate.DefaultNavBarButtonClickListener();
    }

    @Override
    @CallSuper
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragment.setHasOptionsMenu(true);
        mBackPressedCallback = new BackPressedCallback(false);
        mFragment.requireActivity().getOnBackPressedDispatcher().addCallback(mFragment, mBackPressedCallback);
    }

    @CallSuper
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = mFragment.getArguments();
        // We only register navigation view model if the fragment was replaced by the fragment transaction manager.
        // If a fragment was added by calling fragmentTransactionManager.add() or show() then the parent fragment would handle the navigation calls.
        // This is because calling add() or show() will not trigger a lifecycle method (onPause() or onStop()) on the previous fragment.
        // Not registering a view model in this case will prevent multiple request handlers getting registered at the same time.
        if (args != null && args.getBoolean(ActivityDelegateConstants.KEY_REGISTER_NAV_VIEW_MODEL)) {
            mNavViewModel = ViewModelProviders.of(mFragment).get(ReactNavigationViewModel.class);
            mNavViewModel.getRouteLiveData().observe(mFragment.getViewLifecycleOwner(), routeObserver);
            mNavViewModel.registerNavRequestHandler();
        }
    }

    @CallSuper
    public void onResume() {
        super.onResume();
        if (mNavViewModel != null) {
            mNavViewModel.registerNavRequestHandler();
        }
        EnNavigationApi.events().emitNavEvent(new NavEventData.Builder(NavEventType.DID_FOCUS.toString()).viewId(getMiniAppViewIdentifier()).build());
    }

    @SuppressWarnings("unused")
    @CallSuper
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mMenu = menu;
        updateNavBar(mFragment.getArguments());
    }

    @SuppressWarnings("unused")
    @CallSuper
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Fragment fragment = getTopOfTheStackFragment();
            if (mFragment == fragment) {
                if (mBackPressedCallback.isEnabled()) {
                    mBackPressedCallback.handleOnBackPressed();
                    return true;
                } else {
                    mFragment.requireActivity().onBackPressed();
                    return true;
                }
            } else if (fragment instanceof ComponentAsOverlay) {
                return fragment.onOptionsItemSelected(item);
            } else {
                throw new IllegalStateException("Should never reach here, Looks like the top of the stack fragment is not this(" + this + ") or implements ComponentAsOverlay. topOfTheStackFragment: " + fragment);
            }
        }
        return false;
    }

    @CallSuper
    public void onPause() {
        super.onPause();
        if (mNavViewModel != null) {
            mNavViewModel.unRegisterNavRequestHandler();
        }
        EnNavigationApi.events().emitNavEvent(new NavEventData.Builder(NavEventType.DID_BLUR.toString()).viewId(getMiniAppViewIdentifier()).build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMenu != null) {
            mMenu = null;
        }
        mFragmentNavigator = null;
    }

    public void setMenuItemDataProvider(@NonNull MenuItemDataProvider menuItemDataProvider) {
        mMenuItemDataProvider = menuItemDataProvider;
    }

    public void back(@NonNull Route route) {
        Logger.v(TAG, "handling back call inside %s", getReactComponentName());
        //Manage fragment back-stack popping. If the given route.path is not in the stack pop a new fragment.
        boolean result = mElectrodeActivityListener.backToMiniApp(route.getArguments().getString("path"), populateArgsForBack(route));
        route.setResult(result, !result ? "back navigation failed. component not found in the back stack" : null);
    }

    public void update(@NonNull Route route) {
        Logger.v(TAG, "handling update call inside %s", getReactComponentName());
        if (mFragment.getArguments() != null) {
            mFragment.getArguments().putAll(route.getArguments());
        }
        boolean result = updateNavBar(route.getArguments());
        route.setResult(true, !result ? "failed to update nav bar." : null);
    }

    public void finish(@NonNull Route route) {
        Logger.v(TAG, "handling finish call inside %s", getReactComponentName());
        mElectrodeActivityListener.finishFlow(NavUtils.getPayload(route.getArguments()));
        route.setResult(true, null);
    }

    public void navigate(@NonNull Route route) {
        Logger.v(TAG, "handling navigate call inside %s", getReactComponentName());
        final String path = NavUtils.getPath(route.getArguments());
        Logger.d(TAG, "navigating to: " + path);

        if (path != null && path.length() != 0) {
            // If the hosting activity or fragment has not handled the navigation, fall back to the default.
            if (!mElectrodeActivityListener.navigate(path, route.getArguments()) && (mFragmentNavigator == null || !mFragmentNavigator.navigate(path, route.getArguments()))) {
                LaunchConfig launchConfig = createNextLaunchConfig(route);
                if (mOnUpdateNextPageLaunchConfigListener != null) {
                    mOnUpdateNextPageLaunchConfigListener.updateNextPageLaunchConfig(path, launchConfig);
                }
                mElectrodeActivityListener.startMiniAppFragment(path, launchConfig);
            }
            route.setResult(true, "Navigation completed.");
        } else {
            route.setResult(false, "Navigation failed. Received empty/null path");
        }
    }

    private Bundle populateArgsForBack(@NonNull Route route) {
        Bundle bundle = new Bundle(route.getArguments());
        bundle.remove("path");
        bundle.remove(KEY_NAV_TYPE);
        return bundle.size() > 0 ? bundle : null;
    }

    /**
     * Creates the launch config the next route
     *
     * @param route {@link Route}
     * @return LaunchConfig
     */
    protected LaunchConfig createNextLaunchConfig(@NonNull Route route) {
        LaunchConfig config = new LaunchConfig();
        boolean showAsOverlay = route.getArguments().getBoolean("overlay");
        config.updateInitialProps(route.getArguments());
        config.setFragmentManager(mFragmentConfig != null && mFragmentConfig.mUseChildFragmentManager ? mFragment.getChildFragmentManager() : null);
        config.setShowAsOverlay(showAsOverlay);
        return config;
    }

    @Nullable
    protected Fragment getTopOfTheStackFragment() {
        if (mFragment.getActivity() != null) {
            List<Fragment> fragments = mFragment.getActivity().getSupportFragmentManager().getFragments();
            if (fragments.size() > 0) {
                return fragments.get(fragments.size() - 1);
            }
        }
        return null;
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

        private OnNavBarItemClickListener mSuppliedButtonClickListener;

        DefaultNavBarButtonClickListener() {
            mSuppliedButtonClickListener = (mFragment instanceof OnNavBarItemClickListener) ? (OnNavBarItemClickListener) mFragment : null;
        }

        @Override
        public boolean onNavBarButtonClicked(@NonNull NavigationBarButton button, @NonNull MenuItem item) {
            if (mSuppliedButtonClickListener == null || !mSuppliedButtonClickListener.onNavBarButtonClicked(button, item)) {
                //TODO: This line should be removed with next major version(2.x) update of ern-navigation. Keeping here for backward compatibility
                EnNavigationApi.events().emitOnNavButtonClick(button.getId());

                EnNavigationApi.events().emitNavEvent(new NavEventData.Builder(NavEventType.BUTTON_CLICK.toString()).viewId(getMiniAppViewIdentifier()).jsonPayload("{\"id\": \"" + button.getId() + "\"}").build());
            }
            return true;
        }
    }

    /**
     * Fragments may implement FragmentNavigator when it needs to override a navigate call.
     */
    public interface FragmentNavigator {
        /**
         * Use to delegate a navigate call to the fragment before it is being handled by the delegate.
         *
         * @param pageName {@link String} MiniApp view component name or the next page to be navigated to.
         * @param data     {@link Bundle} Data associated with this navigation.
         * @return true | false
         */
        boolean navigate(@Nullable String pageName, @NonNull Bundle data);
    }

    /**
     * Fragments may implement this interface when they need to customize the next Launch Config {@link LaunchConfig} while navigating to a new page.
     */
    public interface OnUpdateNextPageLaunchConfigListener {
        /**
         * Simply update the config here.
         *
         * @param nextPageName        {@link String} Next page name
         * @param defaultLaunchConfig {@link LaunchConfig} with default values for the next page launch config.
         */
        void updateNextPageLaunchConfig(@NonNull final String nextPageName, @NonNull final LaunchConfig defaultLaunchConfig);
    }

    private void updateNavBar(@NonNull NavigationBar navigationBar) {
        Logger.d(TAG, "Updating nav bar: %s", navigationBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            Logger.d(TAG, "Action bar is null, skipping nav bar update");
            return;
        }
        actionBar.setTitle(navigationBar.getTitle());
        updateHomeAsUpIndicator(navigationBar.getLeftButton(), actionBar);

        if (mMenu != null && mFragment.getActivity() != null) {
            MenuUtil.updateMenuItems(mMenu, navigationBar, mNavBarButtonClickListener, mMenuItemDataProvider, mFragment.getActivity());
        }
    }

    private void updateHomeAsUpIndicator(@Nullable NavigationBarLeftButton leftButton, @NonNull ActionBar actionBar) {
        mBackPressedCallback.setEnabled(leftButton);
        if (leftButton != null) {
            if (leftButton.getDisabled() != null && leftButton.getDisabled()) {
                Logger.d(TAG, "Disabling DisplayHomeAsUp for component: %s", getReactComponentName());
                actionBar.setDisplayHomeAsUpEnabled(false);
                return;
            } else if (setHomeAsUpIndicatorIcon(actionBar, leftButton)) {
                return;
            }
        }

        //Default action
        if (mFragment.getArguments() != null) {
            if (mFragment.getArguments().getBoolean(ActivityDelegateConstants.KEY_MINI_APP_FRAGMENT_SHOW_UP_ENABLED)) {
                Logger.d(TAG, "Enabling up indicator for component: %s", getReactComponentName());
                actionBar.setHomeAsUpIndicator(0);
                actionBar.setDisplayHomeAsUpEnabled(true);
            } else if (mFragment.getArguments().getBoolean(ActivityDelegateConstants.KEY_MINI_APP_FRAGMENT_HIDE_UP_INDICATOR)) {
                Logger.d(TAG, "Hiding up indicator for component: %s", getReactComponentName());
                actionBar.setDisplayHomeAsUpEnabled(false);
            }
        }
    }

    private boolean setHomeAsUpIndicatorIcon(@NonNull ActionBar actionBar, @NonNull NavigationBarLeftButton leftButton) {
        if (mFragment.getActivity() != null) {
            final String iconName = leftButton.getIcon();
            if (iconName == null) {
                return false;
            }
            Logger.d(TAG, "Customizing up indicator for component: %s", getReactComponentName());
            if (mMenuItemDataProvider != null && mMenuItemDataProvider.homeAsUpIndicatorOverride(iconName) != MenuItemDataProvider.NONE) {
                Logger.d(TAG, "Setting up-indicator provided by native for component: %s", getReactComponentName());
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeAsUpIndicator(mMenuItemDataProvider.homeAsUpIndicatorOverride(iconName));
                return true;
            } else if (MenuUtil.canLoadIconFromURI(iconName)) {
                try {
                    Drawable iconDrawable = MenuUtil.getBitmapFromURL(mFragment.getActivity(), iconName);
                    actionBar.setDisplayHomeAsUpEnabled(true);
                    actionBar.setHomeAsUpIndicator(iconDrawable);
                    return true;
                } catch (IOException e) {
                    Logger.w(TAG, "Load failed for left icon from URL: " + iconName);
                }
            } else {
                int icon = mFragment.getActivity().getResources().getIdentifier(iconName, "drawable", mFragment.getActivity().getPackageName());
                if (icon != 0) {
                    actionBar.setDisplayHomeAsUpEnabled(true);
                    actionBar.setHomeAsUpIndicator(icon);
                    return true;
                } else {
                    Logger.w(TAG, "Left Icon not found for button:%s", leftButton.getId());
                }
            }
        }
        Logger.v(TAG, "No custom up indicator set.");
        return false;
    }

    @Nullable
    private ActionBar getSupportActionBar() {
        if (mFragment.getActivity() instanceof AppCompatActivity) {
            return ((AppCompatActivity) mFragment.getActivity()).getSupportActionBar();
        }
        return null;
    }

    private class BackPressedCallback extends OnBackPressedCallback {
        @Nullable
        private NavigationBarLeftButton mLeftButton;

        /**
         * Create a {@link OnBackPressedCallback}.
         *
         * @param enabled The default enabled state for this callback.
         * @see #setEnabled(boolean)
         */
        private BackPressedCallback(boolean enabled) {
            super(enabled);
        }

        private void setEnabled(@Nullable NavigationBarLeftButton leftButton) {
            mLeftButton = leftButton;
            if (leftButton != null && (leftButton.getId() != null || (leftButton.getDisabled() != null && leftButton.getDisabled()))) {
                setEnabled(true);
            } else {
                setEnabled(false);
            }
        }

        @Override
        public void handleOnBackPressed() {
            if (mLeftButton == null) {
                throw new IllegalStateException("handleOnBackPressed: Should never reach here. NavigationBarLeftButton is null.");
            }

            if (mLeftButton.getId() != null) {
                Logger.v(TAG, "handleOnBackPressed: firing event to React Native, button id: %s", mLeftButton.getId());
                //TODO: This line should be removed with next major version update of ern-navigation. Keeping here for backward compatibility
                EnNavigationApi.events().emitOnNavButtonClick(mLeftButton.getId());

                EnNavigationApi.events().emitNavEvent(new NavEventData.Builder(NavEventType.BUTTON_CLICK.toString()).viewId(getMiniAppViewIdentifier()).jsonPayload("{\"id\": \"" + mLeftButton.getId() + "\"}").build());
            } else if (mLeftButton.getDisabled() != null && mLeftButton.getDisabled()) {
                Logger.v(TAG, "handleOnBackPressed: Back press disabled, component:%s", getReactComponentName());
            } else {
                Logger.w(TAG, "handleOnBackPressed: not handled by %s. [SHOULD NEVER REACH HERE]", getReactComponentName());
            }
        }
    }
}
