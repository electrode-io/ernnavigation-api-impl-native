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
import androidx.annotation.Nullable;

import com.ern.api.impl.core.ElectrodeReactFragmentDelegate;
import com.ernnavigationApi.ern.model.NavigationBar;

import org.json.JSONObject;

/**
 * @deprecated use {@link ElectrodeNavigationActivityListener}
 */
@Deprecated
public interface MiniAppNavRequestListener extends ElectrodeReactFragmentDelegate.MiniAppRequestListener {
    /**
     * Use to delegate a navigate call to the parent activity.
     *
     * @param route
     * @return true | false
     */
    boolean navigate(Route route);

    /**
     * Use to communicate back to activity when a react native flow is completed.
     */
    void finishFlow(@Nullable JSONObject finalPayload);

    /**
     * Use to update the action bar title.
     *
     * @param navigationBar {@link NavigationBar}
     * @param navBarButtonClickListener {@link OnNavBarItemClickListener}
     */
    @Deprecated
    void updateNavBar(@NonNull NavigationBar navigationBar, @NonNull OnNavBarItemClickListener navBarButtonClickListener);

    /**
     * Use for navigating back to a MiniApp that was already rendered. Doing so will pop all the MiniApps in the stack till it finds a match.
     * <p>
     * If a null component name is received, pop the current fragment.
     * <p>
     * If there is only one fragment in the stack the activity will be finished.
     *
     * @param miniAppComponentName {@link String}
     * @return true | false
     */
    boolean backToMiniApp(@Nullable String miniAppComponentName);
}
