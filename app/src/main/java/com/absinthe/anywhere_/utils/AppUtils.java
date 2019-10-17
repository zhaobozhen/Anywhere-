package com.absinthe.anywhere_.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class AppUtils {
    /**
     * react the url scheme
     * @param context to launch an intent
     * @param param1 param1
     * @param param2 param2
     * @param param3 param3
     */
    public static void openUrl(Context context, String param1, String param2, String param3) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        String url = "anywhere://url?"
                + "param1=" + param1
                + "&param2=" + param2
                + "&param3=" + param3;
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }
}
