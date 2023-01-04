package com.ern.api.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.ern.api.impl.navigation.MiniAppNavigationFragment;
import com.ern.api.impl.navigation.NavigationLaunchConfig;
import com.ernnavigation.ern.api.EnNavigationApi;
import com.ernnavigation.ern.model.ErnNavRoute;
import com.ernnavigation.ern.model.NavigationBar;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeResponseListener;
import com.walmartlabs.electrode.reactnative.bridge.FailureMessage;
import com.walmartlabs.electrode.reactnative.bridge.None;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;

import static com.ern.api.impl.SampleActivity.ROOT_COMPONENT_NAME;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

/**
 * This class is a replica of {@link FragmentScopedNavigationApiTest} except the fact that
 * the navigation request handler is inside the activity instead of fragments.
 * <p>
 * see {@link TestActivity#createNavigationLaunchConfig()}
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ActivityScopedNavigationApiTest {

    private static final String TITLE_ROOT_PAGE = ApplicationProvider.getApplicationContext().getString(com.walmartlabs.ern.navigation.test.R.string.root_page_title);
    private static final String TITLE_PAGE_1 = "page 1";
    private static final String COMPONENT_PAGE_1 = "Component1";

    @Before
    public void setUp() {
        Logger.overrideLogLevel(Logger.LogLevel.VERBOSE);
    }

    @Rule
    public ActivityScenarioRule<TestActivity> rule = new ActivityScenarioRule<TestActivity>(TestActivity.class);

    @Test
    public void testRootComponentRendering() {
        ActivityScenario<TestActivity> scenario = rule.getScenario();
        scenario.onActivity(new ActivityScenario.ActivityAction<TestActivity>() {
            @Override
            public void perform(TestActivity activity) {
                assertThat(activity.getSupportActionBar()).isNotNull();
                assertThat(activity.getSupportActionBar().getTitle()).isEqualTo(TITLE_ROOT_PAGE);
                assertThat(activity.getSupportFragmentManager().getBackStackEntryCount()).isEqualTo(1);
                assertThat(activity.getSupportFragmentManager().getFragments().size()).isEqualTo(1);
                assertThat(activity.getSupportFragmentManager().getFragments().get(0)).isInstanceOf(MiniAppNavigationFragment.class);
                MiniAppNavigationFragment miniAppFragment = (MiniAppNavigationFragment) activity.getSupportFragmentManager().getFragments().get(0);
                assertThat(miniAppFragment.getReactComponentName()).isEqualTo(ROOT_COMPONENT_NAME);
            }
        });
    }

    @Test
    public void testNavigate() {
        final CountDownLatch latch = new CountDownLatch(1);
        ActivityScenario<TestActivity> scenario = rule.getScenario();
        // Navigate to second page and ensure that the nav bar title is updated.
        EnNavigationApi.requests().navigate(new ErnNavRoute.Builder(COMPONENT_PAGE_1).navigationBar(new NavigationBar.Builder(TITLE_PAGE_1).build()).build(), new ElectrodeBridgeResponseListener<None>() {
            @Override
            public void onSuccess(@Nullable None responseData) {
                latch.countDown();
            }

            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                fail(failureMessage.getMessage());
            }
        });

        // Wait for the navigation request to complete
        try {
            latch.await();
        } catch (InterruptedException e) {
            fail();
        }

        scenario.onActivity(new ActivityScenario.ActivityAction<TestActivity>() {
            @Override
            public void perform(TestActivity activity) {
                assertThat(activity.getSupportActionBar()).isNotNull();
                assertThat(activity.getSupportActionBar().getTitle()).isEqualTo(TITLE_PAGE_1);
                // Since we performed a navigation there should be two fragments in the stack, 1. Root component and 2. Navigated component
                assertThat(activity.getSupportFragmentManager().getBackStackEntryCount()).isEqualTo(2);
                assertThat(activity.getSupportFragmentManager().getFragments().size()).isEqualTo(1);
                assertThat(activity.getSupportFragmentManager().getFragments().get(0)).isInstanceOf(MiniAppNavigationFragment.class);
                assertThat(((MiniAppNavigationFragment) activity.getSupportFragmentManager().getFragments().get(0)).getReactComponentName()).isEqualTo(COMPONENT_PAGE_1);
                assertThat(activity.getSupportFragmentManager().getFragments().get(0)).isInstanceOf(MiniAppNavigationFragment.class);
            }
        });
    }

    @Test
    public void testBackgroundNavigate() {
        final CountDownLatch latch = new CountDownLatch(1);
        ActivityScenario<TestActivity> scenario = rule.getScenario();

        // Move to background
        scenario.moveToState(Lifecycle.State.CREATED);

        // Navigate to second page and ensure that the nav bar title is updated.
        EnNavigationApi.requests().navigate(new ErnNavRoute.Builder(COMPONENT_PAGE_1).navigationBar(new NavigationBar.Builder(TITLE_PAGE_1).build()).build(), new ElectrodeBridgeResponseListener<None>() {
            @Override
            public void onSuccess(@Nullable None responseData) {
                latch.countDown();
            }

            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                fail(failureMessage.getMessage());
            }
        });

        // Wait for the navigation request to complete
        try {
            latch.await();
        } catch (InterruptedException e) {
            fail();
        }

        // Ensure that the page is not navigated while backgrounded
        scenario.onActivity(new ActivityScenario.ActivityAction<TestActivity>() {
            @Override
            public void perform(TestActivity activity) {
                assertThat(activity.getSupportActionBar()).isNotNull();
                assertThat(activity.getSupportActionBar().getTitle()).isEqualTo(TITLE_ROOT_PAGE);
                assertThat(activity.isBackgrounded).isTrue();
            }
        });

        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(new ActivityScenario.ActivityAction<TestActivity>() {
            @Override
            public void perform(TestActivity activity) {
                assertThat(activity.isForegrounded).isTrue();
                assertThat(activity.getSupportActionBar()).isNotNull();
                assertThat(activity.getSupportActionBar().getTitle()).isEqualTo(TITLE_PAGE_1);
                //Since we performed a navigation there should be two fragments in the stack, 1. Root component and 2. Navigated component
                assertThat(activity.getSupportFragmentManager().getBackStackEntryCount()).isEqualTo(2);
                assertThat(activity.getSupportFragmentManager().getFragments().size()).isEqualTo(1);
                assertThat(activity.getSupportFragmentManager().getFragments().get(0)).isInstanceOf(MiniAppNavigationFragment.class);
                assertThat(((MiniAppNavigationFragment) activity.getSupportFragmentManager().getFragments().get(0)).getReactComponentName()).isEqualTo(COMPONENT_PAGE_1);
                assertThat(activity.getSupportFragmentManager().getFragments().get(0)).isInstanceOf(MiniAppNavigationFragment.class);
            }
        });
    }

    @Test
    public void testUpdate() {
        final String UPDATED_TITLE = "Page 0 - Updated";
        ActivityScenario<TestActivity> scenario = rule.getScenario();
        scenario.onActivity(new ActivityScenario.ActivityAction<TestActivity>() {
            @Override
            public void perform(TestActivity activity) {
                assertThat(activity.getSupportActionBar()).isNotNull();
                assertThat(activity.getSupportActionBar().getTitle()).isEqualTo(TITLE_ROOT_PAGE);
                assertThat(activity.getSupportFragmentManager().getBackStackEntryCount()).isEqualTo(1);
                assertThat(activity.getSupportFragmentManager().getFragments().size()).isEqualTo(1);
                assertThat(activity.getSupportFragmentManager().getFragments().get(0)).isInstanceOf(MiniAppNavigationFragment.class);
                MiniAppNavigationFragment miniAppFragment = (MiniAppNavigationFragment) activity.getSupportFragmentManager().getFragments().get(0);
                assertThat(miniAppFragment.getReactComponentName()).isEqualTo(ROOT_COMPONENT_NAME);
            }
        });

        final CountDownLatch latch = new CountDownLatch(1);

        EnNavigationApi.requests().update(new ErnNavRoute.Builder("blah").navigationBar(new NavigationBar.Builder(UPDATED_TITLE).build()).build(), new ElectrodeBridgeResponseListener<None>() {
            @Override
            public void onSuccess(@Nullable None responseData) {
                latch.countDown();
            }

            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                fail(failureMessage.getMessage());
            }
        });

        // Wait for the update request to complete
        try {
            latch.await();
        } catch (InterruptedException e) {
            fail();
        }

        scenario.onActivity(new ActivityScenario.ActivityAction<TestActivity>() {
            @Override
            public void perform(TestActivity activity) {
                assertThat(activity.getSupportActionBar()).isNotNull();
                assertThat(activity.getSupportActionBar().getTitle()).isEqualTo(UPDATED_TITLE);
                assertThat(activity.getSupportFragmentManager().getBackStackEntryCount()).isEqualTo(1);
                assertThat(activity.getSupportFragmentManager().getFragments().size()).isEqualTo(1);
                assertThat(activity.getSupportFragmentManager().getFragments().get(0)).isInstanceOf(MiniAppNavigationFragment.class);
                MiniAppNavigationFragment miniAppFragment = (MiniAppNavigationFragment) activity.getSupportFragmentManager().getFragments().get(0);
                assertThat(miniAppFragment.getReactComponentName()).isEqualTo(ROOT_COMPONENT_NAME);

                //TODO: Validate Menu Items.
            }
        });
    }

    @Test
    public void testBack() {
        final CountDownLatch latch = new CountDownLatch(1);
        ActivityScenario<TestActivity> scenario = rule.getScenario();
        // Navigate to second page and ensure that the nav bar title is updated.
        EnNavigationApi.requests().navigate(new ErnNavRoute.Builder(COMPONENT_PAGE_1).navigationBar(new NavigationBar.Builder(TITLE_PAGE_1).build()).build(), new ElectrodeBridgeResponseListener<None>() {
            @Override
            public void onSuccess(@Nullable None responseData) {
                latch.countDown();
            }

            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                fail(failureMessage.getMessage());
            }
        });

        // Wait for the navigation request to complete
        try {
            latch.await();
        } catch (InterruptedException e) {
            fail();
        }

        scenario.onActivity(new ActivityScenario.ActivityAction<TestActivity>() {
            @Override
            public void perform(TestActivity activity) {
                assertThat(activity.getSupportActionBar()).isNotNull();
                assertThat(activity.getSupportActionBar().getTitle()).isEqualTo(TITLE_PAGE_1);
                //Since we performed a navigation there should be two fragments in the stack, 1. Root component and 2. Navigated component
                assertThat(activity.getSupportFragmentManager().getBackStackEntryCount()).isEqualTo(2);
                assertThat(activity.getSupportFragmentManager().getFragments().size()).isEqualTo(1);
                assertThat(activity.getSupportFragmentManager().getFragments().get(0)).isInstanceOf(MiniAppNavigationFragment.class);
                assertThat(((MiniAppNavigationFragment) activity.getSupportFragmentManager().getFragments().get(0)).getReactComponentName()).isEqualTo(COMPONENT_PAGE_1);
                assertThat(activity.getSupportFragmentManager().getFragments().get(0)).isInstanceOf(MiniAppNavigationFragment.class);
            }
        });

        // Navigate back to FirstPage
        final CountDownLatch backLatch = new CountDownLatch(1);
        EnNavigationApi.requests().back(new ErnNavRoute.Builder(ROOT_COMPONENT_NAME).navigationBar(new NavigationBar.Builder(COMPONENT_PAGE_1).build()).build(), new ElectrodeBridgeResponseListener<None>() {
            @Override
            public void onSuccess(@Nullable None responseData) {
                backLatch.countDown();
            }

            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                fail(failureMessage.getMessage());
            }
        });

        // Wait for the back request to complete
        try {
            backLatch.await();
        } catch (InterruptedException e) {
            fail();
        }

        scenario.onActivity(new ActivityScenario.ActivityAction<TestActivity>() {
            @Override
            public void perform(TestActivity activity) {
                assertThat(activity.getSupportActionBar()).isNotNull();
                assertThat(activity.getSupportActionBar().getTitle()).isEqualTo(rootPageTitle());
                //Since we performed a navigation there should be two fragments in the stack, 1. Root component and 2. Navigated component
                assertThat(activity.getSupportFragmentManager().getBackStackEntryCount()).isEqualTo(1);
                assertThat(activity.getSupportFragmentManager().getFragments().size()).isEqualTo(1);
                assertThat(activity.getSupportFragmentManager().getFragments().get(0)).isInstanceOf(MiniAppNavigationFragment.class);
                assertThat(((MiniAppNavigationFragment) activity.getSupportFragmentManager().getFragments().get(0)).getReactComponentName()).isEqualTo(ROOT_COMPONENT_NAME);
                assertThat(activity.getSupportFragmentManager().getFragments().get(0)).isInstanceOf(MiniAppNavigationFragment.class);
            }
        });
    }

    @Test
    public void testBackWithSingleFragment() {
        final CountDownLatch latch = new CountDownLatch(1);
        ActivityScenario<SampleActivity> scenario = ActivityScenario.launch(SampleActivity.class);
        EnNavigationApi.requests().back(new ErnNavRoute.Builder(ROOT_COMPONENT_NAME).navigationBar(new NavigationBar.Builder(COMPONENT_PAGE_1).build()).build(), new ElectrodeBridgeResponseListener<None>() {
            @Override
            public void onSuccess(@Nullable None responseData) {
                latch.countDown();
            }

            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                fail(failureMessage.getMessage());
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            fail();
        }
        scenario.onActivity(new ActivityScenario.ActivityAction<SampleActivity>() {
            @Override
            public void perform(SampleActivity activity) {
                // Calling back on a single fragment activity finishes the activity.
                assertThat(activity.didFinish).isTrue();
            }
        });
    }

    @Test
    public void testDeviceBackPressedOnSingleFragment() {
        ActivityScenario<SampleActivity> scenario = ActivityScenario.launch(SampleActivity.class);
        scenario.onActivity(new ActivityScenario.ActivityAction<SampleActivity>() {
            @Override
            public void perform(SampleActivity activity) {
                activity.onBackPressed();
                // Calling back on a single fragment activity finishes the activity.
                assertThat(activity.didFinish).isTrue();
            }
        });
    }

    @Test
    public void testFinish() {
        final CountDownLatch latch = new CountDownLatch(1);
        ActivityScenario<TestActivity> scenario = rule.getScenario();
        EnNavigationApi.requests().finish(null, new ElectrodeBridgeResponseListener<None>() {
            @Override
            public void onSuccess(@Nullable None responseData) {
                latch.countDown();
            }

            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                fail(failureMessage.getMessage());
            }
        });

        // Wait for the finish request to complete
        try {
            latch.await();
        } catch (InterruptedException e) {
            fail();
        }

        scenario.onActivity(new ActivityScenario.ActivityAction<TestActivity>() {
            @Override
            public void perform(TestActivity activity) {
                assertThat(activity.didFinishFlow).isTrue();
                assertThat(activity.finishFlowPayload).isNull();
            }
        });
    }

    @Test
    public void testFinishWhileBackgrounded() {
        final CountDownLatch latch = new CountDownLatch(1);
        ActivityScenario<TestActivity> scenario = rule.getScenario();
        // Background activity
        scenario.moveToState(Lifecycle.State.CREATED);
        EnNavigationApi.requests().finish("{}", new ElectrodeBridgeResponseListener<None>() {
            @Override
            public void onSuccess(@Nullable None responseData) {
                latch.countDown();
            }

            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                fail(failureMessage.getMessage());
            }
        });

        // Wait for the finish request to complete
        try {
            latch.await();
        } catch (InterruptedException e) {
            fail();
        }

        // Foreground activity
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onActivity(new ActivityScenario.ActivityAction<TestActivity>() {
            @Override
            public void perform(TestActivity activity) {
                assertThat(activity.didFinishFlow).isTrue();
                assertThat(activity.finishFlowPayload).isNotNull();
            }
        });
    }

    @Test
    public void testBackgroundBackToApi() {
        final CountDownLatch latch = new CountDownLatch(1);
        ActivityScenario<TestActivity> scenario = rule.getScenario();

        // Navigate to second page and ensure that the nav bar title is updated.
        EnNavigationApi.requests().navigate(new ErnNavRoute.Builder(COMPONENT_PAGE_1).navigationBar(new NavigationBar.Builder(TITLE_PAGE_1).build()).build(), new ElectrodeBridgeResponseListener<None>() {
            @Override
            public void onSuccess(@Nullable None responseData) {
                latch.countDown();
            }

            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                fail(failureMessage.getMessage());
            }
        });

        //Wait for the navigation request to complete
        try {
            latch.await();
        } catch (InterruptedException e) {
            fail();
        }

        // Move to background
        scenario.moveToState(Lifecycle.State.CREATED);
        final CountDownLatch backToLatch = new CountDownLatch(1);

        EnNavigationApi.requests().back(new ErnNavRoute.Builder(ROOT_COMPONENT_NAME).build(), new ElectrodeBridgeResponseListener<None>() {
            @Override
            public void onSuccess(@Nullable None responseData) {
                backToLatch.countDown();
            }

            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                fail();
            }
        });

        // Wait for the backTo request to complete
        try {
            backToLatch.await();
        } catch (InterruptedException e) {
            fail();
        }

        // Resume the activity and ensure that the back action has been completed successfully
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(new ActivityScenario.ActivityAction<TestActivity>() {
            @Override
            public void perform(TestActivity activity) {
                assertThat(activity.isForegrounded).isTrue();
                assertThat(activity.getSupportActionBar()).isNotNull();
                assertThat(activity.getSupportActionBar().getTitle()).isEqualTo(TITLE_ROOT_PAGE);
                // Since we performed a back action there should only be one fragment left in the stack.
                assertThat(activity.getSupportFragmentManager().getBackStackEntryCount()).isEqualTo(1);
                assertThat(activity.getSupportFragmentManager().getFragments().size()).isEqualTo(1);
                assertThat(activity.getSupportFragmentManager().getFragments().get(0)).isInstanceOf(MiniAppNavigationFragment.class);
                assertThat(((MiniAppNavigationFragment) activity.getSupportFragmentManager().getFragments().get(0)).getReactComponentName()).isEqualTo(ROOT_COMPONENT_NAME);
                assertThat(activity.getSupportFragmentManager().getFragments().get(0)).isInstanceOf(MiniAppNavigationFragment.class);
            }
        });
    }

    private String rootPageTitle() {
        return ApplicationProvider.getApplicationContext().getResources().getString(com.walmartlabs.ern.navigation.test.R.string.root_page_title);
    }

    public static class TestActivity extends SampleActivity {
        @Override
        protected NavigationLaunchConfig createNavigationLaunchConfig() {
            NavigationLaunchConfig config = super.createNavigationLaunchConfig();
            config.useActivityScopeForNavigation(true, null);
            return config;
        }
    }
}
