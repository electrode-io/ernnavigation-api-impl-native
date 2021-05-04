package com.ern.api.impl.core;

import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * MiniApp launch config that defines the custom configurations that can be used while starting a new MiniAppFragment to host a React Native view component.
 */
public class LaunchConfig {
    public static final int ADD_TO_BACK_STACK = 0;
    public static final int DO_NOT_ADD_TO_BACK_STACK = 1;

    @IntDef({ADD_TO_BACK_STACK, DO_NOT_ADD_TO_BACK_STACK})
    @Retention(RetentionPolicy.SOURCE)
    @interface AddToBackStackState {
    }

    public static final int NONE = 0;

    public enum TRANSITION {
        DEFAULT,
        FADE,
        SLIDE
    }

    /**
     * Pass a fragmentManager that should be used to start the new fragment.
     * If not passed the {@link AppCompatActivity#getSupportFragmentManager()} would be used to start the new fragment.
     */
    @Nullable
    FragmentManager mFragmentManager;

    /**
     * Fragment class responsible for hosting the React Native view.
     * <p>
     * A fragment class that can host a react view, one that has a proper implementation of {@link ElectrodeBaseFragmentDelegate}.
     */
    @Nullable
    Class<? extends Fragment> mFragmentClass;

    /**
     * ViewGroup id to which the fragment needs to be loaded in your layout xml.
     * If not passed, the default fragmentContainerId provided by the activity would be used.
     */
    @IdRes
    int mFragmentContainerId = NONE;

    /**
     * Optional props that you need to pass to a React Native component as initial props.
     */
    Bundle mInitialProps = null;

    /**
     * Set this to true if you want to force enable up navigation for component.
     */
    boolean mForceUpEnabled;

    /**
     * Set this to true if you want to hide the up indicator for a component
     */
    boolean mHideUpIndicatorIcon;

    /**
     * Shows your next page view component as an overLay on top of the existing screen
     * This flag is ignored if the component is the root component.
     */
    boolean mShowAsOverlay;

    /**
     * Set this value to manage the fragment back stack
     */
    @AddToBackStackState
    int mAddToBackStack = ADD_TO_BACK_STACK;

    /**
     * Decides if the root component needs to be launched when the activity delegates onCreate is called.
     */
    boolean mStartRootInOnCreate = true;

    /**
     * Set this to to true if the root page up click is handled by React native.
     */
    boolean mRootBackPressHandledByRN;

    /**
     * Indicates if a new fragment/page should replace the current visible fragment.
     * This is valid only when the backStackEntryCount is > 0.
     */
    boolean mReplace;

    /**
     * Choose the animation for your page transitions.
     */
    TRANSITION navigationTransition = TRANSITION.DEFAULT;

    public LaunchConfig() {
    }

    @Nullable
    public FragmentManager getFragmentManager() {
        return mFragmentManager;
    }

    /**
     * Pass a fragmentManager that should be used to start the new fragment.
     * If not passed the {@link AppCompatActivity#getSupportFragmentManager()} would be used to start the new fragment.
     */
    public void setFragmentManager(@Nullable FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
    }

    @Nullable
    public Class<? extends Fragment> getFragmentClass() {
        return mFragmentClass;
    }

    /**
     * Fragment class responsible for hosting the React Native view.
     * <p>
     * A fragment class that can host a react view, one that has a proper implementation of {@link ElectrodeBaseFragmentDelegate}.
     */
    public void setFragmentClass(@Nullable Class<? extends Fragment> fragmentClass) {
        mFragmentClass = fragmentClass;
    }

    public int getFragmentContainerId() {
        return mFragmentContainerId;
    }

    /**
     * ViewGroup id to which the fragment needs to be loaded in your layout xml.
     * If not passed, the default fragmentContainerId provided by the activity would be used.
     */
    public void setFragmentContainerId(@IdRes int fragmentContainerId) {
        mFragmentContainerId = fragmentContainerId;
    }

    public Bundle getInitialProps() {
        return mInitialProps;
    }

    /**
     * Optional props that you need to pass to a React Native component as initial props.
     */
    public void updateInitialProps(@Nullable Bundle initialProps) {
        if (mInitialProps != null && initialProps != null) {
            initialProps.putAll(initialProps);
        } else {
            mInitialProps = initialProps;
        }
    }

    public int getAddToBackStack() {
        return mAddToBackStack;
    }

    /**
     * Set this value to manage the fragment back stack behavior
     */
    public void setAddToBackStack(int addToBackStack) {
        mAddToBackStack = addToBackStack;
    }

    /**
     * Use this when you want up enabled for a specific page.
     *
     * @param forceUpEnabled
     */
    public void setForceUpEnabled(boolean forceUpEnabled) {
        mForceUpEnabled = forceUpEnabled;
    }

    /**
     * Set this to true will hide the up indicator for a given page.
     * Setting this to false will do nothing but falling back to the default ActionBar behavior.
     * i.e, if the app bar has an icon then it will show up and vice versa.
     */
    public void setHideUpIndicatorIcon(boolean mHideUpIndicatorIcon) {
        this.mHideUpIndicatorIcon = mHideUpIndicatorIcon;
    }

    /**
     * Should hide up indicator for a component
     * * @return true | false
     */
    public boolean isHideUpIndicatorIcon() {
        return mHideUpIndicatorIcon;
    }

    /**
     * Shows your next page view component as an overLay on top of the existing screen
     * This flag is ignored if the component is the root component.
     *
     * @param showAsOverlay true | false
     */
    public void setShowAsOverlay(boolean showAsOverlay) {
        mShowAsOverlay = showAsOverlay;
    }

    public boolean isShowAsOverlay() {
        return mShowAsOverlay;
    }

    /**
     * Decides if the root component needs to be launched when the activity delegates onCreate is called.
     * This is typically set when application/activity needs to take control of when the root component fragment needs to be started.
     *
     * @param startRootInOnCreate true | false Default value: true
     */
    public void setStartRootInOnCreate(boolean startRootInOnCreate) {
        mStartRootInOnCreate = startRootInOnCreate;
    }

    /**
     * Decides if the root component needs to be launched when the activity delegates onCreate is called.
     * This is typically set when application/activity needs to take control of when the root component fragment needs to be started.
     */

    public boolean isStartRootInOnCreate() {
        return mStartRootInOnCreate;
    }

    /**
     * Decides if the back press needed to be handed over to RN for the root page.
     *
     * @return true | false
     */
    public void setRootBackPressHandledByRN(boolean rootBackPressHandledByRN) {
        mRootBackPressHandledByRN = rootBackPressHandledByRN;
    }

    /**
     * Indicates if the back press needed to be handed over to RN for the root page.
     *
     * @return true | false
     */
    public boolean isRootBackPressHandledByRN() {
        return mRootBackPressHandledByRN;
    }

    /**
     * Set to true when you want the next fragment in the navigation stack to replace the current one
     *
     * @param replace true | false
     */
    public void setReplace(boolean replace) {
        mReplace = replace;
    }

    /**
     * Indicates if a new fragment/page should replace the current visible fragment.
     * This is valid only when the backStackEntryCount is > 0.
     */
    public boolean shouldReplace() {
        return mReplace;
    }

    public void setNavigationTransition(@NonNull TRANSITION transition) {
        navigationTransition = transition;
    }
}
