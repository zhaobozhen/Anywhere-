package com.absinthe.anywhere_.utils.manager;

public class IzukoHelper {

    static {
        System.loadLibrary("izuko");
    }

    public static native void checkSignature();

    public static native String getCipherKey();

}
