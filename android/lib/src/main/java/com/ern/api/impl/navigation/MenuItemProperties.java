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
    boolean isHandleClickInActivity();
}
