package com.absinthe.anywhere_.utils.handler;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.fragment.app.Fragment;

public class URLSchemeHandler {

    public static void parse(String url, Context context) throws ActivityNotFoundException {
        context.startActivity(handleIntent(url));
    }

    public static void parse(String url, Activity activity) throws ActivityNotFoundException {
        activity.startActivity(handleIntent(url));
    }

    public static void parseForResult(String url, Activity activity, int requestCode) throws ActivityNotFoundException {
        activity.startActivityForResult(handleIntent(url), requestCode);
    }

    public static void parse(String url, Fragment fragment) throws ActivityNotFoundException {
        fragment.startActivity(handleIntent(url));
    }

    public static void parseForResult(String url, Fragment fragment, int requestCode) throws ActivityNotFoundException {
        fragment.startActivityForResult(handleIntent(url), requestCode);
    }

    public static Intent handleIntent(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(url));
        return intent;
    }
}
