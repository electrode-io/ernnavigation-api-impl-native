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
    void onNavBarButtonClicked(@NonNull NavigationBarButton button, @NonNull MenuItem item);
}
