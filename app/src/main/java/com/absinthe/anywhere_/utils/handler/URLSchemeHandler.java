package com.absinthe.anywhere_.utils.handler;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.fragment.app.Fragment;

public class URLSchemeHandler {

    public static void parse(String url, Context context) throws ActivityNotFoundException {
        try {
            context.startActivity(handleIntent(url));
        } catch (ActivityNotFoundException | SecurityException e) {
            throw new ActivityNotFoundException();
        }
    }

    public static void parse(String url, Activity activity) throws ActivityNotFoundException {
        try {
            activity.startActivity(handleIntent(url));
        } catch (ActivityNotFoundException | SecurityException e) {
            throw new ActivityNotFoundException();
        }
    }

    public static void parseForResult(String url, Activity activity, int requestCode) throws ActivityNotFoundException {
        try {
            activity.startActivityForResult(handleIntent(url), requestCode);
        } catch (ActivityNotFoundException | SecurityException e) {
            throw new ActivityNotFoundException();
        }
    }

    public static void parse(String url, Fragment fragment) throws ActivityNotFoundException {
        try {
            fragment.startActivity(handleIntent(url));
        } catch (ActivityNotFoundException | SecurityException e) {
            throw new ActivityNotFoundException();
        }
    }

    public static void parseForResult(String url, Fragment fragment, int requestCode) throws ActivityNotFoundException {
        try {
            fragment.startActivityForResult(handleIntent(url), requestCode);
        } catch (ActivityNotFoundException | SecurityException e) {
            throw new ActivityNotFoundException();
        }
    }

    public static Intent handleIntent(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(url));
        return intent;
    }
}
