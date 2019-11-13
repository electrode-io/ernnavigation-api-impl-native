package com.ern.api.impl.navigation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ern.api.impl.core.ElectrodeReactFragmentActivityDelegate;

/**
 * Interface that exposes a way to provide more configuration while starting a MiniApp fragment.
 * @deprecated use {@link ElectrodeNavigationActivityListener}
 */
@Deprecated
public interface MiniAppNavConfigRequestListener extends MiniAppNavRequestListener {

    /**
     * starts a new fragment and inflate it with the given react component.
     *
     * @param componentName react view component name.
     * @param props         optional properties for the component rendering.
     */
    void startMiniAppFragment(@NonNull String componentName, @Nullable Bundle props, @NonNull ElectrodeReactFragmentActivityDelegate.StartMiniAppConfig startMiniAppConfig);
}
