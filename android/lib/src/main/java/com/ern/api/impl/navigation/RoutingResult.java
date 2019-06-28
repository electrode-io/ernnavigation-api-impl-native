package com.ern.api.impl.navigation;

import androidx.annotation.Nullable;

public class RoutingResult {

    final boolean isComplete;
    final String message;

    RoutingResult(boolean isComplete, @Nullable String message) {
        this.isComplete = isComplete;
        this.message = message;
    }
}
