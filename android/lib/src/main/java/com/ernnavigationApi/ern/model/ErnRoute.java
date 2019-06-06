/*
* Copyright 2017 WalmartLabs
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.ernnavigationApi.ern.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.List;

import com.walmartlabs.electrode.reactnative.bridge.Bridgeable;

import static com.walmartlabs.electrode.reactnative.bridge.util.BridgeArguments.*;

public class ErnRoute implements Parcelable, Bridgeable {

    private String path;
    private String jsonPayload;
    private NavigationBar navigationBar;

    private ErnRoute() {}

    private ErnRoute(Builder builder) {
        this.path = builder.path;
        this.jsonPayload = builder.jsonPayload;
        this.navigationBar = builder.navigationBar;
    }

    private ErnRoute(Parcel in) {
        this(in.readBundle());
    }

    public ErnRoute(@NonNull Bundle bundle) {
        if(!bundle.containsKey("path")){
            throw new IllegalArgumentException("path property is required");
        }

        this.path = bundle.getString("path");
        this.jsonPayload = bundle.getString("jsonPayload");
        this.navigationBar = bundle.containsKey("navigationBar") ? new NavigationBar(bundle.getBundle("navigationBar")) : null;
    }

    public static final Creator<ErnRoute> CREATOR = new Creator<ErnRoute>() {
        @Override
        public ErnRoute createFromParcel(Parcel in) {
            return new ErnRoute(in);
        }

        @Override
        public ErnRoute[] newArray(int size) {
            return new ErnRoute[size];
        }
    };

    /**
    * Path of the Route. Mostly the name of the container(defined by the native app) or the miniapp name. The content of the path is mainly determined by the native implemenation of the API
    *
    * @return String
    */
    @NonNull
    public String getPath() {
        return path;
    }

    /**
    * Optional Payload (respresented as JSON String) needed by the screen you are trying to navigate to.
    *
    * @return String
    */
    @Nullable
    public String getJsonPayload() {
        return jsonPayload;
    }

    @Nullable
    public NavigationBar getNavigationBar() {
        return navigationBar;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBundle(toBundle());
    }

    @NonNull
    @Override
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("path", this.path);
        if(jsonPayload != null) {
            bundle.putString("jsonPayload", this.jsonPayload );
        }
        if(this.navigationBar != null) {
            bundle.putBundle("navigationBar", this.navigationBar.toBundle());
        }
        return bundle;
    }

    @Override
    public String toString() {
        return "{"
        + "path:" + (path != null ? "\"" + path + "\"" : null)+ ","
        + "jsonPayload:" + (jsonPayload != null ? "\"" + jsonPayload + "\"" : null)+ ","
        + "navigationBar:" + (navigationBar != null ? navigationBar.toString() : null)
        + "}";
    }

    public static class Builder {
        private final String path;
        private String jsonPayload;
        private NavigationBar navigationBar;

        public Builder(@NonNull String path) {
            this.path = path;
        }

        @NonNull
        public Builder jsonPayload(@Nullable String jsonPayload) {
            this.jsonPayload = jsonPayload;
            return this;
        }
        @NonNull
        public Builder navigationBar(@Nullable NavigationBar navigationBar) {
            this.navigationBar = navigationBar;
            return this;
        }

        @NonNull
        public ErnRoute build() {
            return new ErnRoute(this);
        }
    }
}
