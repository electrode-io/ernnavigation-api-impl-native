package com.ern.api.impl.util;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import com.walmartlabs.ern.navigation.res.lib.R;

public class AnimUtil {

    public static void fade(@NonNull FragmentTransaction transaction) {
        transaction.setCustomAnimations(
                R.anim.fade_in,//enter
                R.anim.fade_out,//exit
                R.anim.fade_in,//popEnter
                R.anim.fade_out);//popExit
    }

    public static void slide(@NonNull FragmentTransaction transaction) {
        transaction.setCustomAnimations(
                R.anim.slide_in,//enter
                R.anim.fade_out,//exit
                R.anim.fade_in,//popEnter
                R.anim.slide_out);//popExit
    }
}
