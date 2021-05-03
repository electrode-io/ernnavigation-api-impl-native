package com.ern.api.impl.navigation.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.ern.api.impl.core.ElectrodeBaseFragmentDelegate;
import com.facebook.react.ReactRootView;
import com.walmartlabs.ern.navigation.res.lib.R;

public class MiniAppView extends FrameLayout {
    public interface OnSetInitialPropsListener {
        @Nullable
        Bundle getProps(@NonNull String componentName);
    }

    @Nullable
    private OnSetInitialPropsListener mInitialPropsListener;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MiniAppView(@NonNull Context context) {
        this(context, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MiniAppView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MiniAppView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MiniAppView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        ElectrodeBaseFragmentDelegate.ElectrodeActivityListener reactDelegate;
        if (context instanceof ElectrodeBaseFragmentDelegate.ElectrodeActivityListener) {
            reactDelegate = (ElectrodeBaseFragmentDelegate.ElectrodeActivityListener) context;
        } else {
            throw new RuntimeException("Activity must implement ElectrodeActivityListener for MiniAppView to properly create a react root view");
        }

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.MiniAppView);
        String miniAppComponentName = attributes.getString(R.styleable.MiniAppView_component_name);
        if (TextUtils.isEmpty(miniAppComponentName)) {
            throw new RuntimeException("MiniAppView entry missing required attribute: component_name");
        }
        //noinspection ConstantConditions
        Bundle initialProps = getInitialProps(context, miniAppComponentName);
        ReactRootView reactRootView = (ReactRootView) reactDelegate.createReactNativeView(miniAppComponentName, initialProps);
        if (reactRootView == null) {
            throw new RuntimeException("Not able to create a React Native view for component: " + miniAppComponentName);
        }
        this.addView(reactRootView);
        attributes.recycle();
    }

    private Bundle getInitialProps(@NonNull Context context, @NonNull String componentName) {
        if (context instanceof OnSetInitialPropsListener) {
            return ((OnSetInitialPropsListener) context).getProps(componentName);
        }
        return null;
    }
}
