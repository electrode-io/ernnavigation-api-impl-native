package com.ern.api.impl.navigation;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.ern.api.impl.core.ElectrodeReactFragmentActivityDelegate;
import com.ernnavigationApi.ern.model.NavigationBar;

/**
 * @deprecated  This delegate is no longer in use.  Change to use {@link ElectrodeReactFragmentActivityDelegate}
 */
@Deprecated
public class ElectrodeReactNavigationActivityDelegate extends ElectrodeReactFragmentActivityDelegate {
    private static final String TAG = ElectrodeReactNavigationActivityDelegate.class.getSimpleName();

    public ElectrodeReactNavigationActivityDelegate(@NonNull FragmentActivity activity) {
        super(activity);
    }

    /**
     *
     * @param navigationBar
     * @param navBarButtonClickListener
     * @return
     * @deprecated No longer used, remove delegating this from your activity.
     */
    @Deprecated
    public boolean updateNavBar(@NonNull NavigationBar navigationBar, @NonNull final OnNavBarItemClickListener navBarButtonClickListener) {
        return true;
    }
}