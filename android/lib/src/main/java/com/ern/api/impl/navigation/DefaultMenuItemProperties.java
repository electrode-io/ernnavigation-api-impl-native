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
    public boolean isHandleClickInActivity() {
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
