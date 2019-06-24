package com.ern.api.impl.navigation;

import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.ernnavigationApi.ern.model.NavigationBarButton;

public interface OnNavBarItemClickListener {
    void onNavBarButtonClicked(@NonNull NavigationBarButton button, @NonNull MenuItem item);
}
