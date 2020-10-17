package com.ern.api.impl;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.ern.api.impl.core.ElectrodeBaseFragmentDelegate;
import com.ern.api.impl.core.ElectrodeFragmentConfig;
import com.ern.api.impl.navigation.ElectrodeNavigationActivityListener;
import com.ern.api.impl.navigation.ElectrodeNavigationFragmentConfig;
import com.ern.api.impl.navigation.ElectrodeNavigationFragmentDelegate;
import com.ern.api.impl.navigation.MiniAppNavigationFragment;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class NavigationFragmentDelegateTest {

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
                assertThat(testFragment.getView().findViewById(com.walmartlabs.ern.navigation.test.R.id.miniapp_view_container)).isNotNull();
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
}
