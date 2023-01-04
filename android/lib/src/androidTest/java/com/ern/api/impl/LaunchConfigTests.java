package com.ern.api.impl;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.ern.api.impl.core.LaunchConfig;
import com.ern.api.impl.navigation.MiniAppNavigationFragment;
import com.ern.api.impl.navigation.NavEventType;
import com.ern.api.impl.navigation.NavigationLaunchConfig;
import com.ernnavigation.ern.api.EnNavigationApi;
import com.ernnavigation.ern.model.ErnNavRoute;
import com.ernnavigation.ern.model.NavEventData;
import com.ernnavigation.ern.model.NavigationBar;
import com.ernnavigation.ern.model.NavigationBarLeftButton;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeEventListener;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeResponseListener;
import com.walmartlabs.electrode.reactnative.bridge.FailureMessage;
import com.walmartlabs.electrode.reactnative.bridge.None;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;
import com.walmartlabs.ern.navigation.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
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

    public static class RootBackPressConfigActivity extends SampleActivity {
        @Override
        protected NavigationLaunchConfig createNavigationLaunchConfig() {
            NavigationLaunchConfig config = super.createNavigationLaunchConfig();
            config.setRootBackPressHandledByRN(true);
            return config;
        }
    }

    @Test
    public void testRootBackPressHandledByRn() {
        ActivityScenario<RootBackPressConfigActivity> scenario = ActivityScenario.launch(RootBackPressConfigActivity.class);
        scenario.onActivity(new ActivityScenario.ActivityAction<RootBackPressConfigActivity>() {
            @Override
            public void perform(RootBackPressConfigActivity activity) {
                activity.onBackPressed();
                // Since the back press is supposed to be handed over to RN, native does not handle it.
                assertThat(activity.didFinish).isFalse();
            }
        });
    }

    public static class OverlayActivity extends SampleActivity {
        @Override
        protected NavigationLaunchConfig createNavigationLaunchConfig() {
            NavigationLaunchConfig config = super.createNavigationLaunchConfig();
            config.setShowAsOverlay(true);
            return config;
        }
    }

    @Test
    public void testShowAsOverlay() {
        ActivityScenario<OverlayActivity> scenario = ActivityScenario.launch(OverlayActivity.class);
        scenario.onActivity(new ActivityScenario.ActivityAction<OverlayActivity>() {
            @Override
            public void perform(OverlayActivity activity) {
                assertThat(activity.getSupportFragmentManager().getBackStackEntryCount()).isEqualTo(1);
                //There should not be any fragment since startRootInOnCreate is disabled
                assertThat(activity.getSupportFragmentManager().getFragments().size()).isEqualTo(1);
            }
        });
    }

    public static class UpEnabledForRootActivity extends SampleActivity {
        @Override
        protected NavigationLaunchConfig createNavigationLaunchConfig() {
            NavigationLaunchConfig launchConfig = super.createNavigationLaunchConfig();
            //Setting this ensures that up arrow is enabled for the root component
            launchConfig.setForceUpEnabled(true);
            return launchConfig;
        }
    }

    @Test
    public void upEnabledForRootTest() {
        final ActivityScenario<UpEnabledForRootActivity> scenario = ActivityScenario.launch(UpEnabledForRootActivity.class);

        onView(withContentDescription(R.string.abc_action_bar_up_description)).check(new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                assertThat(view).isNotNull();
            }
        });
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());
        Lifecycle.State state = scenario.getState();
        // Sometimes the activity gets destroyed immediately ot it can be delayed.
        // Covering both cases below.
        if(state == Lifecycle.State.STARTED) {
            scenario.onActivity(new ActivityScenario.ActivityAction<UpEnabledForRootActivity>() {
                @Override
                public void perform(UpEnabledForRootActivity activity) {
                    assertThat(activity.isFinishing()).isTrue();
                }
            });
        } else {
            assertThat(scenario.getState()).isEqualTo(Lifecycle.State.DESTROYED);
        }
    }

    @Test
    public void upDisabledForRootTest() {
        ActivityScenario.launch(SampleActivity.class);
        onView(withContentDescription(R.string.abc_action_bar_up_description)).check(doesNotExist());
    }

    public static class RootUpClickToRnActivity extends SampleActivity {
        @Override
        protected NavigationLaunchConfig createNavigationLaunchConfig() {
            NavigationLaunchConfig launchConfig = super.createNavigationLaunchConfig();
            //Setting this ensures that up arrow is enabled for the root component
            launchConfig.setForceUpEnabled(true);
            launchConfig.setRootBackPressHandledByRN(true);
            return launchConfig;
        }
    }

    /**
     * This test ensures that when `aunchConfig.setRootBackPressHandledByRN(true);` is set in launchConfig,
     * the back and up arrow press click would be sent back to RN for handling. For this to work, it is expected that,
     * RN component calls `update` api with a NavigationBarLeftButton object with a valid id. If not set, this flag is ignored
     */
    @Test
    public void validateRootBackPressHandoverToRN() {
        ActivityScenario<RootUpClickToRnActivity> scenario = ActivityScenario.launch(RootUpClickToRnActivity.class);
        final CountDownLatch latch = new CountDownLatch(1);
        final String LEFT_BUTTON_ID = "leftButtonId";
        NavigationBarLeftButton leftButton = new NavigationBarLeftButton
                .Builder()
                .id(LEFT_BUTTON_ID)
                .build();
        NavigationBar navBar = new NavigationBar
                .Builder("title")
                .leftButton(leftButton)
                .build();
        ErnNavRoute route = new ErnNavRoute
                .Builder("dummyRootComponent")
                .navigationBar(navBar)
                .build();
        EnNavigationApi
                .requests()
                .update(route, new ElectrodeBridgeResponseListener<None>() {
                    @Override
                    public void onSuccess(@Nullable None responseData) {
                        latch.countDown();
                    }

                    @Override
                    public void onFailure(@NonNull FailureMessage failureMessage) {
                        fail("Update request should not fail");
                    }
                });
        try {
            latch.await();
        } catch (InterruptedException e) {
            fail();
        }

        final CountDownLatch eventLatch = new CountDownLatch(1);
        UUID uuid = EnNavigationApi.events().addNavEventEventListener(new ElectrodeBridgeEventListener<NavEventData>() {
            @Override
            public void onEvent(@Nullable NavEventData eventPayload) {
                assertThat(eventPayload).isNotNull();
                if (eventPayload.getEventType().equals(NavEventType.BUTTON_CLICK.toString())) {
                    assertThat(eventPayload.getJsonPayload()).isNotNull();
                    try {
                        JSONObject buttonClickJson = new JSONObject(eventPayload.getJsonPayload());
                        assertThat(buttonClickJson.getString("id")).isEqualTo(LEFT_BUTTON_ID);
                    } catch (JSONException e) {
                        fail("Invalid JSON for button click payload");
                    }
                    eventLatch.countDown();
                }
            }
        });
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());
        //Click should fire an event back to RN instead of going back and destroying the activity.
        scenario.onActivity(new ActivityScenario.ActivityAction<RootUpClickToRnActivity>() {
            @Override
            public void perform(RootUpClickToRnActivity activity) {
                assertThat(activity.isFinishing()).isFalse();
            }
        });

        try {
            eventLatch.await();
        } catch (InterruptedException e) {
            fail();
        }
        EnNavigationApi.events().removeNavEventEventListener(uuid);
    }

    @Test
    public void validateLeftButtonClickDisabledForRootComponent() {
        ActivityScenario<RootUpClickToRnActivity> scenario = ActivityScenario.launch(RootUpClickToRnActivity.class);
        final CountDownLatch latch = new CountDownLatch(1);
        final String LEFT_BUTTON_ID = "leftButtonId";
        NavigationBarLeftButton leftButton = new NavigationBarLeftButton
                .Builder()
                .id(LEFT_BUTTON_ID)
                .disabled(true)
                .build();
        NavigationBar navBar = new NavigationBar
                .Builder("title")
                .leftButton(leftButton)
                .build();
        ErnNavRoute route = new ErnNavRoute
                .Builder("dummyRootComponent")
                .navigationBar(navBar)
                .build();
        EnNavigationApi
                .requests()
                .update(route, new ElectrodeBridgeResponseListener<None>() {
                    @Override
                    public void onSuccess(@Nullable None responseData) {
                        latch.countDown();
                    }

                    @Override
                    public void onFailure(@NonNull FailureMessage failureMessage) {
                        fail("Update request should not fail");
                    }
                });
        try {
            latch.await();
        } catch (InterruptedException e) {
            fail();
        }

        final CountDownLatch eventLatch = new CountDownLatch(1);
        UUID uuid = EnNavigationApi.events().addNavEventEventListener(new ElectrodeBridgeEventListener<NavEventData>() {
            @Override
            public void onEvent(@Nullable NavEventData eventPayload) {
                assertThat(eventPayload).isNotNull();
                if (eventPayload.getEventType().equals(NavEventType.BUTTON_CLICK.toString())) {
                    fail("Left button click is disabled, should not receive a call back");
                }
            }
        });
        onView(withContentDescription(R.string.abc_action_bar_up_description)).check(doesNotExist());
        //Click should do nothing since the back press is disabled
        scenario.onActivity(new ActivityScenario.ActivityAction<RootUpClickToRnActivity>() {
            @Override
            public void perform(RootUpClickToRnActivity activity) {
                activity.onBackPressed();
            }
        });

        try {
            eventLatch.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // Expected to timeout. Giving sometime for the event listener to get called if any.
            // In case if it gets called the test would fail
        }
        EnNavigationApi.events().removeNavEventEventListener(uuid);
    }

    @Test
    public void validateLeftButtonClickDisabledNavigatedComponent() {
        ActivityScenario<SampleActivity> scenario = ActivityScenario.launch(SampleActivity.class);
        final CountDownLatch latch = new CountDownLatch(1);
        final String LEFT_BUTTON_ID = "leftButtonId";
        final String NEW_PAGE_TITLE = "new page title";
        NavigationBarLeftButton leftButton = new NavigationBarLeftButton
                .Builder()
                .id(LEFT_BUTTON_ID)
                .disabled(true)
                .build();
        NavigationBar navBar = new NavigationBar
                .Builder(NEW_PAGE_TITLE)
                .leftButton(leftButton)
                .build();
        ErnNavRoute route = new ErnNavRoute
                .Builder("new page")
                .navigationBar(navBar)
                .build();
        EnNavigationApi
                .requests()
                .navigate(route, new ElectrodeBridgeResponseListener<None>() {
                    @Override
                    public void onSuccess(@Nullable None responseData) {
                        latch.countDown();
                    }

                    @Override
                    public void onFailure(@NonNull FailureMessage failureMessage) {
                        fail("Navigate request should not fail");
                    }
                });
        try {
            latch.await();
        } catch (InterruptedException e) {
            fail();
        }

        final CountDownLatch eventLatch = new CountDownLatch(1);
        UUID uuid = EnNavigationApi.events().addNavEventEventListener(new ElectrodeBridgeEventListener<NavEventData>() {
            @Override
            public void onEvent(@Nullable NavEventData eventPayload) {
                assertThat(eventPayload).isNotNull();
                if (eventPayload.getEventType().equals(NavEventType.BUTTON_CLICK.toString())) {
                    fail("Left button click is disabled, should not receive a call back");
                }
            }
        });
        onView(withContentDescription(R.string.abc_action_bar_up_description)).check(doesNotExist());
        //Click should do nothing since the back press is disabled
        scenario.onActivity(new ActivityScenario.ActivityAction<SampleActivity>() {
            @Override
            public void perform(SampleActivity activity) {
                assertThat(activity.getSupportActionBar()).isNotNull();
                assertThat(activity.getSupportActionBar().isShowing()).isTrue();
                assertThat(activity.getSupportActionBar().getTitle()).isEqualTo(NEW_PAGE_TITLE);
                activity.onBackPressed();
            }
        });

        try {
            eventLatch.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // Expected to timeout. Giving sometime for the event listener to get called if any.
            // In case if it gets called the test would fail
        }
        EnNavigationApi.events().removeNavEventEventListener(uuid);
    }

    /**
     * This method will repeatedly call navigate from page1 to n until the limit is met
     *
     * @param latch                {@link CountDownLatch} passed by the tests based on test criteria
     * @param currentCount         - Current page count
     * @param totalNavigationCount - Total navigation before breaking the chain
     */
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

