package com.absinthe.anywhere_.utils.manager

import android.app.Activity
import android.content.*
import android.os.Build
import android.text.Spanned
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.CommandResult
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.model.Settings
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.ui.backup.RestoreApplyFragmentDialog
import com.absinthe.anywhere_.ui.backup.WebdavFilesListDialogFragment
import com.absinthe.anywhere_.ui.dialog.*
import com.absinthe.anywhere_.ui.dialog.DynamicParamsDialogFragment.OnParamsInputListener
import com.absinthe.anywhere_.ui.list.CardListDialogFragment
import com.absinthe.anywhere_.ui.settings.IconPackDialogFragment
import com.absinthe.anywhere_.ui.settings.IntervalDialogFragment
import com.absinthe.anywhere_.ui.settings.TimePickerDialogFragment
import com.absinthe.anywhere_.ui.shortcuts.CreateShortcutDialogFragment
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.ClipboardUtil
import com.absinthe.anywhere_.utils.ShortcutsUtils
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.utils.handler.URLSchemeHandler
import com.absinthe.anywhere_.view.app.AnywhereDialogBuilder
import com.absinthe.anywhere_.view.app.AnywhereDialogFragment
import com.absinthe.anywhere_.view.home.ColorPickerDialogBuilder
import com.flask.colorpicker.ColorPickerView

/**
 * Dialog Manager
 *
 *
 * To manage all Dialogs / DialogFragments / BottomSheetDialogs in App.
 */
object DialogManager {

    fun showResetBackgroundDialog(activity: Activity) {
        AnywhereDialogBuilder(activity)
                .setTitle(R.string.dialog_reset_background_confirm_title)
                .setMessage(R.string.dialog_reset_background_confirm_message)
                .setPositiveButton(R.string.dialog_delete_positive_button) { _, _ ->
                    GlobalValues.backgroundUri = ""
                    GlobalValues.actionBarType = Const.ACTION_BAR_TYPE_DARK
                    AppUtils.restart()
                }
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
                .show()
    }

    fun showClearShortcutsDialog(activity: Activity) {
        AnywhereDialogBuilder(activity)
                .setTitle(R.string.dialog_reset_background_confirm_title)
                .setMessage(R.string.dialog_reset_shortcuts_confirm_message)
                .setPositiveButton(R.string.dialog_delete_positive_button) { _, _ ->
                    if (AppUtils.atLeastNMR1()) {
                        ShortcutsUtils.clearAllShortcuts()
                    }
                }
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
                .show()
    }

    fun showBackupShareDialog(activity: Activity, dig: String, encrypted: String) {
        AnywhereDialogBuilder(activity)
                .setTitle(R.string.settings_backup_share_title)
                .setMessage(dig)
                .setPositiveButton(R.string.btn_backup_copy) { _, _ ->
                    val cm = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val mClipData = ClipData.newPlainText("Label", encrypted)
                    cm.setPrimaryClip(mClipData)
                    ToastUtil.makeText(R.string.toast_copied)
                }
                .setNeutralButton(R.string.btn_backup_share) { _, _ ->
                    val textIntent = Intent(Intent.ACTION_SEND)
                    textIntent.type = "text/plain"
                    textIntent.putExtra(Intent.EXTRA_TEXT, encrypted)
                    activity.startActivity(Intent.createChooser(textIntent, activity.getString(R.string.settings_backup_share_title)))
                }
                .show()
    }

    fun showDebugDialog(activity: Activity) {
        AnywhereDialogBuilder(activity)
                .setTitle("Debug info")
                .setMessage(GlobalValues.info)
                .setPositiveButton(R.string.dialog_delete_positive_button, null)
                .setNeutralButton(R.string.logcat) { _, _ ->
                    Settings.setLogger()
                    AppUtils.startLogcat(activity)
                }
                .setCancelable(false)
                .show()
    }

