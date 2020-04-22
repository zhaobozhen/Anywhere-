package com.absinthe.anywhere_.ui.shortcuts

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.FileUriExposedException
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.EventTag
import com.absinthe.anywhere_.constants.GlobalValues.workingMode
import com.absinthe.anywhere_.model.AnywhereEntity
import com.absinthe.anywhere_.services.CollectorService
import com.absinthe.anywhere_.utils.AppUtils.openNewURLScheme
import com.absinthe.anywhere_.utils.CommandUtils.execCmd
import com.absinthe.anywhere_.utils.TextUtils
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.utils.UiUtils
import com.absinthe.anywhere_.utils.handler.Opener
import com.absinthe.anywhere_.utils.handler.URLSchemeHandler.parse
import com.absinthe.anywhere_.utils.manager.DialogManager.showImageDialog
import com.absinthe.anywhere_.utils.manager.URLManager
import com.absinthe.anywhere_.view.AnywhereDialogBuilder
import com.absinthe.anywhere_.view.AnywhereDialogFragment
import com.absinthe.anywhere_.viewmodel.AnywhereViewModel
import com.blankj.utilcode.util.Utils
import com.microsoft.appcenter.analytics.Analytics
import timber.log.Timber

class ShortcutsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UiUtils.setActionBarTransparent(this)

        val viewModel = ViewModelProvider(this).get(AnywhereViewModel::class.java)
        val action = intent.action
        Timber.d("action = %s", action)
        Analytics.trackEvent(EventTag.SHORTCUT_OPEN)

        if (action != null) {
            if (action == ACTION_START_COLLECTOR) {
                if (workingMode == Const.WORKING_MODE_URL_SCHEME) {
                    openNewURLScheme(this)
                } else {
                    CollectorService.startCollector(this)
                }
                finish()
            } else if (action == ACTION_START_COMMAND) {
                val cmd = intent.getStringExtra(Const.INTENT_EXTRA_SHORTCUTS_CMD)

                if (cmd != null) {
                    Timber.d(cmd)
                    if (cmd.startsWith(AnywhereType.DYNAMIC_PARAMS_PREFIX) ||
                            cmd.startsWith(AnywhereType.SHELL_PREFIX)) {
                        Opener.with(this)
                                .load(cmd)
                                .setOpenedListener { finish() }
                                .open()
                    } else {
                        Opener.with(this).load(cmd).open()
                        finish()
                    }
                } else {
                    finish()
                }
            } else if (action == ACTION_START_FROM_WIDGET) {
                val cmd = intent.getStringExtra(Const.INTENT_EXTRA_WIDGET_COMMAND)

                if (cmd != null) {
                    if (cmd.startsWith(AnywhereType.DYNAMIC_PARAMS_PREFIX) ||
                            cmd.startsWith(AnywhereType.SHELL_PREFIX)) {
                        Opener.with(this)
                                .load(cmd)
                                .setOpenedListener { finish() }
                                .open()
                    } else {
                        Opener.with(this).load(cmd).open()
                        finish()
                    }
                } else {
                    finish()
                }
            } else if (action == ACTION_START_QR_CODE) {
                intent.getStringExtra(Const.INTENT_EXTRA_SHORTCUTS_CMD)?.let {
                    Timber.d(it)
                    execCmd(it)
                }
                finish()
            } else if (action == Intent.ACTION_CREATE_SHORTCUT) {
                viewModel.allAnywhereEntities?.observe(this, Observer { anywhereEntities: List<AnywhereEntity>? ->
                    val arrayAdapter = ArrayAdapter<String>(Utils.getApp(), android.R.layout.select_dialog_singlechoice)
                    Timber.d("list = %s", anywhereEntities)

                    if (anywhereEntities != null) {
                        for (ae in anywhereEntities) {
                            arrayAdapter.add(ae.appName)
                        }
                    }
                    AnywhereDialogBuilder(this)
                            .setAdapter(arrayAdapter) { _: DialogInterface?, i: Int ->
                                val shortcutIntent = Intent(this@ShortcutsActivity, ShortcutsActivity::class.java).apply {
                                    val cmd = TextUtils.getItemCommand(anywhereEntities?.get(i))

                                    if (cmd.startsWith(AnywhereType.QRCODE_PREFIX)) {
                                        this.action = ACTION_START_QR_CODE
                                    } else {
                                        this.action = ACTION_START_COMMAND
                                    }
                                    putExtra(Const.INTENT_EXTRA_SHORTCUTS_CMD, cmd)
                                }

                                val intent = Intent().apply {
                                    putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent)
                                    putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.shortcut_open))
                                    putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                                            Intent.ShortcutIconResource.fromContext(
                                                    this@ShortcutsActivity, R.drawable.ic_shortcut_start_collector))
                                }
                                setResult(Activity.RESULT_OK, intent)
                                finish()
                            }
                            .setOnCancelListener { finish() }
                            .show()
                })
            } else if (action == ACTION_START_IMAGE) {
                val uri = intent.getStringExtra(Const.INTENT_EXTRA_SHORTCUTS_CMD)

                if (uri != null) {
                    val ae = AnywhereEntity.Builder().apply {
                        param1 = uri
                    }
                    showImageDialog(this, ae, object : AnywhereDialogFragment.OnDismissListener {
                        override fun onDismiss() {
                            finish()
                        }
                    })
                } else {
                    finish()
                }
            } else if (action == Intent.ACTION_VIEW) {
                val uri = intent.data
                if (uri != null) {
                    val host = uri.host

                    if (android.text.TextUtils.equals(host, URLManager.OPEN_HOST)) {
                        val param1 = uri.getQueryParameter(Const.INTENT_EXTRA_PARAM_1)
                        val param2 = uri.getQueryParameter(Const.INTENT_EXTRA_PARAM_2)
                        val param3 = uri.getQueryParameter(Const.INTENT_EXTRA_PARAM_3)

                        if (param1 != null && param2 != null && param3 != null) {
                            if (param2.isEmpty() && param3.isEmpty()) {
                                try {
                                    parse(param1, this)
                                } catch (e: Exception) {
                                    e.printStackTrace()

                                    if (e is ActivityNotFoundException) {
                                        ToastUtil.makeText(R.string.toast_no_react_url)
                                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        if (e is FileUriExposedException) {
                                            ToastUtil.makeText(R.string.toast_file_uri_exposed)
                                        }
                                    }
                                }
                            } else {
                                val ae = AnywhereEntity.Builder().apply {
                                    this.param1 = param1
                                    this.param2 = param2
                                    this.param3 = param3
                                    this.type = AnywhereType.ACTIVITY
                                }
                                execCmd(TextUtils.getItemCommand(ae))
                            }
                        }
                    }
                }
                finish()
            }
        } else {
            finish()
        }
    }

    override fun initView() {}
    override fun setViewBinding() {}
    override fun setToolbar() {}

    init {
        isPaddingToolbar = false
    }

    companion object {
        const val ACTION_START_COLLECTOR = "START_COLLECTOR"
        const val ACTION_START_COMMAND = "START_COMMAND"
        const val ACTION_START_FROM_WIDGET = "START_FROM_WIDGET"
        const val ACTION_START_QR_CODE = "START_QR_CODE"
        const val ACTION_START_IMAGE = "START_IMAGE"
    }
}