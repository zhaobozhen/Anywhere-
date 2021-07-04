package com.absinthe.anywhere_.utils.manager

import android.app.Activity
import android.os.Process
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.*

object ActivityStackManager {
    /***
     * Get stack
     *
     * @return Activity stack
     */
    /**
     * Activity Stack
     */
    private var stack: Stack<WeakReference<AppCompatActivity>> = Stack()

    /***
     * Size of Activities
     *
     * @return Size of Activities
     */
    fun stackSize(): Int {
        return stack.size
    }

    /**
     * Add Activity to stack
     */
    fun addActivity(activity: WeakReference<AppCompatActivity>) {
        stack.add(activity)
    }

    /**
     * Delete Activity
     *
     * @param activity Weak Reference of Activity
     */
    fun removeActivity(activity: WeakReference<AppCompatActivity>) {
        stack.remove(activity)
    }

    /***
     * Get top stack Activity
     *
     * @return Activity
     */
    val topActivity: AppCompatActivity?
        get() {
            return try {
                stack.lastElement().get()
            } catch (e: NoSuchElementException) {
                null
            }
        }

    /***
     * Get Activity by class
     *
     * @param cls Activity class
     * @return Activity
     */
    fun getActivity(cls: Class<*>): AppCompatActivity? {
        var returnActivity: AppCompatActivity?
        for (activity in stack) {
            activity.get()?.let {
                if (it.javaClass == cls) {
                    returnActivity = activity.get()
                    return returnActivity
                }
            }
        }
        return null
    }

    /**
     * Kill top stack Activity
     */
    fun killTopActivity() {
        try {
            val activity = stack.lastElement()
            killActivity(activity)
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.e(e.toString())
        }
    }

    /***
     * Kill Activity
     *
     * @param activity Activity want to kill
     */
    private fun killActivity(activity: WeakReference<AppCompatActivity>) {
        try {
            val iterator = stack.iterator()
            while (iterator.hasNext()) {
                val stackActivity = iterator.next()
                if (stackActivity.get() == null) {
                    iterator.remove()
                    continue
                }
                stackActivity.get()?.let {
                    if (it.javaClass.name == activity.get()?.javaClass?.name) {
                        iterator.remove()
                        it.finish()
                        return
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e.toString())
        }
    }

    /***
     * Kill Activity by class
     *
     * @param cls class
     */
    fun killActivity(cls: Class<*>) {
        try {
            val listIterator = stack.listIterator()
            while (listIterator.hasNext()) {
                val activity: Activity? = listIterator.next().get()
                if (activity == null) {
                    listIterator.remove()
                    continue
                }
                if (activity.javaClass == cls) {
                    listIterator.remove()
                    activity.finish()
                    break
                }
            }
        } catch (e: Exception) {
            Timber.e(e.toString())
        }
    }

    /**
     * Kill all Activity
     */
    private fun killAllActivity() {
        try {
            val listIterator = stack.listIterator()
            while (listIterator.hasNext()) {
                val activity = listIterator.next().get()
                activity?.finish()
                listIterator.remove()
            }
        } catch (e: Exception) {
            Timber.e(e.toString())
        }
    }

    /**
     * Exit application
     */
    fun appExit() {
        killAllActivity()
        Process.killProcess(Process.myPid())
    }
}