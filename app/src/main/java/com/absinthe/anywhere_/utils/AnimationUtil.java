package com.absinthe.anywhere_.utils;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class AnimationUtil {
    public enum AnimationState {
        STATE_SHOW,
        STATE_HIDDEN,
        STATE_GONE
    }

    public static final int LONG = 1000;
    public static final int SHORT = 500;

    /**
     * Fade in/out animation
     *
     * @param view     triggered view
     * @param state    fade state
     * @param duration fade duration (ms)
     */
    public static void showAndHiddenAnimation(final View view, AnimationState state, long duration) {
        float start = 0f;
        float end = 0f;

        if (state == AnimationState.STATE_SHOW) {
            end = 1f;
            view.setVisibility(View.VISIBLE);
        } else if (state == AnimationState.STATE_HIDDEN) {
            start = 1f;
            view.setVisibility(View.INVISIBLE);
        } else if (state == AnimationState.STATE_GONE) {
            start = 1f;
            view.setVisibility(View.GONE);
        }
        AlphaAnimation animation = new AlphaAnimation(start, end);
        animation.setDuration(duration);
        animation.setFillAfter(true);
        animation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
            }
        });
        view.setAnimation(animation);
        animation.start();
    }
}

