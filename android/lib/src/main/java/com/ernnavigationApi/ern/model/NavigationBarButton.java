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

public class NavigationBarButton implements Parcelable, Bridgeable {

    private String name;
    private String identifier;
    private String orientation;

    private NavigationBarButton() {}

    private NavigationBarButton(Builder builder) {
        this.name = builder.name;
        this.identifier = builder.identifier;
        this.orientation = builder.orientation;
    }

    private NavigationBarButton(Parcel in) {
        this(in.readBundle());
    }

    public NavigationBarButton(@NonNull Bundle bundle) {
        if(!bundle.containsKey("name")){
            throw new IllegalArgumentException("name property is required");
        }

        if(!bundle.containsKey("identifier")){
            throw new IllegalArgumentException("identifier property is required");
        }

        if(!bundle.containsKey("orientation")){
            throw new IllegalArgumentException("orientation property is required");
        }

        this.name = bundle.getString("name");
        this.identifier = bundle.getString("identifier");
        this.orientation = bundle.getString("orientation");
    }

    public static final Creator<NavigationBarButton> CREATOR = new Creator<NavigationBarButton>() {
        @Override
        public NavigationBarButton createFromParcel(Parcel in) {
            return new NavigationBarButton(in);
        }

        @Override
        public NavigationBarButton[] newArray(int size) {
            return new NavigationBarButton[size];
        }
    };

    /**
    * Name of button
    *
    * @return String
    */
    @NonNull
    public String getName() {
        return name;
    }

    /**
    * Id of the button
    *
    * @return String
    */
    @NonNull
    public String getIdentifier() {
        return identifier;
    }

    /**
    * Orientation LEFT|RIGHT|CENTER etc.
    *
    * @return String
    */
    @NonNull
    public String getOrientation() {
        return orientation;
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
        bundle.putString("name", this.name);
        bundle.putString("identifier", this.identifier);
        bundle.putString("orientation", this.orientation);
        return bundle;
    }

    @Override
    public String toString() {
        return "{"
        + "name:" + (name != null ? "\"" + name + "\"" : null)+ ","
        + "identifier:" + (identifier != null ? "\"" + identifier + "\"" : null)+ ","
        + "orientation:" + (orientation != null ? "\"" + orientation + "\"" : null)
        + "}";
    }

    public static class Builder {
        private final String name;
        private final String identifier;
        private final String orientation;

        public Builder(@NonNull String name, @NonNull String identifier, @NonNull String orientation) {
            this.name = name;
            this.identifier = identifier;
            this.orientation = orientation;
        }


        @NonNull
        public NavigationBarButton build() {
            return new NavigationBarButton(this);
        }
    }
}
