package com.absinthe.anywhere_.services;

import android.os.Handler;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;

public class IzukoService extends BaseAccessibilityService {
    private static boolean isClicked = true;
    private static String sPackageName = "";
    private static String sClassName = "";
    private static String sClickText = "";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (isClicked) return;

        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
                event.getPackageName().equals(sPackageName)) {

            CharSequence className = event.getClassName();
            if (className.equals(sClassName)) {

                if (findViewByText("root") == null) {
                    clickTextViewByText(sClickText);
                } else {
                    clickTextViewByText("知道了"); //Root AlertDialog
                    new Handler(Looper.getMainLooper()).postDelayed(() -> clickTextViewByText(sClickText), 200);
                }

                isClicked = true;
            }
        }
    }

    @Override
    public void onInterrupt() {

    }

    public static void isClicked(boolean isClicked) {
        IzukoService.isClicked = isClicked;
    }

    public static void setPackageName(String sPackageName) {
        IzukoService.sPackageName = sPackageName;
    }

    public static void setClassName(String sClassName) {
        IzukoService.sClassName = sClassName;
    }

    public static void setClickText(String sClickText) {
        IzukoService.sClickText = sClickText;
    }
}
