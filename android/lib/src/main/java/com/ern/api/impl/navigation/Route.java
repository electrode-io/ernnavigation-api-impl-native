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

public final class Route { //FIXME: The class will implement NavDirections from android jet pack in the future. Enabling it to be future proof once we start using the new architecture components.
    public static final int NONE = -1;

    @Nullable
    private final Bundle arguments;

    @Nullable
    private RoutingNotifier routingNotifier;

    private final int actionId;

    /**
     * This filed will be populated after the routing has been handled.
     */
    private RoutingResult result;

    Route(Builder builder) {
        this.arguments = builder.arguments;
        this.routingNotifier = builder.routingNotifier;
        this.actionId = builder.actionId;
    }

    //FIXME: Made it identical to NavDirections public method. Will Override in the future.
    //@Override
    public int getActionId() {
        return actionId;
    }

    //FIXME: Made it identical to NavDirections public method. Will Override in the future.
    //@Override
    @NonNull
    public Bundle getArguments() {
        return arguments;
    }

    /**
     * Call this method only when the request is fully handled as this will trigger a notification back to the api caller.
     *
     * @param isSuccess true | false
     * @param message   optional message to pass back to the api caller, mainly used when the request fails.
     */
    public void setResult(boolean isSuccess, @Nullable String message) {
        if (result == null) {
            result = new RoutingResult(isSuccess, message);
            if (routingNotifier != null) {
                routingNotifier.routingComplete(result);
                routingNotifier = null;
            }
        } else {
            throw new IllegalStateException("Result is already set for this route. This operation should not be performed again.");
        }
    }

    /**
     * Indicates if the routing was handled for this route and a result is set.
     *
     * @return true | false boolean
     */
    boolean isCompleted() {
        return result != null;
    }

    public static class Builder {

        @Nullable
        private Bundle arguments;

        @Nullable
        private RoutingNotifier routingNotifier;

        private int actionId;

        public Builder(@NonNull Bundle arguments) {
            this.arguments = arguments;
        }

        public Builder routingNotifier(@Nullable RoutingNotifier routingNotifier) {
            this.routingNotifier = routingNotifier;
            return this;
        }

        public Builder actionId(int actionId) {
            this.actionId = actionId < 0 ? NONE : actionId;
            return this;
        }


        public Route build() {
            return new Route(this);
        }
    }
}
