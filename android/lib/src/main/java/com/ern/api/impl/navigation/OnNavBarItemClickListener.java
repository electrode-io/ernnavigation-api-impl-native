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
