/*
 * Copyright 2019 Walmart Labs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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