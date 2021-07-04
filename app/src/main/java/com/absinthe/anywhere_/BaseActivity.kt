package com.absinthe.anywhere_

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.os.FileUriExposedException
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.listener.OnDocumentResultListener
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.utils.manager.ActivityStackManager
import com.absinthe.libraries.utils.manager.SystemBarManager
import com.absinthe.libraries.utils.utils.UiUtils
import rikka.material.app.MaterialActivity
import timber.log.Timber
import java.lang.ref.WeakReference

@SuppressLint("Registered, MissingSuperCall")
abstract class BaseActivity<T : ViewBinding> : MaterialActivity() {

    var shouldFinishOnResume = false

    private var mListener: OnDocumentResultListener? = null
    private lateinit var reference: WeakReference<AppCompatActivity>

    protected lateinit var binding: T
    protected lateinit var root: View

    protected abstract fun setViewBinding(): T

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i("onCreate")
        window.decorView.post { UiUtils.setSystemBarStyle(window) }

        super.onCreate(savedInstanceState)

        reference = WeakReference(this)
        SystemBarManager.measureSystemBar(window)
        ActivityStackManager.addActivity(reference)

        binding = setViewBinding()
        root = binding.root
        setContentView(root)
        initView()
    }

    override fun onResume() {
        super.onResume()
        if (shouldFinishOnResume) {
            finish()
        }
    }

    override fun onDestroy() {
        ActivityStackManager.removeActivity(reference)
        super.onDestroy()
    }

    override fun shouldApplyTranslucentSystemBars(): Boolean {
        return true
    }

    override fun computeUserThemeKey(): String {
        return ""
    }

    override fun onApplyUserThemeResource(theme: Resources.Theme, isDecorView: Boolean) {
        theme.applyStyle(R.style.ThemeOverlay, true)
    }

    fun setDocumentResultListener(listener: OnDocumentResultListener?) {
        mListener = listener
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Const.REQUEST_CODE_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            try {
                data?.data?.let {
                    mListener?.onResult(it)
                    if (it.toString().contains("file://")) {
                        ToastUtil.makeText(R.string.toast_file_uri_exposed)
                    } else {
                        try {
                            AppUtils.takePersistableUriPermission(this, it, data)
                        } catch (e: RuntimeException) {
                            ToastUtil.makeText(R.string.toast_runtime_error)
                        }
                    }
                }
            } catch (e: Exception) {
                if (AppUtils.atLeastN()) {
                    if (e is FileUriExposedException) {
                        ToastUtil.makeText(R.string.toast_file_uri_exposed)
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun finish() {
        if (GlobalValues.isExcludeFromRecent) {
            finishAndRemoveTask()
        } else {
            super.finish()
        }
    }

    protected open fun initView() { }
}