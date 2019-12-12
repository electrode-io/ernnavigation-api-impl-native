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

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;

/**
 * Default implementation is here {@link DefaultMenuItemProperties}
 */
public interface MenuItemProperties {
    @IdRes
    int itemId();

    @DrawableRes
    int icon();

    /**
     * When set to true for a menu item ,the click will be delivered to {@link androidx.appcompat.app.AppCompatActivity#onOptionsItemSelected(MenuItem)}
     *
     * @return true | false
     */
    boolean shouldHandleClickOnNative();
}
