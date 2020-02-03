package com.absinthe.anywhere_.utils.manager;

import com.absinthe.anywhere_.model.Settings;

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
