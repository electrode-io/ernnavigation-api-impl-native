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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.URLUtil;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ernnavigationApi.ern.model.NavigationBar;
import com.ernnavigationApi.ern.model.NavigationBarButton;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;
import com.walmartlabs.ern.container.ElectrodeReactContainer;

import java.io.IOException;
import java.net.URL;

final class MenuUtil {

    private MenuUtil() {
    }

    private static final String TAG = MenuUtil.class.getSimpleName();

    static void updateMenuItems(@NonNull Menu menu, @NonNull NavigationBar navigationBar, @NonNull OnNavBarItemClickListener navBarButtonClickListener, @Nullable MenuItemDataProvider menuItemDataProvider, @NonNull Context context) {
        Logger.d(TAG, "Updating nav bar menu items");
        menu.clear();

        if (navigationBar.getButtons() == null || navigationBar.getButtons().size() == 0) {
            Logger.d(TAG, "No buttons found in the NavBar");
            return;
        }

        for (final NavigationBarButton button : navigationBar.getButtons()) {
            // button.getLocation() is now deprecated. The new implementation considers all buttons inside navigationBar.getButtons() as right buttons.
            // This condition is kept here to keep the backward compatibility.
            if ("left".equalsIgnoreCase(button.getLocation())) {
                continue;
            }
            addButtonAsMenuItem(button, menu, navBarButtonClickListener, menuItemDataProvider, context);
        }
    }

    private static MenuItem addButtonAsMenuItem(@NonNull NavigationBarButton button, @NonNull Menu menu, @NonNull final OnNavBarItemClickListener navBarButtonClickListener, @Nullable MenuItemDataProvider menuItemDataProvider, @NonNull Context context) {
        MenuItemProperties menuItemProperties = null;
        @DrawableRes int icon = Menu.NONE;
        @IdRes int itemId = Menu.NONE;

        if (menuItemDataProvider != null) {
            menuItemProperties = menuItemDataProvider.menuItemPropertiesFor(button);
            if (menuItemProperties != null) {
                icon = menuItemProperties.icon();
                itemId = menuItemProperties.itemId();
            }
        }

        MenuItem menuItem = menu.add(Menu.NONE, itemId, Menu.NONE, button.getTitle() != null ? button.getTitle() : button.getId());

        if (icon != Menu.NONE) {
            Logger.d(TAG, "setting native provided menu icon, ignoring icon passed inside NavigationBarButton");
            menuItem.setIcon(icon);
            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        } else if (button.getIcon() != null) {
            String iconLocation = button.getIcon();
            if (canLoadIconFromURI(iconLocation)) {
                try {
                    menuItem.setIcon(getBitmapFromURL(context, iconLocation));
                    menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                } catch (IOException e) {
                    Logger.w(TAG, "Load failed for icon from URL: " + iconLocation);
                }
            } else {
                icon = context.getResources().getIdentifier(iconLocation, "drawable", context.getPackageName());
                if (icon != Menu.NONE) {
                    menuItem.setIcon(icon);
                    menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                } else {
                    Logger.w(TAG, "Icon not found for button:%s", button.getId());
                }
            }
        }

        if (button.getDisabled() != null) {
            menuItem.setEnabled(!button.getDisabled());
        }

        if (menuItemProperties == null || !menuItemProperties.shouldHandleClickOnNative()) {
            registerItemClickListener(menuItem, button, navBarButtonClickListener);
        }

        return menuItem;
    }

    public static boolean canLoadIconFromURI(String icon) {
        return ElectrodeReactContainer.isReactNativeDeveloperSupport() && URLUtil.isValidUrl(icon) && Patterns.WEB_URL.matcher(icon).matches();
    }

    public static Drawable getBitmapFromURL(Context context, String iconLocation) throws IOException {
        Logger.d(TAG, "Attempting to load icon from URL: " + iconLocation);
        StrictMode.ThreadPolicy oldPolicy = StrictMode.getThreadPolicy();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(policy);
        URL iconUrl = new URL(iconLocation);
        Bitmap iconBitmap = BitmapFactory.decodeStream(iconUrl.openConnection().getInputStream());
        StrictMode.setThreadPolicy(oldPolicy);
        return new BitmapDrawable(context.getResources(), iconBitmap);
    }

    private static void registerItemClickListener(@NonNull final MenuItem menuItem, @NonNull final NavigationBarButton button, @NonNull final OnNavBarItemClickListener navBarButtonClickListener) {
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Logger.d(TAG, "Nav button clicked: %s", button);
                navBarButtonClickListener.onNavBarButtonClicked(button, item);
                return true;
            }
        });
    }
}
