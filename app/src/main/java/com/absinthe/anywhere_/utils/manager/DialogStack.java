package com.absinthe.anywhere_.utils.manager;

import android.app.Dialog;

import androidx.fragment.app.DialogFragment;

import com.absinthe.anywhere_.view.AnywhereBottomSheetDialog;

import java.util.Objects;
import java.util.Stack;

/**
 * Created by Absinthe at 2020/1/13
 *
 * Dialog Stack
 *
 * Make it display unique Dialog at the same time
 * to make app delegate.
 */
public class DialogStack {

    public enum Singleton {
        INSTANCE;
        private Stack<Object> instance;

        Singleton() {
            instance = new Stack<>();
        }

        public Stack<Object> getInstance() {
            return instance;
        }
    }

    private static boolean isPrintStack = false;

    public static void push(Object dialog) {
        printStack();
        Logger.i("Push Start");
        if (!(dialog instanceof Dialog) && !(dialog instanceof DialogFragment)) {
            return;
        }

        if (Singleton.INSTANCE.getInstance().empty()) {
            Singleton.INSTANCE.getInstance().push(dialog);
        } else {
            Object peekObject = Singleton.INSTANCE.getInstance().peek();

            if (peekObject instanceof AnywhereBottomSheetDialog) {
                ((AnywhereBottomSheetDialog) peekObject).hide();
            } else if (peekObject instanceof Dialog) {
                ((Dialog) peekObject).hide();
            } else if (peekObject instanceof DialogFragment) {
                Objects.requireNonNull(
                        ((DialogFragment) peekObject).getDialog()).hide();
            }
            Singleton.INSTANCE.getInstance().push(dialog);
        }

        if (dialog instanceof AnywhereBottomSheetDialog) {
            ((AnywhereBottomSheetDialog) dialog).isPush = true;
            ((AnywhereBottomSheetDialog) dialog).show();
        } else if (dialog instanceof Dialog) {
            ((Dialog) dialog).show();
        }

        Logger.i("Push End");
        printStack();
    }

    public static void pop() {
        printStack();
        Logger.i("Pop Start");
        if (Singleton.INSTANCE.getInstance().empty()) {
            return;
        }

        Object peekObject = Singleton.INSTANCE.getInstance().peek();

        if (peekObject instanceof Dialog) {
            ((Dialog) peekObject).dismiss();
        } else if (peekObject instanceof DialogFragment) {
            ((DialogFragment) peekObject).dismiss();
        }
        Singleton.INSTANCE.getInstance().pop();

        if (!Singleton.INSTANCE.getInstance().empty()) {
            peekObject = Singleton.INSTANCE.getInstance().peek();

            if (peekObject instanceof Dialog) {
                ((Dialog) peekObject).show();
            } else if (peekObject instanceof DialogFragment) {
                Objects.requireNonNull(
                        ((DialogFragment) peekObject).getDialog()).show();
            }
        }

        Logger.i("Pop End");
        printStack();
    }

    public static void setPrintStack(boolean flag) {
        isPrintStack = flag;
    }

    private static void printStack() {
        if (isPrintStack) {
            Logger.i("DialogStack:");

            for (Object object : Singleton.INSTANCE.getInstance()) {
                Logger.i(object.getClass());
            }

            Logger.i("--------------------------------------");
        }
    }
}
