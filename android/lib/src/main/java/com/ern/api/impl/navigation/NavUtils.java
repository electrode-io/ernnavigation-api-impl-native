package com.ern.api.impl.navigation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ernnavigationApi.ern.model.ErnNavRoute;
import com.ernnavigationApi.ern.model.NavigationBar;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;

import org.json.JSONException;
import org.json.JSONObject;

public final class NavUtils {

    private static final String TAG = NavUtils.class.getSimpleName();

    private NavUtils() {

    }

    /**
     * Extracts the value for the key 'path' from the given bundle. Return null if the key is not present or if the value is not of {@link String} type..
     *
     * @param args {@link Bundle} Generally a bundle representation of {@link ErnNavRoute}
     * @return String
     */
    @Nullable
    public static String getPath(@NonNull Bundle args) {
        if (args.containsKey("path")) {
            Object obj = args.get("path");
            if (obj instanceof String) {
                return (String) obj;
            }
        }
        return null;
    }


    /**
     * Extracts the value for the key 'jsonPayload' from the given bundle. Return null if the key is not present or if the value cannot be converted to a {@link JSONObject}.
     *
     * @param args {@link Bundle} Generally a bundle representation of {@link ErnNavRoute}
     * @return JSONObject or null.
     */
    @Nullable
    public static JSONObject getPayload(@NonNull Bundle args) {
        if (args.containsKey("jsonPayload")) {
            Object obj = args.get("jsonPayload");
            if (obj instanceof String) {
                try {
                    return new JSONObject((String) obj);
                } catch (JSONException e) {
                    Logger.w(TAG, "Parsing failed for jsonPayload");
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * Extracts the value for the key 'jsonPayload' from the given bundle. Return null if the key is not present or if the value cannot be converted to a {@link JSONObject}.
     *
     * @param args {@link Bundle} Generally a bundle representation of {@link ErnNavRoute}
     * @return JSONObject or null.
     */
    @Nullable
    public static NavigationBar getNavBar(@NonNull Bundle args) {
        if (args.containsKey("navigationBar")) {
            Object obj = args.get("navigationBar");
            if (obj instanceof Bundle) {
                try {
                    return new NavigationBar((Bundle) obj);
                } catch (IllegalArgumentException e) {
                    Logger.w(TAG, "Invalid bundle: " + e.getMessage());
                    return null;
                }
            } else {
                Logger.w(TAG, "received wrong navigationBar key, value is not a bundle." + obj);
            }
        }
        return null;
    }
}
