package com.absinthe.anywhere_.ui.shortcuts

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.os.FileUriExposedException
import android.os.IBinder
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.EventTag
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.model.AnywhereEntity
import com.absinthe.anywhere_.services.CollectorService
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.AppUtils.openNewURLScheme
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

    private var isBound = false
    private var collectorService: CollectorService? = null

    private val conn = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            isBound = true
            collectorService = (service as CollectorService.CollectorBinder).service
            collectorService?.startCollector()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UiUtils.setActionBarTransparent(this)

        val viewModel = ViewModelProvider(this).get(AnywhereViewModel::class.java)
        Analytics.trackEvent(EventTag.SHORTCUT_OPEN)

        intent.action?.let {
            Timber.d("action = %s", it)

            when (it) {
                ACTION_START_COLLECTOR -> {
                    if (GlobalValues.workingMode == Const.WORKING_MODE_URL_SCHEME) {
                        openNewURLScheme(this)
                    } else {
                        if (isBound) {
                            collectorService?.startCollector()
                        } else {
                            bindService(Intent(this, CollectorService::class.java), conn, Context.BIND_AUTO_CREATE)
                        }
                    }
                    finish()
                }
                ACTION_START_COMMAND -> {
                    intent.getStringExtra(Const.INTENT_EXTRA_SHORTCUTS_CMD)?.let { cmd ->
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
                    } ?: finish()
                }
                ACTION_START_FROM_WIDGET -> {
                    intent.getStringExtra(Const.INTENT_EXTRA_WIDGET_COMMAND)?.let { cmd ->
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
                    } ?: finish()
                }
                ACTION_START_QR_CODE -> {
                    intent.getStringExtra(Const.INTENT_EXTRA_SHORTCUTS_CMD)?.let { cmd ->
                        Opener.with(this).load(cmd).open()
                    }
                    finish()
                }
                Intent.ACTION_CREATE_SHORTCUT -> {
                    viewModel.allAnywhereEntities.observe(this, Observer { anywhereEntities: List<AnywhereEntity>? ->
                        val arrayAdapter = ArrayAdapter<String>(Utils.getApp(), android.R.layout.select_dialog_singlechoice)

                        anywhereEntities?.let { entities ->
                            Timber.d("list = %s", entities)

                            for (ae in entities) {
                                arrayAdapter.add(ae.appName)
                            }
                        }

                        AnywhereDialogBuilder(this)
                                .setAdapter(arrayAdapter) { _: DialogInterface?, i: Int ->
                                    val shortcutIntent = Intent(this@ShortcutsActivity, ShortcutsActivity::class.java).apply {
                                        val cmd = TextUtils.getItemCommand(anywhereEntities?.get(i))

                                        action = if (cmd.startsWith(AnywhereType.QRCODE_PREFIX)) {
                                            ACTION_START_QR_CODE
                                        } else {
                                            ACTION_START_COMMAND
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
                }
                ACTION_START_IMAGE -> {
                    intent.getStringExtra(Const.INTENT_EXTRA_SHORTCUTS_CMD)?.let { uri ->
                        val ae = AnywhereEntity.Builder().apply {
                            param1 = uri
                        }
                        showImageDialog(this, ae.param1, object : AnywhereDialogFragment.OnDismissListener {
                            override fun onDismiss() {
                                finish()
                            }
                        })
                    } ?: finish()
                }
                Intent.ACTION_VIEW -> {
                    intent.data?.let { uri ->
                        val host = uri.host

                        if (android.text.TextUtils.equals(host, URLManager.OPEN_HOST)) {
                            val param1 = uri.getQueryParameter(Const.INTENT_EXTRA_PARAM_1) ?: ""
                            val param2 = uri.getQueryParameter(Const.INTENT_EXTRA_PARAM_2) ?: ""
                            val param3 = uri.getQueryParameter(Const.INTENT_EXTRA_PARAM_3) ?: ""

                            if (param2.isEmpty() && param3.isEmpty()) {
                                try {
                                    parse(param1, this)
                                } catch (e: Exception) {
                                    e.printStackTrace()

                                    if (e is ActivityNotFoundException) {
                                        ToastUtil.makeText(R.string.toast_no_react_url)
                                    } else if (AppUtils.atLeastN()) {
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

                                Opener.with(this).load(ae).open()
                            }
                        }
                    }
                    finish()
                }
                else -> finish()
            }
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