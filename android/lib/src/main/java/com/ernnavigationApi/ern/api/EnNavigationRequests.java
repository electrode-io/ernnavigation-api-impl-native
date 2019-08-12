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

import androidx.annotation.NonNull;

import com.ernnavigationApi.ern.model.ErnNavRoute;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeRequestHandler;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeResponseListener;
import com.walmartlabs.electrode.reactnative.bridge.None;
import com.walmartlabs.electrode.reactnative.bridge.RequestHandlerProcessor;
import com.walmartlabs.electrode.reactnative.bridge.RequestProcessor;
import com.walmartlabs.electrode.reactnative.bridge.RequestHandlerHandle;


final class EnNavigationRequests implements EnNavigationApi.Requests {
    EnNavigationRequests() {}


    @Override
    public RequestHandlerHandle registerBackRequestHandler(@NonNull final ElectrodeBridgeRequestHandler<ErnNavRoute, None> handler) {
        return new RequestHandlerProcessor<>(REQUEST_BACK, ErnNavRoute.class, None.class, handler).execute();
    }

    @Override
    public RequestHandlerHandle registerFinishRequestHandler(@NonNull final ElectrodeBridgeRequestHandler<String, None> handler) {
        return new RequestHandlerProcessor<>(REQUEST_FINISH, String.class, None.class, handler).execute();
    }

    @Override
    public RequestHandlerHandle registerNavigateRequestHandler(@NonNull final ElectrodeBridgeRequestHandler<ErnNavRoute, None> handler) {
        return new RequestHandlerProcessor<>(REQUEST_NAVIGATE, ErnNavRoute.class, None.class, handler).execute();
    }

    @Override
    public RequestHandlerHandle registerUpdateRequestHandler(@NonNull final ElectrodeBridgeRequestHandler<ErnNavRoute, None> handler) {
        return new RequestHandlerProcessor<>(REQUEST_UPDATE, ErnNavRoute.class, None.class, handler).execute();
    }

    //------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void back(ErnNavRoute route, @NonNull final ElectrodeBridgeResponseListener<None> responseListener) {
        new RequestProcessor<>(REQUEST_BACK,  route, None.class, responseListener).execute();
    }
    @Override
    public void finish(String finalPayload,@NonNull final ElectrodeBridgeResponseListener<None> responseListener) {
        new RequestProcessor<>(REQUEST_FINISH,  finalPayload, None.class, responseListener).execute();
    }
    @Override
    public void navigate(ErnNavRoute route, @NonNull final ElectrodeBridgeResponseListener<None> responseListener) {
        new RequestProcessor<>(REQUEST_NAVIGATE,  route, None.class, responseListener).execute();
    }
    @Override
    public void update(ErnNavRoute updatedRoute, @NonNull final ElectrodeBridgeResponseListener<None> responseListener) {
        new RequestProcessor<>(REQUEST_UPDATE,  updatedRoute, None.class, responseListener).execute();
    }
}