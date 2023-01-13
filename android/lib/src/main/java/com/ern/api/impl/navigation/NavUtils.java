/*
 * Copyright 2019 Walmart Labs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ern.api.impl.navigation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ernnavigation.ern.model.ErnNavRoute;
import com.ernnavigation.ern.model.NavigationBar;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public final class NavUtils {

    private static final String TAG = NavUtils.class.getSimpleName();
    public static final String KEY_JSON_PAYLOAD = "jsonPayload";

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
        if (args.containsKey(KEY_JSON_PAYLOAD)) {
            Object obj = args.get(KEY_JSON_PAYLOAD);
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
     * Merges the two bundle and also performs a merge for KEY_JSON_PAYLOAD entry if both bundles have a KEY_JSON_PAYLOAD entry.
     *
     * @param oldBundle old bundle
     * @param newBundle new bundle
     * @return Bundle Always returns the oldBundle with combined jsonPayload from newBundle along with other bundle elements.
     */
    @NonNull
    public static Bundle mergeBundleWithJsonPayloads(@NonNull Bundle oldBundle, @Nullable Bundle newBundle) {
        if (newBundle != null) {
            JSONObject mergedJsonPayload = null;
            if (newBundle.containsKey(KEY_JSON_PAYLOAD)) { //Merge the KEY_JSON_PAYLOAD entry if the new bundle has an entry
                JSONObject newJsonPayload = NavUtils.getPayload(newBundle);
                mergedJsonPayload = NavUtils.getPayload(oldBundle);
                if (newJsonPayload != null) {
                    if (mergedJsonPayload != null) {
                        Iterator<String> keys = newJsonPayload.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            try {
                                mergedJsonPayload.put(key, newJsonPayload.get(key));
                            } catch (JSONException e) {
                                Logger.e(TAG, "Error merging jsonPayload: %s", e.getMessage());
                            }
                        }
                    } else {
                        mergedJsonPayload = newJsonPayload;
                    }
                }
            }
            oldBundle.putAll(newBundle);
            if (mergedJsonPayload != null) {
                oldBundle.putString(KEY_JSON_PAYLOAD, mergedJsonPayload.toString());
            }
        }
        return oldBundle;
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
