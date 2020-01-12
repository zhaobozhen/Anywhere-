package com.absinthe.anywhere_.utils.manager;

import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.absinthe.anywhere_.utils.UiUtils;

import java.util.Timer;
import java.util.TimerTask;

public class MenuAnimator {

    private final int ALPHA_MAX = 255;//just look at the documentation
    private final int NUMBER_OF_TICK = 255;//can go from 1 to 255, it's the number of tick
    private final int ALPHA_PER_TICK = ALPHA_MAX / NUMBER_OF_TICK;//alpha we'll remove/add on every tick
    private long DELAY = 1000;//amount of time in milliseconds before animation execution.
    private final AppCompatActivity mActivity;

    /*
     ** Private field
     */
    private MenuItem mMenuItem;
    private Timer mTimer;
    private int mCurrentAlpha;
    private int mColor;

    /*
     ** Constructor
     */
    public MenuAnimator(@NonNull AppCompatActivity activity, @NonNull final MenuItem menuItem, final int color) {
        mActivity = activity;
        mMenuItem = menuItem;
        mTimer = new Timer();
        mColor = color;
    }

    /*
     ** Public method
     */
    public void fadeIn(final long duration) {
        final long period = duration / NUMBER_OF_TICK;//time between 2 run() call
        mCurrentAlpha = 0;

        //init a timer which will updateActionBarColor on every each period
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                //update the actionBar
                updateItemColor(true);
            }
        }, DELAY, period);
    }

    public void fadeOut(final long duration) {
        final long period = duration / NUMBER_OF_TICK;//time between 2 run() call
        mCurrentAlpha = ALPHA_MAX;

        //init a timer which will updateActionBarColor on every each period
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                //update the actionBar
                updateItemColor(false);
            }
        }, DELAY, period);
    }

    /*
     ** Private method
     */
    private void updateItemColor(boolean isFadeIn) {
        //We have to go to the main thread for updating the interface.
        mActivity.runOnUiThread(new TimerTask() {
            @Override
            public void run() {
                //check if the animation is finish
                if (mCurrentAlpha > 255 || mCurrentAlpha < 0) {
                    mTimer.cancel();
                    mTimer.purge();
                    return;
                }
                UiUtils.tintMenuIconWithAlpha(mActivity, mMenuItem, mColor, mCurrentAlpha);

                //upgrade alpha
                if (isFadeIn) {
                    mCurrentAlpha += ALPHA_PER_TICK;
                } else {
                    mCurrentAlpha -= ALPHA_PER_TICK;
                }
            }
        });
    }
}
