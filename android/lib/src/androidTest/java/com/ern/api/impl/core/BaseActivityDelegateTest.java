package com.ern.api.impl.core;

import androidx.annotation.NonNull;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.ern.api.impl.SampleActivity;
import com.ern.api.impl.navigation.ElectrodeNavigationActivityDelegate;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertThat;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class BaseActivityDelegateTest {

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

    public static class DelegateTestActivity extends SampleActivity {
        public ElectrodeNavigationActivityDelegate navigationActivityDelegate;

        @NonNull
        @Override
        protected ElectrodeNavigationActivityDelegate createElectrodeDelegate() {
            navigationActivityDelegate = super.createElectrodeDelegate();
            return navigationActivityDelegate;
        }
    }
}