    fun showDeleteAnywhereDialog(activity: Activity, ae: AnywhereEntity) {
        AnywhereDialogBuilder(activity)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(HtmlCompat.fromHtml(String.format(activity.getString(R.string.dialog_delete_message), "<b>" + ae.appName + "</b>"), HtmlCompat.FROM_HTML_MODE_LEGACY))
                .setPositiveButton(R.string.dialog_delete_positive_button) { _, _ ->
                    activity.onBackPressed()
                    AnywhereApplication.sRepository.delete(ae, 300)
                }
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
                .show()
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    fun showAddShortcutDialog(context: Context, builder: AnywhereDialogBuilder, ae: AnywhereEntity, action: () -> Unit) {
        builder.setTitle(R.string.dialog_add_shortcut_title)
                .setMessage(HtmlCompat.fromHtml(String.format(context.getString(R.string.dialog_add_shortcut_message), "<b>" + ae.appName + "</b>"), HtmlCompat.FROM_HTML_MODE_LEGACY))
                .setPositiveButton(R.string.dialog_delete_positive_button) { _, _ -> action() }
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
                .show()
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    fun showCannotAddShortcutDialog(context: Context, action: () -> Unit) {
        AnywhereDialogBuilder(context)
                .setTitle(R.string.dialog_cant_add_shortcut_title)
                .setMessage(R.string.dialog_cant_add_shortcut_message)
                .setPositiveButton(R.string.dialog_delete_positive_button, null)
                .setNeutralButton(R.string.dialog_add_shortcut_anymore_button) { _, _ -> action() }
                .show()
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    fun showRemoveShortcutDialog(context: Context, ae: AnywhereEntity, action: () -> Unit) {
        val builder = AnywhereDialogBuilder(context)
        builder.setTitle(R.string.dialog_remove_shortcut_title)
                .setMessage(HtmlCompat.fromHtml(String.format(context.getString(R.string.dialog_remove_shortcut_message), "<b>" + ae.appName + "</b>"), HtmlCompat.FROM_HTML_MODE_LEGACY))
                .setPositiveButton(R.string.dialog_delete_positive_button) { _, _ -> action() }
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
        builder.show()
    }

    fun showDeleteSelectCardDialog(context: Context, action: () -> Unit) {
        AnywhereDialogBuilder(context)
                .setTitle(R.string.dialog_delete_selected_title)
                .setMessage(R.string.dialog_delete_selected_message)
                .setPositiveButton(R.string.dialog_delete_positive_button) { _, _ -> action() }
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
                .show()
    }

    fun showHasNotGrantPermYetDialog(activity: Activity, action: () -> Unit) {
        AnywhereDialogBuilder(activity)
                .setMessage(R.string.dialog_message_perm_not_ever)
                .setPositiveButton(R.string.dialog_delete_positive_button) { _, _ -> action() }
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
                .show()
    }

    fun showShortcutCommunityTipsDialog(activity: Activity, action: () -> Unit) {
        AnywhereDialogBuilder(activity)
                .setMessage(R.string.dialog_shortcut_community_tips)
                .setPositiveButton(android.R.string.ok) { _, _ -> action() }
                .show()
    }

    fun showCheckShizukuWorkingDialog(context: Context) {
        AnywhereDialogBuilder(context)
                .setMessage(R.string.dialog_message_shizuku_not_running)
                .setPositiveButton(R.string.dialog_delete_positive_button) { _: DialogInterface?, _: Int ->
                    val intent = context.packageManager.getLaunchIntentForPackage("moe.shizuku.privileged.api")
                    if (intent != null) {
                        (context as AppCompatActivity).startActivityForResult(intent, Const.REQUEST_CODE_SHIZUKU_PERMISSION)
                    } else {
                        ToastUtil.makeText(R.string.toast_not_install_shizuku)
                        try {
                            URLSchemeHandler.parse(context, URLManager.SHIZUKU_MARKET_URL)
                        } catch (e: ActivityNotFoundException) {
                            e.printStackTrace()
                            ToastUtil.makeText(R.string.toast_no_react_url)
                        }
                    }
                }
                .show()
    }

    fun showGotoShizukuManagerDialog(activity: Activity, action: () -> Unit) {
        AnywhereDialogBuilder(activity)
                .setTitle(R.string.dialog_permission_title)
                .setMessage(R.string.dialog_permission_message)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_delete_positive_button) { _, _ -> action() }
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
                .show()
    }

    fun showDeletePageDialog(context: Context, title: String, isDeletePageAndItem: Boolean, action: () -> Unit) {
        val message: Spanned = if (isDeletePageAndItem) {
            HtmlCompat.fromHtml(String.format(context.getString(R.string.dialog_delete_with_sub_item_message), "<b>$title</b>"), HtmlCompat.FROM_HTML_MODE_LEGACY)
        } else {
            HtmlCompat.fromHtml(String.format(context.getString(R.string.dialog_delete_message), "<b>$title</b>"), HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
        AnywhereDialogBuilder(context)
                .setTitle(R.string.dialog_delete_selected_title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_delete_positive_button) { _, _ -> action() }
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
                .show()
    }

    fun showPageListDialog(context: Context, action: (title: String) -> Unit) {
        val items = mutableListOf<String>()

        AnywhereApplication.sRepository.allPageEntities.value?.let { list ->
            list.iterator().forEach { items.add(it.title) }

            AnywhereDialogBuilder(context).apply {
                setTitle(R.string.menu_move_to_page)
                setItems(items.toTypedArray()) { _, which ->
                    action(items[which])
                }
                show()
            }
        }
    }

    fun showPageListDialog(context: Context, ae: AnywhereEntity) {
        val items = mutableListOf<String>()

        AnywhereApplication.sRepository.allPageEntities.value?.let { list ->
            list.iterator().forEach { items.add(it.title) }

            AnywhereDialogBuilder(context).apply {
                setTitle(R.string.menu_move_to_page)
                setItems(items.toTypedArray()) { _, which ->
                    ae.category = list[which].title
                    AnywhereApplication.sRepository.update(ae)
                }
                show()
            }
        }
    }

    fun showColorPickerDialog(context: Context, item: AnywhereEntity) {
        val builder = ColorPickerDialogBuilder.with(context)
        builder.setTitle(context.getString(R.string.dialog_choose_color_title))
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .lightnessSliderOnly()
                .showColorEdit(true)
                .setPositiveButton(context.getString(R.string.dialog_delete_positive_button)) { _: DialogInterface?, i: Int, _: Array<Int?>? ->
                    AnywhereApplication.sRepository.update(AnywhereEntity(item).apply {
                        color = i
                    })
                    builder.setDismissParent(true)
                }
                .setNeutralButton(context.getString(R.string.btn_reset_color)) { _: DialogInterface?, _: Int ->
                    AnywhereApplication.sRepository.update(AnywhereEntity(item).apply {
                        color = 0
                    })
                    builder.setDismissParent(true)
                }
                .setNegativeButton(context.getString(R.string.dialog_delete_negative_button), null)
        builder.build().show()
    }

    fun showAddPageDialog(context: Context, action: (which: Int) -> Unit) {
        val items = arrayOf("Add card page", "Add WebView")
        val builder = AnywhereDialogBuilder(context)
        builder.setItems(items) { _, which: Int ->
            action(which)
            builder.setDismissParent(true)
        }
        builder.show()
    }

    fun showShellResultDialog(context: Context, result: String?, posListener: DialogInterface.OnClickListener? = null, cancelListener: DialogInterface.OnCancelListener? = null) {
        if (!GlobalValues.isShowShellResult) {
            ToastUtil.makeText(R.string.toast_execute_shell_successful)
            posListener?.onClick(null, 0)
            return
        }

        val parsedResult = if (CommandResult.MAP.containsKey(result)) {
            "[Anywhere- $result] ${CommandResult.MAP[result]}"
        } else {
            result
        }

        AnywhereDialogBuilder(context)
                .setTitle(R.string.dialog_shell_result_title)
                .setMessage(parsedResult)
                .setPositiveButton(R.string.dialog_close_button, posListener)
                .setNeutralButton(R.string.dialog_copy) { _, _ ->
                    ClipboardUtil.put(context, "$parsedResult")
                    ToastUtil.makeText(R.string.toast_copied)
                }
                .setOnCancelListener(cancelListener)
                .apply {
                    (this as AnywhereDialogBuilder).setMessageSelectable(true)
                }
                .show()
    }

    fun showIconPackChoosingDialog(activity: AppCompatActivity) {
        val fragment = IconPackDialogFragment()
        fragment.show(activity.supportFragmentManager, fragment.tag)
    }

    fun showDarkModeTimePickerDialog(activity: AppCompatActivity) {
        val fragment = TimePickerDialogFragment()
        fragment.show(activity.supportFragmentManager, fragment.tag)
    }

    fun showIntervalSetupDialog(activity: AppCompatActivity) {
        val fragment = IntervalDialogFragment()
        fragment.show(activity.supportFragmentManager, fragment.tag)
    }

    fun showRestoreApplyDialog(activity: AppCompatActivity) {
        val dialog = RestoreApplyFragmentDialog()
        dialog.show(activity.supportFragmentManager, dialog.tag)
    }

    fun showCreatePinnedShortcutDialog(activity: AppCompatActivity, ae: AnywhereEntity) {
        val fragment = CreateShortcutDialogFragment(ae)
        fragment.show(activity.supportFragmentManager, fragment.tag)
    }

    fun showCardListDialog(activity: AppCompatActivity): CardListDialogFragment {
        val fragment = CardListDialogFragment()
        fragment.show(activity.supportFragmentManager, fragment.tag)
        return fragment
    }

    fun showRenameDialog(activity: AppCompatActivity, title: String) {
        val dialog = RenameDialogFragment(title)
        dialog.show(activity.supportFragmentManager, dialog.tag)
    }

    fun showImageDialog(activity: AppCompatActivity, uri: String, listener: AnywhereDialogFragment.OnDismissListener? = null) {
        val dialog = ImageDialogFragment(uri, listener)
        dialog.show(activity.supportFragmentManager, dialog.tag)
    }

    fun showGrantPrivilegedPermDialog(activity: AppCompatActivity) {
        val dialogFragment = IceBoxGrantDialogFragment()
        dialogFragment.show(activity.supportFragmentManager, dialogFragment.tag)
    }

    fun showCardSharingDialog(activity: AppCompatActivity, text: String) {
        val dialogFragment = CardSharingDialogFragment(text)
        dialogFragment.show(activity.supportFragmentManager, dialogFragment.tag)
    }

    fun showDynamicParamsDialog(activity: AppCompatActivity, text: String, listener: OnParamsInputListener?) {
        val dialogFragment = DynamicParamsDialogFragment(text)
        dialogFragment.setListener(listener)
        dialogFragment.show(activity.supportFragmentManager, dialogFragment.tag)
    }

    fun showAdvancedCardSelectDialog(activity: AppCompatActivity) {
        val dialogFragment = AdvancedCardSelectDialogFragment()
        dialogFragment.show(activity.supportFragmentManager, dialogFragment.tag)
    }

    fun showWebdavRestoreDialog(activity: AppCompatActivity) {
        val dialog = WebdavFilesListDialogFragment()
        dialog.show(activity.supportFragmentManager, dialog.tag)
    }
}