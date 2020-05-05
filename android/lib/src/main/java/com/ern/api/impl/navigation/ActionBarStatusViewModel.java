package com.ern.api.impl.navigation;

import androidx.lifecycle.ViewModel;

/**
 * View model that needs to be used with the activity context to track the action bar status.
 * This is used for managing hiding and showing the action bar based on which side calls hide, Native or React Native.
 */
public class ActionBarStatusViewModel extends ViewModel {
    /**
     * Flag that will be set when a visible action bar is hid by a request from React Native.
     * If the action bar was already hidden from the native side this flag should not be set.
     */
    boolean hiddenByRn;
}
