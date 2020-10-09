package com.ern.api.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.ern.api.impl.core.LaunchConfig;
import com.ern.api.impl.navigation.MiniAppNavigationFragment;
import com.ern.api.impl.navigation.NavigationLaunchConfig;
import com.ernnavigationApi.ern.api.EnNavigationApi;
import com.ernnavigationApi.ern.model.ErnNavRoute;
import com.ernnavigationApi.ern.model.NavigationBar;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeResponseListener;
import com.walmartlabs.electrode.reactnative.bridge.FailureMessage;
import com.walmartlabs.electrode.reactnative.bridge.None;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;

import static com.ern.api.impl.core.LaunchConfig.DO_NOT_ADD_TO_BACK_STACK;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LaunchConfigTests {
    @Before
    public void setUp() {
        Logger.overrideLogLevel(Logger.LogLevel.VERBOSE);
    }

    /**
     * This activity sets the DO_NOT_ADD_TO_BACK_STACK flag only for root component. Subsequent navigation calls will be added to the back stack
     */
    public static class AddToBackStackDisabledForRootActivity extends SampleActivity {
        @Override
        protected NavigationLaunchConfig createNavigationLaunchConfig() {
            NavigationLaunchConfig launchConfig = super.createNavigationLaunchConfig();
            launchConfig.setAddToBackStack(DO_NOT_ADD_TO_BACK_STACK);
            return launchConfig;
        }
    }

    @Test
    public void testRootComponentIsNotAddedToBackStackWhenFlagIsSet() {
        ActivityScenario<AddToBackStackDisabledForRootActivity> scenario = ActivityScenario.launch(AddToBackStackDisabledForRootActivity.class);
        //Navigate several times
        final int TOTAL_NAVIGATION = 10;
        final CountDownLatch latch = new CountDownLatch(TOTAL_NAVIGATION);
        repeatNavigation(latch, 1, TOTAL_NAVIGATION);

        //Wait for the navigation requests to complete
        try {
            latch.await();
        } catch (InterruptedException e) {
            fail();
        }

        scenario.onActivity(new ActivityScenario.ActivityAction<AddToBackStackDisabledForRootActivity>() {
            @Override
            public void perform(AddToBackStackDisabledForRootActivity activity) {
                assertThat(activity.getSupportActionBar()).isNotNull();
                assertThat(activity.getSupportActionBar().getTitle()).isEqualTo("page " + TOTAL_NAVIGATION); //Title for the last page
                //Since ADD_TO_BACK_STACK is turned off for the root component, the stack size should match the TOTAL_NAVIGATION count by excluding the root component
                assertThat(activity.getSupportFragmentManager().getBackStackEntryCount()).isEqualTo(TOTAL_NAVIGATION);
                assertThat(activity.getSupportFragmentManager().getFragments().size()).isEqualTo(1);
                assertThat(((MiniAppNavigationFragment) activity.getSupportFragmentManager().getFragments().get(0)).getReactComponentName()).isEqualTo("component" + TOTAL_NAVIGATION);
            }
        });
    }

    /**
     * This activity sets the DO_NOT_ADD_TO_BACK_STACK flag for root component as well as subsequent nav components.
     */
    public static class AddToBackStackDisabledActivity extends SampleActivity {
        @Override
        protected NavigationLaunchConfig createNavigationLaunchConfig() {
            NavigationLaunchConfig launchConfig = super.createNavigationLaunchConfig();
            //Setting this ensures that the root component is not added to the back stack
            launchConfig.setAddToBackStack(DO_NOT_ADD_TO_BACK_STACK);
            return launchConfig;
        }

        @Override
        public void startMiniAppFragment(@NonNull String componentName, @NonNull LaunchConfig launchConfig) {
            //This will globally turn off Adding to back stack for all pages that is navigated from the root component.
            launchConfig.setAddToBackStack(DO_NOT_ADD_TO_BACK_STACK);
            super.startMiniAppFragment(componentName, launchConfig);
        }
    }

    @Test
    public void testNothingIsAddedToBackStackWhenFlagIsSet() {
        ActivityScenario<AddToBackStackDisabledActivity> scenario = ActivityScenario.launch(AddToBackStackDisabledActivity.class);
        //Navigate several times
        final int TOTAL_NAVIGATION = 10;
        final CountDownLatch latch = new CountDownLatch(TOTAL_NAVIGATION);
        repeatNavigation(latch, 1, TOTAL_NAVIGATION);

        //Wait for the navigation requests to complete
        try {
            latch.await();
        } catch (InterruptedException e) {
            fail();
        }

        scenario.onActivity(new ActivityScenario.ActivityAction<AddToBackStackDisabledActivity>() {
            @Override
            public void perform(AddToBackStackDisabledActivity activity) {
                assertThat(activity.getSupportActionBar()).isNotNull();
                assertThat(activity.getSupportActionBar().getTitle()).isEqualTo("page " + TOTAL_NAVIGATION); //Title for the last page
                //Since ADD_TO_BACK_STACK is turned off for the root component, the stack size should match the TOTAL_NAVIGATION count by excluding the root component
                assertThat(activity.getSupportFragmentManager().getBackStackEntryCount()).isEqualTo(0);
                assertThat(activity.getSupportFragmentManager().getFragments().size()).isEqualTo(1);
                assertThat(((MiniAppNavigationFragment) activity.getSupportFragmentManager().getFragments().get(0)).getReactComponentName()).isEqualTo("component" + TOTAL_NAVIGATION);
            }
        });
    }

    public static class DisableMiniAppLaunchInOnCreateActivity extends SampleActivity {
        @Override
        protected NavigationLaunchConfig createNavigationLaunchConfig() {
            NavigationLaunchConfig launchConfig = super.createNavigationLaunchConfig();
            // This flag will ensure that when the activity is launched the root component will not be started.
            // This is used by consumers who needs to display other screens inside an activity and take control on when to show RN component.
            launchConfig.setStartRootInOnCreate(false);
            return launchConfig;
        }
    }

    @Test
    public void testDisableStartRootInOnCreate() {
        ActivityScenario<DisableMiniAppLaunchInOnCreateActivity> scenario = ActivityScenario.launch(DisableMiniAppLaunchInOnCreateActivity.class);
        scenario.onActivity(new ActivityScenario.ActivityAction<DisableMiniAppLaunchInOnCreateActivity>() {
            @Override
            public void perform(DisableMiniAppLaunchInOnCreateActivity activity) {
                assertThat(activity.getSupportActionBar()).isNotNull();
                assertThat(activity.getSupportActionBar().getTitle()).isEqualTo("page 0"); //Title for the last page
                //Since ADD_TO_BACK_STACK is turned off for the root component, the stack size should match the TOTAL_NAVIGATION count by excluding the root component
                assertThat(activity.getSupportFragmentManager().getBackStackEntryCount()).isEqualTo(0);
                //There should not be any fragment since startRootInOnCreate is disabled
                assertThat(activity.getSupportFragmentManager().getFragments().size()).isEqualTo(0);
            }
        });
    }

    public static class ValidateReplaceActivity extends SampleActivity {
        @Override
        public void startMiniAppFragment(@NonNull String componentName, @NonNull LaunchConfig launchConfig) {
            launchConfig.setReplace(true);
            super.startMiniAppFragment(componentName, launchConfig);
        }
    }

    @Test
    public void testFragmentReplaceForNonRootNavigation() {
        ActivityScenario<ValidateReplaceActivity> scenario = ActivityScenario.launch(ValidateReplaceActivity.class);

        //Navigate several times
        final int TOTAL_NAVIGATION = 10;
        final CountDownLatch latch = new CountDownLatch(TOTAL_NAVIGATION);
        repeatNavigation(latch, 1, TOTAL_NAVIGATION);

        //Wait for the navigation requests to complete
        try {
            latch.await();
        } catch (InterruptedException e) {
            fail();
        }

        scenario.onActivity(new ActivityScenario.ActivityAction<ValidateReplaceActivity>() {
            @Override
            public void perform(ValidateReplaceActivity activity) {
                assertThat(activity.getSupportActionBar()).isNotNull();
                assertThat(activity.getSupportActionBar().getTitle()).isEqualTo("page " + TOTAL_NAVIGATION); //Title for the last page
                //Since every new page is replacing the current page there will always be one fragment in the back stack, the last one.
                assertThat(activity.getSupportFragmentManager().getBackStackEntryCount()).isEqualTo(1);
                assertThat(activity.getSupportFragmentManager().getFragments().size()).isEqualTo(1);
                assertThat(((MiniAppNavigationFragment) activity.getSupportFragmentManager().getFragments().get(0)).getReactComponentName()).isEqualTo("component" + TOTAL_NAVIGATION);
            }
        });
    }

    private void repeatNavigation(final CountDownLatch latch, final int currentCount, final int totalNavigationCount) {
        EnNavigationApi.requests().navigate(new ErnNavRoute.Builder("component" + currentCount).navigationBar(new NavigationBar.Builder("page " + currentCount).build()).build(), new ElectrodeBridgeResponseListener<None>() {
            @Override
            public void onSuccess(@Nullable None responseData) {
                latch.countDown();
                if (totalNavigationCount != currentCount) {
                    repeatNavigation(latch, currentCount + 1, totalNavigationCount);
                }
            }

            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                fail(failureMessage.getMessage());
            }
        });
    }
}

