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

package com.ernnavigationApi.ern.api;

import android.support.annotation.NonNull;

import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeEventListener;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeEvent;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeRequestHandler;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeResponseListener;
import com.walmartlabs.electrode.reactnative.bridge.None;
import com.walmartlabs.electrode.reactnative.bridge.RequestHandlerHandle;
import java.util.*;
import java.util.UUID;

import com.ernnavigationApi.ern.model.ErnRoute;

public final class EnNavigationApi {
    private static final Requests REQUESTS;

    static {
        REQUESTS = new EnNavigationRequests();
    }

    private EnNavigationApi() {
    }

    @NonNull
    public static Requests requests() {
        return REQUESTS;
    }



    public interface Requests {
        String REQUEST_BACK = "com.ernnavigationApi.ern.api.request.back";
        String REQUEST_FINISH = "com.ernnavigationApi.ern.api.request.finish";
        String REQUEST_NAVIGATE = "com.ernnavigationApi.ern.api.request.navigate";
        String REQUEST_UPDATE = "com.ernnavigationApi.ern.api.request.update";


        RequestHandlerHandle registerBackRequestHandler(@NonNull final ElectrodeBridgeRequestHandler<ErnRoute, None> handler);

        RequestHandlerHandle registerFinishRequestHandler(@NonNull final ElectrodeBridgeRequestHandler<String, None> handler);

        RequestHandlerHandle registerNavigateRequestHandler(@NonNull final ElectrodeBridgeRequestHandler<ErnRoute, None> handler);

        RequestHandlerHandle registerUpdateRequestHandler(@NonNull final ElectrodeBridgeRequestHandler<ErnRoute, None> handler);

        void back(ErnRoute route, @NonNull final ElectrodeBridgeResponseListener<None> responseListener);

        void finish(String finalPayload, @NonNull final ElectrodeBridgeResponseListener<None> responseListener);

        void navigate(ErnRoute route, @NonNull final ElectrodeBridgeResponseListener<None> responseListener);

        void update(ErnRoute updatedRoute, @NonNull final ElectrodeBridgeResponseListener<None> responseListener);

    }
}