package com.absinthe.anywhere_.utils.handler

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.FileUriExposedException
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues.isShowShellResult
import com.absinthe.anywhere_.listener.OnAppDefrostListener
import com.absinthe.anywhere_.model.*
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.model.manager.QRCollection
import com.absinthe.anywhere_.services.WorkflowIntentService
import com.absinthe.anywhere_.ui.dialog.DynamicParamsDialogFragment.OnParamsInputListener
import com.absinthe.anywhere_.ui.editor.EXTRA_ENTITY
import com.absinthe.anywhere_.ui.editor.impl.SWITCH_OFF
import com.absinthe.anywhere_.ui.editor.impl.SWITCH_ON
import com.absinthe.anywhere_.ui.qrcode.QRCodeCollectionActivity
import com.absinthe.anywhere_.utils.AppTextUtils.getItemCommand
import com.absinthe.anywhere_.utils.AppTextUtils.getPkgNameByCommand
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.AppUtils.isActivityExported
import com.absinthe.anywhere_.utils.CommandUtils
import com.absinthe.anywhere_.utils.ShortcutsUtils
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.utils.manager.DialogManager
import com.absinthe.anywhere_.view.app.AnywhereDialogFragment
import com.blankj.utilcode.util.IntentUtils
import com.catchingnow.icebox.sdk_client.IceBox
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import timber.log.Timber
import java.lang.ref.WeakReference

private const val TYPE_NONE = -1
private const val TYPE_ENTITY = 0
private const val TYPE_CMD = 1

object Opener {

    private var context: WeakReference<Context>? = null
    private var listener: OnOpenListener? = null
    private var item: AnywhereEntity? = null
    private var command: String? = null
    private var type: Int = TYPE_NONE
    private var extraItem: ExtraBean.ExtraItem? = null

    fun with(context: Context): Opener {
        this.context = WeakReference(context)
        return this
    }

    fun load(item: AnywhereEntity): Opener {
        type = TYPE_ENTITY
        this.item = item
        return this
    }

    fun load(cmd: String): Opener {
        type = TYPE_CMD
        this.command = cmd
        return this
    }

    fun setDynamicExtra(item: ExtraBean.ExtraItem?): Opener {
        extraItem = item
        return this
    }

    fun setOpenedListener(listener: OnOpenListener): Opener {
        this.listener = listener
        return this
    }

    @Throws(NullPointerException::class)
    fun open() {
        context?.get()?.let {
            if (type == TYPE_ENTITY) {
                openFromEntity(it)
            } else if (type == TYPE_CMD) {
                openFromCommand(it)
            }
        } ?: let {
            throw NullPointerException("Got a null context instance from Opener.")
        }
    }

    private fun openFromEntity(context: Context) {
        item?.let {
            openAnywhereEntity(context, it)
        }
    }

    private fun openFromCommand(context: Context) {
        command?.let {
            when {
                it.startsWith(AnywhereType.Prefix.DYNAMIC_PARAMS_PREFIX) -> {
                    openDynamicParamCommand(context, it)
                }
                it.startsWith(AnywhereType.Prefix.SHELL_PREFIX) -> {
                    openShellCommand(context, it)
                }
                else -> {
                    openByCommand(context, it, getPkgNameByCommand(it))
                }
            }
        }
    }

