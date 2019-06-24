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

