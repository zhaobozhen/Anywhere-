package com.absinthe.anywhere_.utils.manager;

import android.app.Activity;
import android.content.Context;
import android.os.Process;
import android.util.Log;

import com.absinthe.anywhere_.BaseActivity;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Stack;

public class ActivityStackManager {
    private static final String TAG = ActivityStackManager.class.getSimpleName();
    private static ActivityStackManager activityStackManager = new ActivityStackManager();

    /**
     * Activity Stack
     */
    private Stack<WeakReference<BaseActivity>> mActivityStack;

    private ActivityStackManager() {
    }

    /***
     * Get AppManager instance
     *
     * @return Instance of AppManager
     */
    public static ActivityStackManager getInstance() {
        if (activityStackManager == null) {
            activityStackManager = new ActivityStackManager();
        }
        return activityStackManager;
    }

    /***
     * Size of Activities
     *
     * @return Size of Activities
     */
    public int stackSize() {
        return mActivityStack.size();
    }

    /***
     * Get stack
     *
     * @return Activity stack
     */
    public Stack<WeakReference<BaseActivity>> getStack() {
        return mActivityStack;
    }

    /**
     * Add Activity to stack
     */
    public void addActivity(WeakReference<BaseActivity> activity) {
        if (mActivityStack == null) {
            mActivityStack = new Stack<>();
        }
        mActivityStack.add(activity);
    }

    /**
     * Delete Activity
     *
     * @param activity Weak Reference of Activity
     */
    public void removeActivity(WeakReference<BaseActivity> activity) {
        if (mActivityStack != null) {
            mActivityStack.remove(activity);
        }
    }

    /***
     * Get top stack Activity
     *
     * @return Activity
     */
    public BaseActivity getTopActivity() {
        BaseActivity activity = mActivityStack.lastElement().get();
        if (null == activity) {
            return null;
        } else {
            return mActivityStack.lastElement().get();
        }
    }

    /***
     * Get Activity by class
     *
     * @param cls Activity class
     * @return Activity
     */
    public BaseActivity getActivity(Class<?> cls) {
        BaseActivity return_activity = null;
        for (WeakReference<BaseActivity> activity : mActivityStack) {
            if (activity.get().getClass().equals(cls)) {
                return_activity = activity.get();
                break;
            }
        }
        return return_activity;
    }

    /**
     * Kill top stack Activity
     */
    public void killTopActivity() {
        try {
            WeakReference<BaseActivity> activity = mActivityStack.lastElement();
            killActivity(activity);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
    }

    /***
     * Kill Activity
     *
     * @param activity Activity want to kill
     */
    public void killActivity(WeakReference<BaseActivity> activity) {
        try {
            Iterator<WeakReference<BaseActivity>> iterator = mActivityStack.iterator();
            while (iterator.hasNext()) {
                WeakReference<BaseActivity> stackActivity = iterator.next();
                if (stackActivity.get() == null) {
                    iterator.remove();
                    continue;
                }
                if (stackActivity.get().getClass().getName().equals(activity.get().getClass().getName())) {
                    iterator.remove();
                    stackActivity.get().finish();
                    break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /***
     * Kill Activity by class
     *
     * @param cls class
     */
    public void killActivity(Class<?> cls) {
        try {
            ListIterator<WeakReference<BaseActivity>> listIterator = mActivityStack.listIterator();
            while (listIterator.hasNext()) {
                Activity activity = listIterator.next().get();
                if (activity == null) {
                    listIterator.remove();
                    continue;
                }
                if (activity.getClass() == cls) {
                    listIterator.remove();
                    activity.finish();
                    break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * Kill all Activity
     */
    public void killAllActivity() {
        try {
            ListIterator<WeakReference<BaseActivity>> listIterator = mActivityStack.listIterator();
            while (listIterator.hasNext()) {
                BaseActivity activity = listIterator.next().get();
                if (activity != null) {
                    activity.finish();
                }
                listIterator.remove();
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * Exit application
     */
    public void AppExit(Context context) {
        killAllActivity();
        Process.killProcess(Process.myPid());
    }

}