    private fun openAnywhereEntity(context: Context, item: AnywhereEntity) {
        when (item.type) {
            AnywhereType.Card.QR_CODE -> {
                val qrId = if (context is QRCodeCollectionActivity) {
                    item.id
                } else {
                    item.param2
                }
                QRCollection.getQREntity(qrId)?.launch()
                listener?.onOpened()
            }
            AnywhereType.Card.IMAGE -> {
                DialogManager.showImageDialog((context as AppCompatActivity), item.param1, object : AnywhereDialogFragment.OnDismissListener {
                    override fun onDismiss() {
                        listener?.onOpened()
                    }
                })
            }
            AnywhereType.Card.ACTIVITY -> {
                val className = if (item.param2.startsWith(".")) {
                    item.param1 + item.param2
                } else {
                    item.param2
                }

                when {
                    item.param2.isBlank() || isActivityExported(context, ComponentName(item.param1, className)) -> {
                        val extraBean: ExtraBean? = try {
                            Gson().fromJson(item.param3, ExtraBean::class.java)
                        } catch (e: JsonSyntaxException) {
                            null
                        }
                        val action = if (extraBean == null || extraBean.action.isEmpty()) {
                            Intent.ACTION_VIEW
                        } else {
                            extraBean.action
                        }

                        val intent = if (item.param2.isBlank()) {
                            IntentUtils.getLaunchAppIntent(item.param1)?.apply {
                                if (context !is Activity) {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                            } ?: let {
                                Intent().apply {
                                    if (context !is Activity) {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                }
                            }
                        } else {
                            Intent(action).apply {
                                component = ComponentName(item.param1, className)
                                if (context !is Activity) {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                            }
                        }
                        extraBean?.let {
                            if (it.data.isNotEmpty()) {
                                intent.data = it.data.toUri()
                            }

                            val extras = it.extras.toMutableList()
                            extraItem?.let { extra ->
                                extras.add(extra)
                            }

                            for (extra in extras) {
                                when (extra.type) {
                                    TYPE_STRING, TYPE_STRING_LABEL -> intent.putExtra(extra.key, extra.value)
                                    TYPE_BOOLEAN, TYPE_BOOLEAN_LABEL -> intent.putExtra(extra.key, extra.value.toBoolean())
                                    TYPE_URI, TYPE_URI_LABEL -> intent.putExtra(extra.key, extra.value.toUri())
                                    TYPE_INT, TYPE_INT_LABEL -> {
                                        try {
                                            extra.value.toInt()
                                        } catch (ignore: NumberFormatException) {
                                            null
                                        }?.let { value ->
                                            intent.putExtra(extra.key, value)
                                        }
                                    }
                                    TYPE_LONG, TYPE_LONG_LABEL -> {
                                        try {
                                            extra.value.toLong()
                                        } catch (ignore: NumberFormatException) {
                                            null
                                        }?.let { value ->
                                            intent.putExtra(extra.key, value)
                                        }
                                    }
                                    TYPE_FLOAT, TYPE_FLOAT_LABEL -> {
                                        try {
                                            extra.value.toFloat()
                                        } catch (ignore: NumberFormatException) {
                                            null
                                        }?.let { value ->
                                            intent.putExtra(extra.key, value)
                                        }
                                    }
                                }
                            }
                        }

                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            ToastUtil.makeText(e.toString())
                        }
                        listener?.onOpened()
                    }
                    else -> {
                        openByCommand(context, getItemCommand(item), item.packageName)
                    }
                }
            }
            AnywhereType.Card.URL_SCHEME -> {
                if (item.param3.isNotEmpty()) {
                    DialogManager.showDynamicParamsDialog((context as AppCompatActivity), item.param3, object : OnParamsInputListener {
                        override fun onFinish(text: String?) {
                            try {
                                URLSchemeHandler.parse(context, item.param1 + text)
                            } catch (e: Exception) {
                                Timber.e(e)
                                if (e is ActivityNotFoundException) {
                                    ToastUtil.makeText(R.string.toast_no_react_url)
                                } else if (AppUtils.atLeastN()) {
                                    if (e is FileUriExposedException) {
                                        ToastUtil.makeText(R.string.toast_file_uri_exposed)
                                    }
                                }
                            }
                            listener?.onOpened()
                        }

                        override fun onCancel() {
                            listener?.onOpened()
                        }
                    })
                } else {
                    try {
                        URLSchemeHandler.parse(context, item.param1)
                    } catch (e: Exception) {
                        Timber.e(e)
                        if (e is ActivityNotFoundException) {
                            ToastUtil.makeText(R.string.toast_no_react_url)
                        } else if (AppUtils.atLeastN()) {
                            if (e is FileUriExposedException) {
                                ToastUtil.makeText(R.string.toast_file_uri_exposed)
                            }
                        }
                    }
                    listener?.onOpened()
                }
            }
            AnywhereType.Card.SHELL -> {
                val result = CommandUtils.execAdbCmd(item.param1)
                DialogManager.showShellResultDialog(context, result, { _, _ -> listener?.onOpened() }, { listener?.onOpened() })
            }
            AnywhereType.Card.SWITCH_SHELL -> {
                openByCommand(context, getItemCommand(item), item.packageName)
                val ae = AnywhereEntity(item).apply {
                    param3 = if (param3 == SWITCH_OFF) SWITCH_ON else SWITCH_OFF
                }
                AnywhereApplication.sRepository.update(ae)

                if (AppUtils.atLeastNMR1()) {
                    ShortcutsUtils.updateShortcut(ae)
                }
            }
            AnywhereType.Card.FILE -> {
                val intent = Intent().apply {
                    action = Intent.ACTION_VIEW
                    data = item.param1.toUri()
                    if (context !is Activity) {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                }

                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    ToastUtil.makeText(R.string.toast_no_react_url)
                }
                listener?.onOpened()
            }
            AnywhereType.Card.BROADCAST -> {
                val extraBean: ExtraBean? = try {
                    Gson().fromJson(item.param1, ExtraBean::class.java)
                } catch (e: JsonSyntaxException) {
                    null
                }
                extraBean?.let {
                    val action = if (it.action.isNotEmpty()) {
                        it.action
                    } else {
                        Const.DEFAULT_BR_ACTION
                    }
                    val intent = Intent(action).apply {
                        if (context !is Activity) {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                    }
                    if (extraBean.data.isNotEmpty()) {
                        intent.data = extraBean.data.toUri()
                    }

                    val extras = extraBean.extras.toMutableList()
                    extraItem?.let { extra ->
                        extras.add(extra)
                    }

                    for (extra in extras) {
                        when (extra.type) {
                            TYPE_STRING, TYPE_STRING_LABEL -> intent.putExtra(extra.key, extra.value)
                            TYPE_BOOLEAN, TYPE_BOOLEAN_LABEL -> intent.putExtra(extra.key, extra.value.toBoolean())
                            TYPE_INT, TYPE_INT_LABEL -> intent.putExtra(extra.key, extra.value.toInt())
                            TYPE_LONG, TYPE_LONG_LABEL -> intent.putExtra(extra.key, extra.value.toLong())
                            TYPE_FLOAT, TYPE_FLOAT_LABEL -> intent.putExtra(extra.key, extra.value.toFloat())
                            TYPE_URI, TYPE_URI_LABEL -> intent.putExtra(extra.key, extra.value.toUri())
                        }
                    }

                    context.sendBroadcast(intent)
                } ?: let {
                    ToastUtil.makeText(R.string.toast_json_error)
                }
                listener?.onOpened()
            }
            AnywhereType.Card.WORKFLOW -> {
                WorkflowIntentService.enqueueWork(context, Intent().apply {
                    putExtra(EXTRA_ENTITY, item)
                })
            }
        }
    }

    private fun openByCommand(context: Context, cmd: String, packageName: String?) {
        if (cmd.isEmpty()) {
            return
        }

        if (packageName.isNullOrEmpty()) {
            CommandUtils.execCmd(cmd)
        } else {
            try {
                if (IceBox.getAppEnabledSetting(context, packageName) != 0) {
                    DefrostHandler.defrost(context, packageName, object : OnAppDefrostListener {
                        override fun onAppDefrost() {
                            CommandUtils.execCmd(cmd)
                        }
                    })
                } else {
                    CommandUtils.execCmd(cmd)
                }
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                CommandUtils.execCmd(cmd)
            } catch (e: IndexOutOfBoundsException) {
                e.printStackTrace()
                ToastUtil.makeText(R.string.toast_wrong_cmd)
            }
        }
        listener?.onOpened()
    }

    private fun openDynamicParamCommand(context: Context, command: String) {
        var newCommand = command.removePrefix(AnywhereType.Prefix.DYNAMIC_PARAMS_PREFIX)
        val splitIndex = newCommand.indexOf(']')
        val param = newCommand.substring(0, splitIndex)
        newCommand = newCommand.substring(splitIndex + 1)

        DialogManager.showDynamicParamsDialog((context as BaseActivity), param, object : OnParamsInputListener {
            override fun onFinish(text: String?) {
                openByCommand(context, newCommand + text, getPkgNameByCommand(newCommand))
                listener?.onOpened()
            }

            override fun onCancel() {
                listener?.onOpened()
            }
        })
    }

    private fun openShellCommand(context: Context, command: String) {
        val newCommand = command.removePrefix(AnywhereType.Prefix.SHELL_PREFIX)
        val result = CommandUtils.execAdbCmd(newCommand)

        if (isShowShellResult) {
            DialogManager.showShellResultDialog(context, result, { _, _ -> listener?.onOpened() }, { listener?.onOpened() })
        } else {
            listener?.onOpened()
        }
    }

    interface OnOpenListener {
        fun onOpened()
    }
}