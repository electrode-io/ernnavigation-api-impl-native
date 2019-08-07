package com.walmartlabs.ern.container.plugins;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.ReactInstanceManagerBuilder;
import com.facebook.react.ReactPackage;

public class LinearGradientPlugin implements ReactPlugin {

    public ReactPackage hook(@NonNull Application application, @Nullable ReactPluginConfig config) {
        return null; //new LinearGradientPackage();
    }
}
