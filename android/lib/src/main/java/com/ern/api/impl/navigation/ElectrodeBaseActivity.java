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

import com.ern.api.impl.core.ElectrodeBaseActivityDelegate;
import com.ern.api.impl.core.LaunchConfig;
import com.facebook.react.ReactRootView;

import org.json.JSONObject;

public abstract class ElectrodeBaseActivity extends AppCompatActivity implements ElectrodeNavigationActivityListener {
    public static final int DEFAULT_TITLE = -1;
    public static final int NONE = 0;

    protected ElectrodeBaseActivityDelegate mElectrodeReactNavDelegate;

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
    protected ElectrodeBaseActivityDelegate createElectrodeDelegate() {
        return new ElectrodeBaseActivityDelegate(this, getRootComponentName(), createDefaultLaunchConfig());
    }

    protected LaunchConfig createDefaultLaunchConfig() {
        LaunchConfig defaultLaunchConfig = new LaunchConfig();
        defaultLaunchConfig.setFragmentClass(miniAppFragmentClass());
        defaultLaunchConfig.setFragmentContainerId(getFragmentContainerId());
        defaultLaunchConfig.setFragmentManager(getSupportFragmentManager());
        defaultLaunchConfig.updateInitialProps(getProps());
        return defaultLaunchConfig;
    }

    @Override
    @CallSuper
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mainLayout() != NONE) {
            setContentView(mainLayout());
        }

        mElectrodeReactNavDelegate = createElectrodeDelegate();
        getLifecycle().addObserver(mElectrodeReactNavDelegate);
        mElectrodeReactNavDelegate.onCreate(savedInstanceState);

        setupNavBar();
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
}
