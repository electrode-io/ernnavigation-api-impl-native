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

import android.view.Menu;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;

public class DefaultMenuItemProperties implements MenuItemProperties {

    @IdRes
    private final int itemId;

    @DrawableRes
    private final int icon;

    private final boolean handleClickInActivity;

    private DefaultMenuItemProperties(Builder builder) {
        this.itemId = builder.itemId;
        this.icon = builder.icon;
        this.handleClickInActivity = builder.handleClickInActivity;
    }

    @Override
    public int itemId() {
        return itemId;
    }

    @Override
    public int icon() {
        return icon;
    }

    @Override
    public boolean shouldHandleClickOnNative() {
        return handleClickInActivity;
    }

    public static class Builder {

        @IdRes
        private int itemId = Menu.NONE;

        @DrawableRes
        private int icon = Menu.NONE;

        private boolean handleClickInActivity = false;

        public Builder() {

        }

        public Builder itemId(@IdRes int itemId) {
            this.itemId = itemId;
            return this;
        }

        public Builder icon(@DrawableRes int icon) {
            this.icon = icon;
            return this;
        }

        public Builder handleClickInActivity(boolean handleClickInActivity) {
            this.handleClickInActivity = handleClickInActivity;
            return this;
        }

        public MenuItemProperties build() {
            return new DefaultMenuItemProperties(this);
        }
    }
}
