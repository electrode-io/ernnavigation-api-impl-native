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

import com.ernnavigationApi.ern.model.NavigationBarButton;

/**
 * Interface that helps native to override the menuItem properties.
 */
public interface MenuItemDataProvider {

    /**
     * Use when a menu item need an override from native. When react native sends a NavigationBar native has an opportunity to provide the id and drawable resource from native side.
     *
     * @param navigationBarButton {@link NavigationBarButton} ()}
     * @return MenuItemProperties
     */
    @Nullable
    MenuItemProperties menuItemPropertiesFor(@NonNull NavigationBarButton navigationBarButton);
}

