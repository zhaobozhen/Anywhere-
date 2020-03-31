package com.absinthe.anywhere_.utils

import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation

object AnimationUtil {

    const val LONG = 1000
    const val SHORT = 500

    /**
     * Fade in/out animation
     *
     * @param view     triggered view
     * @param state    fade state
     * @param duration fade duration (ms)
     */
    @JvmStatic
    fun showAndHiddenAnimation(view: View, state: AnimationState, duration: Long) {
        var start = 0f
        var end = 0f

        when (state) {
            AnimationState.STATE_SHOW -> {
                end = 1f
                view.visibility = View.VISIBLE
            }
            AnimationState.STATE_HIDDEN -> {
                start = 1f
                view.visibility = View.INVISIBLE
            }
            AnimationState.STATE_GONE -> {
                start = 1f
                view.visibility = View.GONE
            }
        }

        AlphaAnimation(start, end).apply {
            this.duration = duration
            this.fillAfter = true
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationRepeat(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    view.clearAnimation()
                }
            })
            view.animation = this
            start()
        }
    }

    enum class AnimationState {
        STATE_SHOW, STATE_HIDDEN, STATE_GONE
    }
}