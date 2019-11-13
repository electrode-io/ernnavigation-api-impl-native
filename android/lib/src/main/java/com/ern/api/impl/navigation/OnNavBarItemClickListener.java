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

import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.ernnavigationApi.ern.model.NavigationBarButton;

/**
 * Click listener that a fragment can implement if it want to handle a action bar menu item click inside a fragment.
 */
public interface OnNavBarItemClickListener {
    /**
     * @param button {@link NavigationBarButton} button set by React Native component.
     * @param item   {@link MenuItem}
     * @return true if the button click was handled for this button, false otherwise.
     * Returning false will result in sending a notification back to the React Native component's Component.onNavButtonPress(buttonId)
     */
    boolean onNavBarButtonClicked(@NonNull NavigationBarButton button, @NonNull MenuItem item);
}
