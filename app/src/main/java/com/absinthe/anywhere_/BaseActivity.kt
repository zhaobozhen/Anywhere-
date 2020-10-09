package com.absinthe.anywhere_

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.FileUriExposedException
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.listener.OnDocumentResultListener
import com.absinthe.anywhere_.ui.main.MainActivity
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.StatusBarUtil
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.utils.manager.ActivityStackManager
import com.absinthe.libraries.utils.extensions.paddingTopCompat
import com.blankj.utilcode.util.BarUtils
import timber.log.Timber
import java.lang.ref.WeakReference

@SuppressLint("Registered")
abstract class BaseActivity : AppCompatActivity() {

    protected var isPaddingToolbar: Boolean = false

    protected var mToolbar: Toolbar? = null
    private var mListener: OnDocumentResultListener? = null
    private lateinit var reference: WeakReference<BaseActivity>

    protected abstract fun setViewBinding()
    protected abstract fun setToolbar()

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i("onCreate")
        if (GlobalValues.backgroundUri.isEmpty() || this !is MainActivity) {
            StatusBarUtil.setSystemBarStyle(this)
        }

        super.onCreate(savedInstanceState)

        reference = WeakReference(this)
        ActivityStackManager.addActivity(reference)
        setViewBinding()
        initView()
    }

    override fun onDestroy() {
        ActivityStackManager.removeActivity(reference)
        super.onDestroy()
    }

    protected open fun initView() {
        setToolbar()
        if (isPaddingToolbar) {
            mToolbar?.paddingTopCompat = BarUtils.getStatusBarHeight()
        }
        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
}