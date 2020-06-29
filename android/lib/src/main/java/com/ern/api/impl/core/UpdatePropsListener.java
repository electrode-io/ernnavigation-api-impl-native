package com.ern.api.impl.core;

import android.os.Bundle;

import androidx.annotation.Nullable;

/**
 * Used for updating the props for a React Native view component.
 * This is mainly used while going back to a previous fragment that requires new props being passed to the previous fragment.
 * Refer {@link com.ern.api.impl.navigation.MiniAppNavigationFragment} for usage
 */
public interface UpdatePropsListener {
    void refresh(@Nullable Bundle data);
}
