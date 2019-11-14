package com.ern.api.impl.navigation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ern.api.impl.core.ElectrodeBaseFragmentDelegate;

import org.json.JSONObject;

public interface ElectrodeNavigationActivityListener extends ElectrodeBaseFragmentDelegate.ElectrodeActivityListener {
    /**
     * Use to delegate a navigate call to the parent activity.
     *
     * @param pageName {@link String} MiniApp view component name or the next page to be navigated to.
     * @param data     {@link Bundle} Data associated with this navigation.
     * @return true | false
     */
    boolean navigate(@NonNull String pageName, @NonNull Bundle data);

    /**
     * Use to communicate back to activity when a React Native flow is completed.
     */
    void finishFlow(@Nullable JSONObject finalPayload);

    /**
     * Use for navigating back to a MiniApp that was already rendered. Doing so will pop all the MiniApps in the stack till it finds a match.
     * <p>
     * If a null component name is received, pop the current fragment.
     * <p>
     * If there is only one fragment in the stack the activity will be finished.
     *
     * @param miniAppComponentName {@link String}
     * @param data                 {@link Bundle}
     * @return true | false
     */
    boolean backToMiniApp(@Nullable String miniAppComponentName, @Nullable Bundle data);
}
