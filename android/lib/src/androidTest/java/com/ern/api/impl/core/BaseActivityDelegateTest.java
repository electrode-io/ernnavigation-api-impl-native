package com.ern.api.impl.core;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.ern.api.impl.SampleActivity;
import com.ern.api.impl.navigation.ElectrodeNavigationActivityDelegate;
import com.ern.api.impl.navigation.MiniAppNavigationFragment;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertThat;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class BaseActivityDelegateTest {

    @Before
    public void setUp() {
        Logger.overrideLogLevel(Logger.LogLevel.VERBOSE);
    }

    @Test
    public void testOnBackPressed() {
        ActivityScenario<DelegateTestActivity> scenario = ActivityScenario.launch(DelegateTestActivity.class);
        scenario.onActivity(new ActivityScenario.ActivityAction<DelegateTestActivity>() {
            @Override
            public void perform(DelegateTestActivity activity) {
                assertThat(activity.navigationActivityDelegate.onBackPressed()).isTrue();
                assertThat(activity.didFinish).isTrue();
            }
        });
    }

    @Test
    public void testStartMiniAppWithFragmentOverride() {
        ActivityScenario<DelegateTestActivity> scenario = ActivityScenario.launch(DelegateTestActivity.class);
        scenario.onActivity(new ActivityScenario.ActivityAction<DelegateTestActivity>() {
            @Override
            public void perform(DelegateTestActivity activity) {
                assertThat(activity.getSupportFragmentManager().getFragments().size()).isEqualTo(1);
                assertThat(activity.getSupportFragmentManager().getFragments().get(0)).isInstanceOf(MiniAppNavigationFragment.class); // Fragment class for Root Component
                LaunchConfig config = new LaunchConfig();
                config.mFragmentClass = TestFragment.class;
                activity.navigationActivityDelegate.startMiniAppFragment("Dummy", config);
            }
        });

        scenario.onActivity(new ActivityScenario.ActivityAction<DelegateTestActivity>() {
            @Override
            public void perform(DelegateTestActivity activity) {
                assertThat(activity.getSupportFragmentManager().getFragments().size()).isEqualTo(1);
                assertThat(activity.getSupportFragmentManager().getFragments().get(0)).isInstanceOf(TestFragment.class);// Fragment class must change based on the LaunchConfig
            }
        });
    }

    @Test
    public void testStartMiniAppWithOutFragmentClass() {
        ActivityScenario<DelegateTestActivity> scenario = ActivityScenario.launch(DelegateTestActivity.class);
        scenario.onActivity(new ActivityScenario.ActivityAction<DelegateTestActivity>() {
            @Override
            public void perform(DelegateTestActivity activity) {
                assertThat(activity.getSupportFragmentManager().getFragments().size()).isEqualTo(1);
                assertThat(activity.getSupportFragmentManager().getFragments().get(0)).isInstanceOf(MiniAppNavigationFragment.class); // Fragment class for Root Component
                LaunchConfig config = new LaunchConfig();
                config.mFragmentClass = TestFragment.class;
                activity.navigationActivityDelegate.startMiniAppFragment("Dummy", config);
            }
        });

        scenario.onActivity(new ActivityScenario.ActivityAction<DelegateTestActivity>() {
            @Override
            public void perform(DelegateTestActivity activity) {
                assertThat(activity.getSupportFragmentManager().getFragments().size()).isEqualTo(1);
                assertThat(activity.getSupportFragmentManager().getFragments().get(0)).isInstanceOf(TestFragment.class);// Fragment class must change based on the LaunchConfig
            }
        });
    }

    @Test
    public void testStartDialogFragment() {
        ActivityScenario<DelegateTestActivity> scenario = ActivityScenario.launch(DelegateTestActivity.class);
        scenario.onActivity(new ActivityScenario.ActivityAction<DelegateTestActivity>() {
            @Override
            public void perform(DelegateTestActivity activity) {
                assertThat(activity.getSupportFragmentManager().getFragments().size()).isEqualTo(1);
                assertThat(activity.getSupportFragmentManager().getFragments().get(0)).isInstanceOf(MiniAppNavigationFragment.class); // Fragment class for Root Component
                LaunchConfig config = new LaunchConfig();
                config.mFragmentClass = TestDialogFragment.class;
                activity.navigationActivityDelegate.startMiniAppFragment("DummyDialog", config);
            }
        });

        scenario.onActivity(new ActivityScenario.ActivityAction<DelegateTestActivity>() {
            @Override
            public void perform(DelegateTestActivity activity) {
                // Dialog Fragment gets added to the not replace hence the size should be 2
                assertThat(activity.getSupportFragmentManager().getFragments().size()).isEqualTo(2);
                assertThat(activity.getSupportFragmentManager().getFragments().get(1)).isInstanceOf(TestDialogFragment.class);
            }
        });
    }

    public static class DelegateTestActivity extends SampleActivity {
        public ElectrodeNavigationActivityDelegate navigationActivityDelegate;

        @NonNull
        @Override
        protected ElectrodeNavigationActivityDelegate createElectrodeDelegate() {
            navigationActivityDelegate = super.createElectrodeDelegate();
            return navigationActivityDelegate;
        }
    }

    public static class TestFragment extends MiniAppNavigationFragment {

    }

    public static class TestDialogFragment extends DialogFragment {

    }
}
