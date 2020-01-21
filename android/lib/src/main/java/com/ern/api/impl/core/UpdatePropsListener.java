package com.ern.api.impl.core;

import android.os.Bundle;

import androidx.annotation.Nullable;

/**
 * Used for updating the props for a React Native view component
 * Refer {@link com.ern.api.impl.navigation.MiniAppNavigationFragment} for usage
 */
public interface UpdatePropsListener {
    void refresh(@Nullable Bundle data);
}
