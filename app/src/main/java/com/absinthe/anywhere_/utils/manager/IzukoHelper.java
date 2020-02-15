package com.absinthe.anywhere_.utils.manager;

import androidx.annotation.Keep;

import com.absinthe.anywhere_.model.Settings;

@Keep
public class IzukoHelper {

    static {
        System.loadLibrary("izuko");
    }

    public static native void checkSignature();

    public static native String getCipherKey();

    public static native boolean isHitagi(String token);

    public static boolean isHitagi() {
        return isHitagi(Settings.sToken);
    }

}
