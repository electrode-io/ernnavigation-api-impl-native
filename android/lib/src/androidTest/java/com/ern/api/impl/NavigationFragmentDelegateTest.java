package com.ern.api.impl;

import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.ern.api.impl.core.ActivityDelegateConstants;
import com.ern.api.impl.core.ElectrodeBaseFragmentDelegate;
import com.ern.api.impl.core.ElectrodeFragmentConfig;
import com.ern.api.impl.navigation.ElectrodeNavigationActivityListener;
import com.ern.api.impl.navigation.ElectrodeNavigationFragmentConfig;
import com.ern.api.impl.navigation.ElectrodeNavigationFragmentDelegate;
import com.ern.api.impl.navigation.MenuItemProperties;
import com.ern.api.impl.navigation.MiniAppNavigationFragment;
import com.ern.api.impl.navigation.NavEventType;
import com.ernnavigationApi.ern.api.EnNavigationApi;
import com.ernnavigationApi.ern.model.ErnNavRoute;
import com.ernnavigationApi.ern.model.NavEventData;
import com.ernnavigationApi.ern.model.NavigationBar;
import com.ernnavigationApi.ern.model.NavigationBarButton;
import com.ernnavigationApi.ern.model.NavigationBarLeftButton;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeEventListener;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeResponseListener;
import com.walmartlabs.electrode.reactnative.bridge.FailureMessage;
import com.walmartlabs.electrode.reactnative.bridge.None;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;
import com.walmartlabs.ern.container.ElectrodeReactContainer;
import com.walmartlabs.ern.navigation.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.ern.api.impl.navigation.NavEventType.APP_DATA;
import static com.ern.api.impl.navigation.NavEventType.DID_BLUR;
import static com.ern.api.impl.navigation.NavEventType.DID_FOCUS;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class NavigationFragmentDelegateTest {

    @Before
    public void setup() {
        Logger.overrideLogLevel(Logger.LogLevel.VERBOSE);
    }

    @Test
    public void testFragmentLayoutIsUsedWhenSpecifiedInFragmentConfig() {
        ActivityScenario<TestActivity> scenario = ActivityScenario.launch(TestActivity.class);
        scenario.onActivity(new ActivityScenario.ActivityAction<TestActivity>() {
            @Override
            public void perform(TestActivity activity) {
                assertThat(activity.getSupportFragmentManager().getFragments().size()).isEqualTo(1);
                Fragment fragment = activity.getSupportFragmentManager().getFragments().get(0);
                assertThat(fragment).isInstanceOf(LayoutConfiguredFragment.class);
                LayoutConfiguredFragment testFragment = (LayoutConfiguredFragment) fragment;
                assertThat(testFragment.getView()).isNotNull();
                assertThat((LinearLayout) testFragment.getView().findViewById(com.walmartlabs.ern.navigation.test.R.id.miniapp_view_container)).isNotNull();
            }
        });
    }

    public static class LayoutConfiguredFragment extends MiniAppNavigationFragment {
        @NonNull
        @Override
        protected ElectrodeNavigationFragmentDelegate<ElectrodeNavigationActivityListener, ElectrodeNavigationFragmentConfig> createFragmentDelegate() {
            ElectrodeNavigationFragmentConfig config = new ElectrodeNavigationFragmentConfig();
            config.setFragmentLayoutId(com.walmartlabs.ern.navigation.test.R.layout.fragment_test_layout);
            config.setReactViewContainerId(com.walmartlabs.ern.navigation.test.R.id.miniapp_view_container);
            return new ElectrodeNavigationFragmentDelegate<>(this, config);
        }
    }

    public static class TestActivity extends SampleActivity {
        @NonNull
        @Override
        protected Class<? extends Fragment> miniAppFragmentClass() {
            return LayoutConfiguredFragment.class;
        }
    }

    @Test
    public void testEmptyOrNullComponentNameInOnCreateView() {
        Fragment fragment = mock(MiniAppNavigationFragment.class);
        ElectrodeBaseFragmentDelegate<ElectrodeBaseFragmentDelegate.ElectrodeActivityListener, ElectrodeFragmentConfig> delegate = new ElectrodeBaseFragmentDelegate<>(fragment);
        delegate.electrodeActivityListener = mock(ElectrodeBaseFragmentDelegate.ElectrodeActivityListener.class);
        try {
            delegate.onCreateView(null, null, null);
            fail("Should throw exception before reaching here");
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("Should never reach here. onCreateView() should return a non-null view.");
        }
    }

    @Test
    public void testOnEmitDataWithPayload() {
        final CountDownLatch latch = new CountDownLatch(1);
        Bundle args = new Bundle();
        args.putBoolean(ActivityDelegateConstants.KEY_REGISTER_NAV_VIEW_MODEL, true);
        initReact();

        FragmentScenario<LayoutConfiguredFragment> scenario = FragmentScenario.launch(LayoutConfiguredFragment.class, args);
        scenario.onFragment(new FragmentScenario.FragmentAction<LayoutConfiguredFragment>() {
            @Override
            public void perform(@NonNull final LayoutConfiguredFragment fragment) {
                final JSONObject passedPayload = new JSONObject();
                try {
                    passedPayload.put("testKey", "testValue");
                } catch (JSONException e) {
                    fail("Should not fail create json");
                }
                EnNavigationApi.events().addNavEventEventListener(new ElectrodeBridgeEventListener<NavEventData>() {
                    @Override
                    public void onEvent(@Nullable NavEventData eventPayload) {
                        assertThat(eventPayload).isNotNull();
                        //Only validate APP_DATA event
                        if (eventPayload.getEventType().equals(APP_DATA.toString())) {
                            assertThat(fragment.getArguments()).isNotNull();
                            assertThat(eventPayload.getViewId()).isEqualTo(fragment.getMiniAppViewIdentifier());
                            assertThat(eventPayload.getJsonPayload()).isNotNull();
                            try {
                                JSONObject receivedPayload = new JSONObject(eventPayload.getJsonPayload());
                                assertThat(receivedPayload.getString("testKey")).isEqualTo(passedPayload.getString("testKey"));
                            } catch (JSONException e) {
                                fail("Expected a valid JSON string");
                            }
                            latch.countDown();
                        }
                    }
                });
                ElectrodeNavigationFragmentDelegate<ElectrodeNavigationActivityListener, ElectrodeNavigationFragmentConfig> delegate = new ElectrodeNavigationFragmentDelegate<>(fragment);
                delegate.emitOnAppData(passedPayload);
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            fail("Should not time out");
        }
    }

    @Test
    public void testOnEmitDataWithNullPayload() {
        final CountDownLatch latch = new CountDownLatch(1);
        Bundle args = new Bundle();
        args.putBoolean(ActivityDelegateConstants.KEY_REGISTER_NAV_VIEW_MODEL, true);
        final String viewId = UUID.randomUUID().toString();
        args.putString(ElectrodeBaseFragmentDelegate.KEY_UNIQUE_VIEW_IDENTIFIER, viewId);
        initReact();

        FragmentScenario<LayoutConfiguredFragment> scenario = FragmentScenario.launch(LayoutConfiguredFragment.class, args);
        scenario.onFragment(new FragmentScenario.FragmentAction<LayoutConfiguredFragment>() {
            @Override
            public void perform(@NonNull final LayoutConfiguredFragment fragment) {
                EnNavigationApi.events().addNavEventEventListener(new ElectrodeBridgeEventListener<NavEventData>() {
                    @Override
                    public void onEvent(@Nullable NavEventData eventPayload) {
                        assertThat(eventPayload).isNotNull();
                        assertThat(eventPayload.getViewId()).isNotNull();
                        //Only validate APP_DATA event
                        if (eventPayload.getEventType().equals(APP_DATA.toString()) && eventPayload.getViewId().equals(viewId)) {
                            assertThat(eventPayload.getJsonPayload()).isNull();
                            latch.countDown();
                        }
                    }
                });
                ElectrodeNavigationFragmentDelegate<ElectrodeNavigationActivityListener, ElectrodeNavigationFragmentConfig> delegate = new ElectrodeNavigationFragmentDelegate<>(fragment);
                delegate.emitOnAppData(null);
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            fail("Should not time out");
        }
    }

    private void initReact() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ElectrodeReactContainer.initialize((Application) ApplicationProvider.getApplicationContext(), new ElectrodeReactContainer.Config());
            }
        });
    }

    @Test
    public void testBlurAndFocusEvents() {
        final CountDownLatch latch = new CountDownLatch(2);
        Bundle args = new Bundle();
        args.putBoolean(ActivityDelegateConstants.KEY_REGISTER_NAV_VIEW_MODEL, true);
        initReact();

        EnNavigationApi.events().addNavEventEventListener(new ElectrodeBridgeEventListener<NavEventData>() {
            @Override
            public void onEvent(@Nullable NavEventData eventPayload) {
                assertThat(eventPayload).isNotNull();
                if (eventPayload.getEventType().equals(DID_FOCUS.toString()) || eventPayload.getEventType().equals(DID_BLUR.toString())) {
                    latch.countDown();
                }
            }
        });
        FragmentScenario<LayoutConfiguredFragment> scenario = FragmentScenario.launch(LayoutConfiguredFragment.class, args);
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.moveToState(Lifecycle.State.DESTROYED);
        try {
            latch.await();
        } catch (InterruptedException e) {
            fail("Should not time out");
        }
    }

    @Test
    public void testLeftIconOverride() {
        NavigationBarLeftButton leftButton = new NavigationBarLeftButton
                .Builder()
                .id("leftButtonId")
                .icon("ic_refresh")
                .build();
        NavigationBar navBar = new NavigationBar
                .Builder("page 1")
                .leftButton(leftButton)
                .build();
        ErnNavRoute route = new ErnNavRoute
                .Builder("new page")
                .navigationBar(navBar)
                .build();
        ActivityScenario<SampleActivity> scenario = ActivityScenario.launch(SampleActivity.class);
        final CountDownLatch latch = new CountDownLatch(1);
        EnNavigationApi.requests().navigate(route, new ElectrodeBridgeResponseListener<None>() {
            @Override
            public void onSuccess(@Nullable None responseData) {
                latch.countDown();
            }

            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                fail();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            fail();
        }

        onView(withContentDescription(R.string.abc_action_bar_up_description)).check(new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                assertThat(view).isNotNull();
            }
        });
    }

    @Test
    public void testOverlay() {
        final String TITLE_PAGE_1 = "page 1";
        final String TITLE_PAGE_2 = "page 2";
        ActivityScenario<SampleActivity> scenario = ActivityScenario.launch(SampleActivity.class);
        final CountDownLatch latch = new CountDownLatch(1);
        EnNavigationApi.requests().navigate(new ErnNavRoute
                .Builder("new page")
                .navigationBar(new NavigationBar.Builder(TITLE_PAGE_1).build())
                .overlay(true)
                .build(), new ElectrodeBridgeResponseListener<None>() {
            @Override
            public void onSuccess(@Nullable None responseData) {
                latch.countDown();
            }

            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                fail();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            fail();
        }

        //Ensure Overlay is displayed with proper title.
        scenario.onActivity(new ActivityScenario.ActivityAction<SampleActivity>() {
            @Override
            public void perform(SampleActivity activity) {
                assertThat(activity.getSupportActionBar()).isNotNull();
                assertThat(activity.getSupportActionBar().getTitle()).isEqualTo(TITLE_PAGE_1);
            }
        });

        final CountDownLatch latch2 = new CountDownLatch(1);
        //Try to navigate one more time while overlay is displayed.
        EnNavigationApi.requests().navigate(new ErnNavRoute
                .Builder("another page")
                .navigationBar(new NavigationBar.Builder(TITLE_PAGE_2).build())
                .overlay(true)
                .build(), new ElectrodeBridgeResponseListener<None>() {
            @Override
            public void onSuccess(@Nullable None responseData) {
                latch2.countDown();
            }

            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                fail();
            }
        });

        try {
            latch2.await();
        } catch (InterruptedException e) {
            fail();
        }

        //Ensure the navigation succeeded while on an overlay page
        scenario.onActivity(new ActivityScenario.ActivityAction<SampleActivity>() {
            @Override
            public void perform(SampleActivity activity) {
                assertThat(activity.getSupportActionBar()).isNotNull();
                assertThat(activity.getSupportActionBar().getTitle()).isEqualTo(TITLE_PAGE_2);
            }
        });
    }

    public static class MenuItemsActivity extends SampleActivity {
        @NonNull
        @Override
        protected Class<? extends Fragment> miniAppFragmentClass() {
            return MenuItemsFragment.class;
        }
    }

    public static class MenuItemsFragment extends MiniAppNavigationFragment {

        @Override
        public void onOptionsMenuUpdated(@NonNull Menu menu, @NonNull MenuInflater inflater) {
            super.onOptionsMenuUpdated(menu, inflater);
        }

        @NonNull
        @Override
        protected ElectrodeNavigationFragmentDelegate<ElectrodeNavigationActivityListener, ElectrodeNavigationFragmentConfig> createFragmentDelegate() {
            ElectrodeNavigationFragmentDelegate<ElectrodeNavigationActivityListener, ElectrodeNavigationFragmentConfig> delegate = super.createFragmentDelegate();
            delegate.setMenuItemDataProvider(new MenuItemDataProvider());
            return delegate;
        }

        public static class MenuItemDataProvider implements com.ern.api.impl.navigation.MenuItemDataProvider {
            @Nullable
            @Override
            public MenuItemProperties menuItemPropertiesFor(@NonNull NavigationBarButton rightButton) {
                if (rightButton.getId().equals(RIGHT_BUTTON_ID_1)) {
                    return getMenuItemPropertiesFor(com.walmartlabs.ern.navigation.test.R.id.r_button_1);
                } else if (rightButton.getId().equals(RIGHT_BUTTON_ID_2)) {
                    return getMenuItemPropertiesFor(com.walmartlabs.ern.navigation.test.R.id.r_button_2);
                } else if (rightButton.getId().equals(RIGHT_BUTTON_ID_3)) {
                    return getMenuItemPropertiesFor(com.walmartlabs.ern.navigation.test.R.id.r_button_3);
                } else {
                    return null;
                }
            }

            @NonNull
            private MenuItemProperties getMenuItemPropertiesFor(final int itemId) {
                return new MenuItemProperties() {
                    @Override
                    public int itemId() {
                        return itemId;
                    }

                    @Override
                    public int icon() {
                        return 0;
                    }

                    @Override
                    public boolean shouldHandleClickOnNative() {
                        return false;
                    }
                };
            }

            @Override
            public int homeAsUpIndicatorOverride(@NonNull String iconName) {
                return 0;
            }
        }
    }

    private static final String RIGHT_BUTTON_ID_1 = "id_right1";
    private static final String RIGHT_BUTTON_ID_2 = "id_right2";
    private static final String RIGHT_BUTTON_ID_3 = "id_right3";

    @Test
    public void testRightMenuItemsRenderingAndClicks() {
        ActivityScenario<MenuItemsActivity> scenario = ActivityScenario.launch(MenuItemsActivity.class);
        final CountDownLatch latch = new CountDownLatch(1);
        final NavigationBarButton rightButton1 = new NavigationBarButton.Builder(RIGHT_BUTTON_ID_1).icon("ic_refresh").adaLabel("right button 1").build();
        final NavigationBarButton rightButton2 = new NavigationBarButton.Builder(RIGHT_BUTTON_ID_2).icon("ic_refresh").adaLabel("right button 2").build();
        // Disabled button
        final NavigationBarButton rightButton3 = new NavigationBarButton.Builder(RIGHT_BUTTON_ID_3).icon("ic_refresh").adaLabel("right button 3").disabled(true).build();
        NavigationBar navigationBar = new NavigationBar
                .Builder("page 1")
                .buttons(new ArrayList<NavigationBarButton>() {{
                    add(rightButton1);
                    add(rightButton2);
                    add(rightButton3);
                }})
                .build();
        EnNavigationApi.requests().navigate(new ErnNavRoute
                .Builder("page1component")
                .navigationBar(navigationBar)
                .overlay(true)
                .build(), new ElectrodeBridgeResponseListener<None>() {
            @Override
            public void onSuccess(@Nullable None responseData) {
                latch.countDown();
            }

            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                fail();
            }
        });

        final CountDownLatch eventLatch = new CountDownLatch(2);
        EnNavigationApi.events().addNavEventEventListener(new ElectrodeBridgeEventListener<NavEventData>() {
            @Override
            public void onEvent(@Nullable NavEventData eventPayload) {
                assertThat(eventPayload).isNotNull();
                if (eventPayload.getEventType().equals(NavEventType.BUTTON_CLICK.toString())) {
                    assertThat(eventPayload.getJsonPayload()).isNotNull();
                    eventLatch.countDown();
                }
            }
        });

        onView(withId(com.walmartlabs.ern.navigation.test.R.id.r_button_1)).perform(click());
        onView(withId(com.walmartlabs.ern.navigation.test.R.id.r_button_2)).perform(click());
        onView(withId(com.walmartlabs.ern.navigation.test.R.id.r_button_3)).check(new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                assertThat(view.isEnabled()).isFalse();
            }
        });
        try {
            eventLatch.await();
        } catch (InterruptedException e) {
            fail();
        }
    }
}
