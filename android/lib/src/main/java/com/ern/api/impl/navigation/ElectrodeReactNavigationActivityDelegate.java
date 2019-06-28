package com.ern.api.impl.navigation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.StrictMode;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.URLUtil;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.ern.api.impl.core.ElectrodeReactFragmentActivityDelegate;
import com.ernnavigationApi.ern.model.NavigationBar;
import com.ernnavigationApi.ern.model.NavigationBarButton;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;
import com.walmartlabs.ern.container.ElectrodeReactContainer;

import java.io.IOException;
import java.net.URL;

public class ElectrodeReactNavigationActivityDelegate extends ElectrodeReactFragmentActivityDelegate {
    private static final String TAG = ElectrodeReactNavigationActivityDelegate.class.getSimpleName();

    private MenuItemDataProvider menuItemDataProvider;

    public void setMenuItemDataProvider(MenuItemDataProvider menuItemDataProvider) {
        this.menuItemDataProvider = menuItemDataProvider;
    }

    public ElectrodeReactNavigationActivityDelegate(@NonNull FragmentActivity activity) {
        super(activity);
    }

    public boolean updateNavBar(@NonNull NavigationBar navigationBar, @NonNull final OnNavBarItemClickListener navBarButtonClickListener) {
        Logger.d(TAG, "updateNavBar: %s", navigationBar);
        updateTitle(navigationBar);
        updateMenuItems(navigationBar, navBarButtonClickListener);
        return true;
    }

    private void updateTitle(@NonNull NavigationBar navigationBar) {
        if (mFragmentActivity instanceof AppCompatActivity) {
            ActionBar actionBar;
            actionBar = ((AppCompatActivity) mFragmentActivity).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle("");
                actionBar.setTitle(navigationBar.getTitle());
            }
        } else {
            android.app.ActionBar actionBar = mFragmentActivity.getActionBar();
            if (actionBar != null) {
                actionBar.setTitle("");
                actionBar.setTitle(navigationBar.getTitle());
            }
        }
    }

    private void updateMenuItems(@NonNull NavigationBar navigationBar, @NonNull OnNavBarItemClickListener navBarButtonClickListener) {
        if (mMenu == null) {
            Logger.w(TAG, "Don't have a action bar menu instance to add items. Ensure that the onCreateOptionsMenu() method call is delegated");
            return;
        }
        mMenu.clear();

        if (navigationBar.getButtons() == null || navigationBar.getButtons().size() == 0) {
            Logger.d(TAG, "No buttons found in the NavBar");
            return;
        }

        for (final NavigationBarButton button : navigationBar.getButtons()) {
            if ("right".equalsIgnoreCase(button.getLocation())) {
                addButtonAsMenuItem(button, mMenu, navBarButtonClickListener);
            } else {
                Logger.w(TAG, "NavBarButton location type not supported yet: " + button.getLocation());
            }
        }
    }

    private boolean canLoadIconFromURI(String icon) {
        return ElectrodeReactContainer.isReactNativeDeveloperSupport() && URLUtil.isValidUrl(icon) && Patterns.WEB_URL.matcher(icon).matches();
    }

    private MenuItem addButtonAsMenuItem(@NonNull NavigationBarButton button, @NonNull Menu menu, @NonNull final OnNavBarItemClickListener navBarButtonClickListener) {
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

        if (icon == Menu.NONE && button.getIcon() != null) {
            String iconLocation = button.getIcon();

            if (canLoadIconFromURI(iconLocation)) {
                try {
                    Logger.d(TAG, "Attempting to load icon from URL: " + iconLocation);
                    StrictMode.ThreadPolicy oldPolicy = StrictMode.getThreadPolicy();
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
                    StrictMode.setThreadPolicy(policy);
                    URL iconUrl = new URL(iconLocation);
                    Bitmap iconBitmap = BitmapFactory.decodeStream(iconUrl.openConnection().getInputStream());
                    menuItem.setIcon(new BitmapDrawable(mFragmentActivity.getResources(), iconBitmap));
                    menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                    StrictMode.setThreadPolicy(oldPolicy);
                } catch (IOException e) {
                    Logger.w(TAG, "Load failed for icon from URL: " + iconLocation);
                }
            } else {
                icon = mFragmentActivity.getResources().getIdentifier(iconLocation, "drawable", mFragmentActivity.getPackageName());
                if (icon != Menu.NONE) {
                    menuItem.setIcon(icon);
                    menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                } else {
                    Logger.i(TAG, "Icon not found for button:%s", button.getId());
                }
            }
        }

        if (button.getDisabled() != null) {
            menuItem.setEnabled(!button.getDisabled());
        }

        if (menuItemProperties == null || !menuItemProperties.isHandleClickInActivity()) {
            registerItemClickListener(menuItem, button, navBarButtonClickListener);
        }

        return menuItem;
    }

    private void registerItemClickListener(@NonNull final MenuItem menuItem, @NonNull final NavigationBarButton button, @NonNull final OnNavBarItemClickListener navBarButtonClickListener) {
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                navBarButtonClickListener.onNavBarButtonClicked(button, item);
                return true;
            }
        });
    }


    @Override
    public void onDestroy() {
        mMenu.clear();
        mMenu = null;
        super.onDestroy();
    }
}