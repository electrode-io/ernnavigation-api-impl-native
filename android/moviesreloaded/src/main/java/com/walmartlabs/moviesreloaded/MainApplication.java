package com.walmartlabs.moviesreloaded;

import android.app.Application;

import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;
import com.walmartlabs.ern.container.ElectrodeReactContainer;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ElectrodeReactContainer.initialize(
                this,
                new ElectrodeReactContainer.Config().isReactNativeDeveloperSupport(true)
                /* Add your additional plugins configuration here */);
        if (BuildConfig.DEBUG) {
            Logger.overrideLogLevel(Logger.LogLevel.VERBOSE);
        }
    }
}
