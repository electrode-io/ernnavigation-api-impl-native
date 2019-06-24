package com.ern.api.impl.navigation;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.ern.api.impl.core.ElectrodeReactFragmentActivityDelegate;
import com.ernnavigationApi.ern.model.NavigationBar;
import com.ernnavigationApi.ern.model.NavigationBarButton;

public class ElectrodeReactNavigationActivityDelegate extends ElectrodeReactFragmentActivityDelegate {
    private static final String TAG = ElectrodeReactNavigationActivityDelegate.class.getSimpleName();

    private Menu mMenu;
    private MenuItemDataProvider menuItemDataProvider;

    public void setMenuItemDataProvider(MenuItemDataProvider menuItemDataProvider) {
        this.menuItemDataProvider = menuItemDataProvider;
    }

    public ElectrodeReactNavigationActivityDelegate(@NonNull FragmentActivity activity) {
        super(activity);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        return true;
    }


    public boolean updateNavBar(@NonNull NavigationBar navigationBar, @NonNull final OnNavBarItemClickListener navBarButtonClickListener) {
        if (mFragmentActivity instanceof AppCompatActivity) {
            updateTitle(navigationBar);
            updateMenuItems(navigationBar, navBarButtonClickListener);
            return true;
        }
        return false;
    }

    private void updateTitle(@NonNull NavigationBar navigationBar) {
        ActionBar actionBar;
        actionBar = ((AppCompatActivity) mFragmentActivity).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setTitle(navigationBar.getTitle());
        }
    }

    private void updateMenuItems(@NonNull NavigationBar navigationBar, @NonNull OnNavBarItemClickListener navBarButtonClickListener) {
        if (navigationBar.getButtons() != null && mMenu == null) {
            Log.w(TAG, "Don't have a action bar meny instance to add items. Ensure that the onCreateOptionsMenu() method call is delegated");
            return;
        }
        if (navigationBar.getButtons() != null && mMenu != null) {
            mMenu.clear();
            for (final NavigationBarButton button : navigationBar.getButtons()) {
                if ("right".equalsIgnoreCase(button.getLocation())) {
                    addButtonAsMenuItem(button, mMenu, navBarButtonClickListener);
                } else {
                    Log.w(TAG, "NavBarButton location type not supported yet: " + button.getLocation());
                }
            }
        }
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

        if (icon == Menu.NONE && button.getIcon() != null) {
            icon = mFragmentActivity.getResources().getIdentifier(button.getIcon(), "drawable", mFragmentActivity.getPackageName());
        }

        MenuItem menuItem = menu.add(Menu.NONE, itemId, Menu.NONE, button.getTitle() != null ? button.getTitle() : button.getId());
        if (icon != Menu.NONE) {
            menuItem.setIcon(icon);
            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
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

