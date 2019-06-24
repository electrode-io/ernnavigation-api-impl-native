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

    private String title;
    private String icon;
    private String id;
    private String location;
    private Boolean disabled;
    private String adaLabel;

    private NavigationBarButton() {}

    private NavigationBarButton(Builder builder) {
        this.title = builder.title;
        this.icon = builder.icon;
        this.id = builder.id;
        this.location = builder.location;
        this.disabled = builder.disabled;
        this.adaLabel = builder.adaLabel;
    }

    private NavigationBarButton(Parcel in) {
        this(in.readBundle());
    }

    public NavigationBarButton(@NonNull Bundle bundle) {
        if(!bundle.containsKey("id")){
            throw new IllegalArgumentException("id property is required");
        }

        if(!bundle.containsKey("location")){
            throw new IllegalArgumentException("location property is required");
        }

        this.title = bundle.getString("title");
        this.icon = bundle.getString("icon");
        this.id = bundle.getString("id");
        this.location = bundle.getString("location");
        this.disabled = bundle.containsKey("disabled") ? bundle.getBoolean("disabled") : null;
        this.adaLabel = bundle.getString("adaLabel");
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
    * Button title if any.
    *
    * @return String
    */
    @Nullable
    public String getTitle() {
        return title;
    }

    /**
    * Icon resource identifier that can be used find the icon
    *
    * @return String
    */
    @Nullable
    public String getIcon() {
        return icon;
    }

    /**
    * Id of the button, this namespace will be used as an identifier when a button click event is emitted.
    *
    * @return String
    */
    @NonNull
    public String getId() {
        return id;
    }

    /**
    * Allowed enums: left, right
    *
    * @return String
    */
    @NonNull
    public String getLocation() {
        return location;
    }

    /**
    * Default to false. If set to true the button will be disabled(non-clickable)
    *
    * @return Boolean
    */
    @Nullable
    public Boolean getDisabled() {
        return disabled;
    }

    /**
    * Accessibility label
    *
    * @return String
    */
    @Nullable
    public String getAdaLabel() {
        return adaLabel;
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
        bundle.putString("id", this.id);
        bundle.putString("location", this.location);
        if(title != null) {
            bundle.putString("title", this.title );
        }
        if(icon != null) {
            bundle.putString("icon", this.icon );
        }
        if(this.disabled != null) {
            bundle.putBoolean("disabled", this.disabled);
        }
        if(adaLabel != null) {
            bundle.putString("adaLabel", this.adaLabel );
        }
        return bundle;
    }

    @Override
    public String toString() {
        return "{"
        + "title:" + (title != null ? "\"" + title + "\"" : null)+ ","
        + "icon:" + (icon != null ? "\"" + icon + "\"" : null)+ ","
        + "id:" + (id != null ? "\"" + id + "\"" : null)+ ","
        + "location:" + (location != null ? "\"" + location + "\"" : null)+ ","
        + "disabled:" + disabled+ ","
        + "adaLabel:" + (adaLabel != null ? "\"" + adaLabel + "\"" : null)
        + "}";
    }

    public static class Builder {
        private final String id;
        private final String location;
        private String title;
        private String icon;
        private Boolean disabled;
        private String adaLabel;

        public Builder(@NonNull String id, @NonNull String location) {
            this.id = id;
            this.location = location;
        }

        @NonNull
        public Builder title(@Nullable String title) {
            this.title = title;
            return this;
        }
        @NonNull
        public Builder icon(@Nullable String icon) {
            this.icon = icon;
            return this;
        }
        @NonNull
        public Builder disabled(@Nullable Boolean disabled) {
            this.disabled = disabled;
            return this;
        }
        @NonNull
        public Builder adaLabel(@Nullable String adaLabel) {
            this.adaLabel = adaLabel;
            return this;
        }

        @NonNull
        public NavigationBarButton build() {
            return new NavigationBarButton(this);
        }
    }
}
