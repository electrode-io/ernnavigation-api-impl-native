package com.ern.api.impl;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.ern.api.impl.navigation.ElectrodeBaseActivity;
import com.ern.api.impl.navigation.MiniAppNavigationFragment;
import com.ernnavigationApi.ern.api.EnNavigationApi;
import com.ernnavigationApi.ern.model.ErnNavRoute;
import com.ernnavigationApi.ern.model.NavigationBar;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeResponseListener;
import com.walmartlabs.electrode.reactnative.bridge.FailureMessage;
import com.walmartlabs.electrode.reactnative.bridge.None;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;
import com.walmartlabs.ern.container.ElectrodeReactContainer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import static com.ern.api.impl.BasicNavigationApiTest.SampleActivity.ROOT_COMPONENT_NAME;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class BasicNavigationApiTest {

    private static final String TITLE_ROOT_PAGE = ApplicationProvider.getApplicationContext().getString(com.walmartlabs.ern.navigation.test.R.string.root_page_title);
    private static final String TITLE_PAGE_1 = "page 1";
    private static final String COMPONENT_PAGE_1 = "Component1";

    @Before
    public void setUp() {
        Logger.overrideLogLevel(Logger.LogLevel.VERBOSE);
    }

    @Rule
    public ActivityScenarioRule<SampleActivity> rule = new ActivityScenarioRule<SampleActivity>(SampleActivity.class);

    @Test
    public void testRootComponentRendering() {
        assertNotNull(rule);
        ActivityScenario<SampleActivity> scenario = rule.getScenario();
        assertNotNull(scenario);

        scenario.onActivity(new ActivityScenario.ActivityAction<SampleActivity>() {
            @Override
            public void perform(SampleActivity activity) {
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
        assertNotNull(rule);
        ActivityScenario<SampleActivity> scenario = rule.getScenario();
        assertNotNull(scenario);
        //Navigate to second page and ensure that the nav bar title is updated.
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

        scenario.onActivity(new ActivityScenario.ActivityAction<SampleActivity>() {
            @Override
            public void perform(SampleActivity activity) {
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
    public void testBackgroundNavigate() {
        final CountDownLatch latch = new CountDownLatch(1);
        assertNotNull(rule);
        ActivityScenario<SampleActivity> scenario = rule.getScenario();
        assertNotNull(scenario);

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

        //Wait for the navigation request to complete
        try {
            latch.await();
        } catch (InterruptedException e) {
            fail();
        }

        //Ensure that the page is not navigated while backgrounded
        scenario.onActivity(new ActivityScenario.ActivityAction<SampleActivity>() {
            @Override
            public void perform(SampleActivity activity) {
                assertThat(activity.getSupportActionBar()).isNotNull();
                assertThat(activity.getSupportActionBar().getTitle()).isEqualTo(TITLE_ROOT_PAGE);
                assertThat(activity.isBackgrounded).isTrue();
            }
        });

        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(new ActivityScenario.ActivityAction<SampleActivity>() {
            @Override
            public void perform(SampleActivity activity) {
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
        assertNotNull(rule);
        ActivityScenario<SampleActivity> scenario = rule.getScenario();
        assertNotNull(scenario);

        scenario.onActivity(new ActivityScenario.ActivityAction<SampleActivity>() {
            @Override
            public void perform(SampleActivity activity) {
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

        //Wait for the update request to complete
        try {
            latch.await();
        } catch (InterruptedException e) {
            fail();
        }

        scenario.onActivity(new ActivityScenario.ActivityAction<SampleActivity>() {
            @Override
            public void perform(SampleActivity activity) {
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
        assertNotNull(rule);
        ActivityScenario<SampleActivity> scenario = rule.getScenario();
        assertNotNull(scenario);

        //Navigate to second page and ensure that the nav bar title is updated.
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

        scenario.onActivity(new ActivityScenario.ActivityAction<SampleActivity>() {
            @Override
            public void perform(SampleActivity activity) {
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

        //Navigate back to FirstPage
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

        //Wait for the back request to complete
        try {
            backLatch.await();
        } catch (InterruptedException e) {
            fail();
        }

        scenario.onActivity(new ActivityScenario.ActivityAction<SampleActivity>() {
            @Override
            public void perform(SampleActivity activity) {
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
    public void testFinish() {
        final CountDownLatch latch = new CountDownLatch(1);
        assertNotNull(rule);
        ActivityScenario<SampleActivity> scenario = rule.getScenario();
        assertNotNull(scenario);
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

        //Wait for the finish request to complete
        try {
            latch.await();
        } catch (InterruptedException e) {
            fail();
        }

        scenario.onActivity(new ActivityScenario.ActivityAction<SampleActivity>() {
            @Override
            public void perform(SampleActivity activity) {
                assertThat(activity.isFinishing()).isTrue();
            }
        });
    }

    private String rootPageTitle() {
        return ApplicationProvider.getApplicationContext().getResources().getString(com.walmartlabs.ern.navigation.test.R.string.root_page_title);
    }

    public static class SampleActivity extends ElectrodeBaseActivity {
        public static final String ROOT_COMPONENT_NAME = "Root";
        public boolean isBackgrounded;
        public boolean isForegrounded;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            ElectrodeReactContainer.initialize(getApplication(), new ElectrodeReactContainer.Config());
        }

        @Override
        protected void onResume() {
            super.onResume();
            isBackgrounded = false;
            isForegrounded = true;
        }

        @Override
        protected void onPause() {
            super.onPause();
            isBackgrounded = true;
            isForegrounded = false;
        }

        @Override
        protected int title() {
            return com.walmartlabs.ern.navigation.test.R.string.root_page_title;
        }

        @Override
        protected int mainLayout() {
            return com.walmartlabs.ern.navigation.test.R.layout.activity_sample_main;
        }

        @NonNull
        @Override
        protected String getRootComponentName() {
            return ROOT_COMPONENT_NAME;
        }

        @Override
        protected int getFragmentContainerId() {
            return com.walmartlabs.ern.navigation.test.R.id.sample_fragment_container;
        }

        @Override
        public View createReactNativeView(@NonNull String componentName, @Nullable Bundle props) {
            //Returns a dummy view.
            return ((LayoutInflater) Objects.requireNonNull(getSystemService(Context.LAYOUT_INFLATER_SERVICE))).inflate(com.walmartlabs.ern.navigation.test.R.layout.activity_sample_main, null);
        }

        @Override
        public boolean backToMiniApp(@Nullable String tag, @Nullable Bundle data) {
            //Set back the title when going back to root fragment
            if (ROOT_COMPONENT_NAME.equals(tag)) {
                Objects.requireNonNull(getSupportActionBar()).setTitle(getString(title()));
            }
            return super.backToMiniApp(tag, data);
        }
    }
}
