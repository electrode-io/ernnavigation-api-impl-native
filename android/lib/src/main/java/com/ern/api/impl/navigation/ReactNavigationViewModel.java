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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ernnavigation.ern.api.EnNavigationApi;
import com.ernnavigation.ern.model.ErnNavRoute;
import com.walmartlabs.electrode.reactnative.bridge.BridgeFailureMessage;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeRequestHandler;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeResponseListener;
import com.walmartlabs.electrode.reactnative.bridge.None;
import com.walmartlabs.electrode.reactnative.bridge.RequestHandlerHandle;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;

public final class ReactNavigationViewModel extends ViewModel {
    private static final String TAG = ReactNavigationViewModel.class.getSimpleName();
    static final String KEY_NAV_TYPE = "NAV_TYPE";

    enum Type {
        NAVIGATE,
        UPDATE,
        BACK,
        FINISH
    }

    private final MutableLiveData<Route> routeLiveData = new MutableLiveData<>();
    private final ElectrodeBridgeRequestHandler<ErnNavRoute, None> navRequestHandler = new ElectrodeBridgeRequestHandler<ErnNavRoute, None>() {
        @Override
        public void onRequest(@Nullable final ErnNavRoute ernRoute, @NonNull final ElectrodeBridgeResponseListener<None> responseListener) {
            log("onRequest: NAVIGATE");
            if (!validate(ernRoute, responseListener)) return;

            final Bundle bundle = ernRoute.toBundle();
            bundle.putString(KEY_NAV_TYPE, Type.NAVIGATE.toString());
            post(bundle, responseListener);
        }
    };

    private final ElectrodeBridgeRequestHandler<ErnNavRoute, None> updateRequestHandler = new ElectrodeBridgeRequestHandler<ErnNavRoute, None>() {
        @Override
        public void onRequest(@Nullable ErnNavRoute ernRoute, @NonNull final ElectrodeBridgeResponseListener<None> responseListener) {
            log("onRequest: UPDATE");
            if (!validate(ernRoute, responseListener)) return;
            final Bundle bundle = ernRoute.toBundle();
            bundle.putString(KEY_NAV_TYPE, Type.UPDATE.toString());
            post(bundle, responseListener);
        }
    };

    private final ElectrodeBridgeRequestHandler<ErnNavRoute, None> backRequestHandler = new ElectrodeBridgeRequestHandler<ErnNavRoute, None>() {
        @Override
        public void onRequest(@Nullable ErnNavRoute ernRoute, @NonNull final ElectrodeBridgeResponseListener<None> responseListener) {
            log("onRequest: BACK");
            final Bundle bundle = ernRoute != null ? ernRoute.toBundle() : new Bundle();
            bundle.putString(KEY_NAV_TYPE, Type.BACK.toString());
            post(bundle, responseListener);
        }
    };

    private final ElectrodeBridgeRequestHandler<String, None> finishRequestHandler = new ElectrodeBridgeRequestHandler<String, None>() {
        @Override
        public void onRequest(@Nullable String payload, @NonNull final ElectrodeBridgeResponseListener<None> responseListener) {
            log("onRequest: FINISH");
            final Bundle bundle = new Bundle();
            if (payload != null) {
                bundle.putString("jsonPayload", payload);
            }
            bundle.putString(KEY_NAV_TYPE, Type.FINISH.toString());
            post(bundle, responseListener);
        }
    };

    private void post(final Bundle bundle, @NonNull final ElectrodeBridgeResponseListener<None> responseListener) {
        if (!routeLiveData.hasActiveObservers()) {
            // No active observers indicates that the fragment or activity holding this view model instance is not in `RESUMED` state.
            // We send a successful response in this case to avoid the bridge triggering a failure with a timeout.
            // One drawback of this approach is that later when the fragment resumes and the request actually fails there is no way to notify the caller.
            // In that case this response becomes a false positive.
            responseListener.onSuccess(None.NONE);
        }

        Route route = new Route.Builder(bundle)
                .routingNotifier(new RoutingNotifier() {
                    @Override
                    public void routingComplete(@NonNull RoutingResult result) {
                        if (result.isComplete) {
                            log("Routing successful: notifying response listener.  Message: " + result.message);
                            responseListener.onSuccess(None.NONE);
                        } else {
                            responseListener.onFailure(BridgeFailureMessage.create("NAVIGATION_FAILED", "Unable to handle navigation for " + bundle.getString("path") + " message: " + result.message));
                        }
                    }
                })
                .build();
        routeLiveData.postValue(route);
    }

    private boolean validate(@Nullable ErnNavRoute ernRoute, @NonNull ElectrodeBridgeResponseListener<None> responseListener) {
        if (ernRoute == null) {
            responseListener.onFailure(BridgeFailureMessage.create("NAVIGATION_FAILED", "Empty route received."));
            return false;
        }
        return true;
    }

    private RequestHandlerHandle requestHandle;
    private RequestHandlerHandle updateRequestHandle;
    private RequestHandlerHandle backRequestHandle;
    private RequestHandlerHandle finishRequestHandle;

    public void registerNavRequestHandler() {
        if (requestHandle == null || !requestHandle.isRegistered()) {
            log("Registering navigation request handlers");
            requestHandle = EnNavigationApi.requests().registerNavigateRequestHandler(navRequestHandler);
            updateRequestHandle = EnNavigationApi.requests().registerUpdateRequestHandler(updateRequestHandler);
            backRequestHandle = EnNavigationApi.requests().registerBackRequestHandler(backRequestHandler);
            finishRequestHandle = EnNavigationApi.requests().registerFinishRequestHandler(finishRequestHandler);
        }
    }

    public void unRegisterNavRequestHandler() {
        if (requestHandle != null) {
            log("Unregistering navigation request handlers");
            requestHandle.unregister();
            updateRequestHandle.unregister();
            backRequestHandle.unregister();
            finishRequestHandle.unregister();
            requestHandle = null;
            updateRequestHandle = null;
            backRequestHandle = null;
            finishRequestHandle = null;
        }
    }

    public LiveData<Route> getRouteLiveData() {
        return routeLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        unRegisterNavRequestHandler();
    }

    private void log(String s) {
        Logger.d(TAG, s);
    }
}
