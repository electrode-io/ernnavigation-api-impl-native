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

package com.ern.api.impl.core;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * Fragment that hosts a react native view component.
 *
 * @deprecated use {@link MiniAppFragment}
 */
@Deprecated
public class DefaultMiniAppFragment extends ElectrodeReactCoreFragment<ElectrodeReactFragmentDelegate> {

    public DefaultMiniAppFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    protected ElectrodeReactFragmentDelegate createFragmentDelegate() {
        return new ElectrodeReactFragmentDelegate(this);
    }

    @Nullable
    @Override
    public Bundle initialProps() {
        return null;
    }

    @Override
    public int fragmentLayoutId() {
        return NONE;
    }

    @Override
    public int reactViewContainerId() {
        return NONE;
    }

    @Override
    public int toolBarId() {
        return NONE;
    }
}

