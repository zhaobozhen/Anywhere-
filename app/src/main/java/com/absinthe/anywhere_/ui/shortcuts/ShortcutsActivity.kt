package com.absinthe.anywhere_.ui.shortcuts

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.EventTag
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.model.ExtraBean
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.services.overlay.CollectorService
import com.absinthe.anywhere_.services.overlay.ICollectorService
import com.absinthe.anywhere_.ui.editor.impl.SWITCH_OFF
import com.absinthe.anywhere_.utils.AppUtils.openNewURLScheme
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.utils.UxUtils
import com.absinthe.anywhere_.utils.handler.Opener
import com.absinthe.anywhere_.utils.manager.DialogManager.showImageDialog
import com.absinthe.anywhere_.utils.manager.URLManager
import com.absinthe.anywhere_.view.app.AnywhereDialogBuilder
import com.absinthe.anywhere_.view.app.AnywhereDialogFragment
import com.absinthe.anywhere_.viewmodel.AnywhereViewModel
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.Utils
import com.google.gson.Gson
import com.microsoft.appcenter.analytics.Analytics
import timber.log.Timber

class ShortcutsActivity : BaseActivity() {

    private var isBound = false
    private var collectorService: ICollectorService? = null

    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            collectorService = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            isBound = true
            collectorService = ICollectorService.Stub.asInterface(service)
            collectorService?.startCollector()
        }

    }
    private val viewModel by viewModels<AnywhereViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        UxUtils.setActionBarTransparent(this)
        Analytics.trackEvent(EventTag.SHORTCUT_OPEN)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    override fun initView() {}
    override fun setViewBinding() {}
    override fun setToolbar() {}

    private fun handleIntent(intent: Intent) {
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
                            bindService(Intent(this, CollectorService::class.java), connection, Context.BIND_AUTO_CREATE)
                        }
                    }
                    ActivityUtils.startHomeActivity()
                }
                ACTION_START_ENTITY -> {
                    intent.getStringExtra(Const.INTENT_EXTRA_SHORTCUTS_ID)?.let { id ->
                        viewModel.allAnywhereEntities.observe(this, { list ->
                            list.find { findItem ->
                                findItem.id == id
                            }?.apply {
                                Opener.with(this@ShortcutsActivity)
                                        .load(this)
                                        .setOpenedListener(object : Opener.OnOpenListener {
                                            override fun onOpened() {
                                                finish()
                                            }
                                        })
                                        .open()

                                val tileExtra = intent.getStringExtra(Const.INTENT_EXTRA_FROM_TILE)
                                if (tileExtra != null) {
                                    if (this.type == AnywhereType.Card.SWITCH_SHELL) {
                                        when (tileExtra) {
                                            Const.PREF_TILE_ONE -> GlobalValues.tileOneActive = this.param3 != SWITCH_OFF
                                            Const.PREF_TILE_TWO -> GlobalValues.tileTwoActive = this.param3 != SWITCH_OFF
                                            Const.PREF_TILE_THREE -> GlobalValues.tileThreeActive = this.param3 != SWITCH_OFF
                                            else -> {}
                                        }
                                    }
                                }
                            }
                        })
                    }
                }
                ACTION_START_FROM_WIDGET -> {
                    intent.getStringExtra(Const.INTENT_EXTRA_WIDGET_ITEM_ID)?.let { id ->
                        viewModel.allAnywhereEntities.observe(this, { list ->
                            list.find { findItem ->
                                findItem.id == id
                            }?.apply {
                                Opener.with(this@ShortcutsActivity)
                                        .load(this)
                                        .setOpenedListener(object : Opener.OnOpenListener {
                                            override fun onOpened() {
                                                finish()
                                            }
                                        })
                                        .open()
                            }
                        })
                    } ?: finish()
                }
                Intent.ACTION_CREATE_SHORTCUT -> {
                    viewModel.allAnywhereEntities.observe(this, { anywhereEntities: List<AnywhereEntity>? ->
                        val arrayAdapter = ArrayAdapter<String>(Utils.getApp(), android.R.layout.select_dialog_singlechoice)

                        anywhereEntities?.let { entities ->
                            Timber.d("list = %s", entities)

                            for (ae in entities) {
                                arrayAdapter.add(ae.appName)
                            }

                            AnywhereDialogBuilder(this)
                                    .setAdapter(arrayAdapter) { _: DialogInterface?, i: Int ->
                                        val entity = entities[i]
                                        val shortcutIntent = Intent(this@ShortcutsActivity, ShortcutsActivity::class.java).apply {
                                            if (entities[i].type == AnywhereType.Card.IMAGE) {
                                                action = ACTION_START_IMAGE
                                                putExtra(Const.INTENT_EXTRA_SHORTCUTS_CMD, entity.param1)
                                            } else {
                                                action = ACTION_START_ENTITY
                                                putExtra(Const.INTENT_EXTRA_SHORTCUTS_ID, entity.id)
                                            }
                                        }

                                        setResult(Activity.RESULT_OK, Intent().apply {
                                            putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent)
                                            putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.shortcut_open))
                                            putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                                                    Intent.ShortcutIconResource.fromContext(
                                                            this@ShortcutsActivity, R.drawable.ic_shortcut_start_collector))
                                        })
                                        finish()
                                    }
                                    .setOnCancelListener { finish() }
                                    .show()
                        }
                    })
                }
                ACTION_START_IMAGE -> {
                    intent.getStringExtra(Const.INTENT_EXTRA_SHORTCUTS_CMD)?.let { uri ->
                        showImageDialog(this, uri, object : AnywhereDialogFragment.OnDismissListener {
                            override fun onDismiss() {
                                finish()
                            }
                        })
                    } ?: finish()
                }
                Intent.ACTION_VIEW -> {
                    intent.data?.let { uri ->
                        if (uri.host == URLManager.OPEN_HOST) {
                            var dynamicParam: ExtraBean.ExtraItem? = null
                            uri.getQueryParameter(Const.INTENT_EXTRA_DYNAMIC_PARAM)?.let { dynamic ->
                                try {
                                    dynamicParam = Gson().fromJson(dynamic, ExtraBean.ExtraItem::class.java)
                                } catch (ignore: Exception) {
                                }
                            }
                            uri.getQueryParameter(Const.INTENT_EXTRA_OPEN_SHORT_ID)?.let { sid ->
                                viewModel.allAnywhereEntities.observe(this, { list ->
                                    list.find { findItem ->
                                        findItem.id.endsWith(sid)
                                    }?.apply {
                                        Opener.with(this@ShortcutsActivity)
                                                .load(this)
                                                .setDynamicExtra(dynamicParam)
                                                .setOpenedListener(object : Opener.OnOpenListener {
                                                    override fun onOpened() {
                                                        finish()
                                                    }
                                                })
                                                .open()
                                    } ?: run {
                                        ToastUtil.makeText(R.string.toast_invaild_sid)
                                        finish()
                                    }
                                })
                            } ?: finish()
                        }
                    }
                }
                else -> finish()
            }
        }
    }

    companion object {
        const val ACTION_START_COLLECTOR = "START_COLLECTOR"
        const val ACTION_START_ENTITY = "START_ENTITY"
        const val ACTION_START_FROM_WIDGET = "START_FROM_WIDGET"
        const val ACTION_START_IMAGE = "START_IMAGE" //Old Scheme
    }
}