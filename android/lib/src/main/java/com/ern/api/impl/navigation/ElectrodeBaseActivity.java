package com.ern.api.impl.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.ern.api.impl.core.LaunchConfig;
import com.facebook.react.ReactRootView;
import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;

import org.json.JSONObject;

public abstract class ElectrodeBaseActivity extends AppCompatActivity implements ElectrodeNavigationActivityListener, PermissionAwareActivity {
    public static final int DEFAULT_TITLE = -1;
    public static final int NONE = 0;

    private static final String TAG = ElectrodeBaseActivity.class.getSimpleName();

    protected ElectrodeNavigationActivityDelegate mElectrodeReactNavDelegate;

    /**
     * Override and provide the main layout resource.
     *
     * @return int {@link LayoutRes}
     */
    @LayoutRes
    protected abstract int mainLayout();

    /**
     * React Native component name that will be rendered when the activity is first launched.
     *
     * @return String
     */
    @NonNull
    protected abstract String getRootComponentName();

    /**
     * Id for the fragment container.
     *
     * @return IdRes of the fragment holder in your layout xml.
     */
    @IdRes
    protected abstract int getFragmentContainerId();

    /**
     * Props that needs to be passed to the root component.
     *
     * @return Bundle
     */
    @Nullable
    protected Bundle getProps() {
        return null;
    }

    /**
     * Override to provide title for the landing page.
     *
     * @return int @{@link StringRes}
     */
    @StringRes
    protected int title() {
        return DEFAULT_TITLE;
    }

    /**
     * Initial/Default fragment used by the activity to host a react native view.
     *
     * @return Class
     */
    @NonNull
    protected Class<? extends Fragment> miniAppFragmentClass() {
        return MiniAppNavigationFragment.class;
    }

    /**
     * Override this method to hide/show the nav bar.
     *
     * @return false | true Default: false
     */
    protected boolean hideNavBar() {
        return false;
    }

    /**
     * Override if you need to provide a custom delegate.
     *
     * @return ElectrodeReactFragmentActivityDelegate
     */
    @NonNull
    protected ElectrodeNavigationActivityDelegate createElectrodeDelegate() {
        return new ElectrodeNavigationActivityDelegate(this, getRootComponentName(), createNavigationLaunchConfig());
    }

    /**
     * Default configuration provided for navigation.
     *
     * @return NavigationLaunchConfig
     */
    protected NavigationLaunchConfig createNavigationLaunchConfig() {
        LaunchConfig navConfig = createDefaultLaunchConfig();
        if (navConfig instanceof NavigationLaunchConfig) {
            return (NavigationLaunchConfig) navConfig;
        } else {
            if (navConfig != null) {
                Logger.w(TAG, "createDefaultLaunchConfig() is Deprecated, your default launch config is IGNORED, please move to createNavigationLaunchConfig");
            }
            return createNavLaunchConfigInternal();
        }
    }

    /**
     * Keeping this method for backward compatibility
     *
     * @deprecated use {@link #createNavigationLaunchConfig()}
     */
    @Deprecated
    protected LaunchConfig createDefaultLaunchConfig() {
        return createNavLaunchConfigInternal();
    }

    private NavigationLaunchConfig createNavLaunchConfigInternal() {
        NavigationLaunchConfig defaultLaunchConfig = new NavigationLaunchConfig();
        defaultLaunchConfig.setFragmentClass(miniAppFragmentClass());
        defaultLaunchConfig.setFragmentContainerId(getFragmentContainerId());
        defaultLaunchConfig.setFragmentManager(getSupportFragmentManager());
        defaultLaunchConfig.updateInitialProps(getProps());
        defaultLaunchConfig.setHideUpIndicatorIcon(true);
        return defaultLaunchConfig;
    }

    @Override
    @CallSuper
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preRootComponentRender();
        if (mainLayout() != NONE) {
            setContentView(mainLayout());
        }

        mElectrodeReactNavDelegate = createElectrodeDelegate();
        getLifecycle().addObserver(mElectrodeReactNavDelegate);
        mElectrodeReactNavDelegate.onCreate(savedInstanceState);

        setupNavBar();
    }

    /**
     * Override this to perform any action that needs to be performed before the root component is first rendered.
     * Some apps might only choose to initialize the container inside an activity. This method can act as a helper method for them to perform init.
     */
    protected void preRootComponentRender() {
        // Do nothing here, override if needed.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mElectrodeReactNavDelegate = null;
    }

    @Override
    public void onBackPressed() {
        if (!mElectrodeReactNavDelegate.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mElectrodeReactNavDelegate.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mElectrodeReactNavDelegate.onKeyUp(keyCode, event)) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void setupNavBar() {
        if (getSupportActionBar() != null) {
            if (hideNavBar()) {
                getSupportActionBar().hide();
            } else if (title() != DEFAULT_TITLE) {
                getSupportActionBar().setTitle(getString(title()));
            }
        }
    }

    @Override
    public boolean navigate(@Nullable String pageName, @NonNull Bundle data) {
        //Adding navigate(route) for backward compatibility
        return navigate(new Route.Builder(data).build());
    }

    /**
     * @deprecated override {@link #navigate(String, Bundle)} instead.
     */
    @Deprecated
    public boolean navigate(Route route) {
        return false;
    }

    @Override
    public void finishFlow(@Nullable JSONObject finalPayload) {
        finish();
    }

    @Override
    public boolean backToMiniApp(@Nullable String tag, @Nullable Bundle data) {
        return mElectrodeReactNavDelegate.switchBackToFragment(tag, data);
    }

    @Override
    public View createReactNativeView(@NonNull String componentName, @Nullable Bundle props) {
        return mElectrodeReactNavDelegate.createReactRootView(componentName, props);
    }

    @Override
    public void removeReactNativeView(@NonNull String appName, @NonNull ReactRootView reactRootView) {
        mElectrodeReactNavDelegate.removeMiniAppView(appName, reactRootView);
    }

    @Override
    public void startMiniAppFragment(@NonNull String componentName, @NonNull LaunchConfig launchConfig) {
        mElectrodeReactNavDelegate.startMiniAppFragment(componentName, launchConfig);
    }

    @Nullable
    @Override
    public Bundle globalProps() {
        return null;
    }

    @Override
    public boolean showDevMenuIfDebug(KeyEvent event) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mElectrodeReactNavDelegate.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void requestPermissions(
            String[] permissions, int requestCode, PermissionListener listener) {
        mElectrodeReactNavDelegate.requestPermissions(permissions, requestCode, listener);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mElectrodeReactNavDelegate.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }

    @Override
    public int checkSelfPermission(String permission) {
        return super.checkSelfPermission(permission);
    }

    @Override
    public int checkPermission(String permission, int pid, int uid) {
        return super.checkPermission(permission, pid, uid);
    }
}
