package com.absinthe.anywhere_.utils.manager

import android.app.Activity
import android.content.*
import android.os.Build
import android.text.Html
import android.text.Spanned
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.model.AnywhereEntity
import com.absinthe.anywhere_.model.Const
import com.absinthe.anywhere_.model.GlobalValues
import com.absinthe.anywhere_.model.Settings
import com.absinthe.anywhere_.ui.backup.RestoreApplyFragmentDialog
import com.absinthe.anywhere_.ui.fragment.*
import com.absinthe.anywhere_.ui.fragment.AdvancedCardSelectDialogFragment.OnClickItemListener
import com.absinthe.anywhere_.ui.fragment.DynamicParamsDialogFragment.OnParamsInputListener
import com.absinthe.anywhere_.ui.gift.GiftPriceDialogFragment
import com.absinthe.anywhere_.ui.list.CardListDialogFragment
import com.absinthe.anywhere_.ui.main.RenameFragmentDialog
import com.absinthe.anywhere_.ui.settings.IconPackDialogFragment
import com.absinthe.anywhere_.ui.settings.IntervalDialogFragment
import com.absinthe.anywhere_.ui.settings.TimePickerDialogFragment
import com.absinthe.anywhere_.ui.shortcuts.CreateShortcutDialogFragment
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.ShortcutsUtils
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.utils.handler.URLSchemeHandler
import com.absinthe.anywhere_.view.AnywhereDialogBuilder
import com.absinthe.anywhere_.view.AnywhereDialogFragment
import com.absinthe.anywhere_.view.ColorPickerDialogBuilder
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
                .setPositiveButton(R.string.dialog_delete_positive_button) { _: DialogInterface?, _: Int ->
                    GlobalValues.setsBackgroundUri("")
                    AppUtils.restart()
                }
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
                .show()
    }

    fun showClearShortcutsDialog(activity: Activity) {
        AnywhereDialogBuilder(activity)
                .setTitle(R.string.dialog_reset_background_confirm_title)
                .setMessage(R.string.dialog_reset_shortcuts_confirm_message)
                .setPositiveButton(R.string.dialog_delete_positive_button) { _: DialogInterface?, _: Int ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                        ShortcutsUtils.clearShortcuts()
                    }
                }
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
                .show()
    }

    @JvmStatic
    fun showBackupShareDialog(context: Context, dig: String?, encrypted: String?) {
        AnywhereDialogBuilder(context)
                .setTitle(R.string.settings_backup_share_title)
                .setMessage(dig)
                .setPositiveButton(R.string.btn_backup_copy) { _: DialogInterface?, _: Int ->
                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val mClipData = ClipData.newPlainText("Label", encrypted)
                    cm.setPrimaryClip(mClipData)
                    ToastUtil.makeText(R.string.toast_copied)
                }
                .setNeutralButton(R.string.btn_backup_share) { _: DialogInterface?, _: Int ->
                    val textIntent = Intent(Intent.ACTION_SEND)
                    textIntent.type = "text/plain"
                    textIntent.putExtra(Intent.EXTRA_TEXT, encrypted)
                    context.startActivity(Intent.createChooser(textIntent, context.getString(R.string.settings_backup_share_title)))
                }
                .show()
    }

    fun showDebugDialog(activity: Activity) {
        AnywhereDialogBuilder(activity)
                .setTitle("Debug info")
                .setMessage(GlobalValues.getInfo())
                .setPositiveButton(R.string.dialog_delete_positive_button, null)
                .setNeutralButton(R.string.logcat) { _: DialogInterface?, _: Int ->
                    Settings.setLogger()
                    AppUtils.startLogcat(activity)
                }
                .setCancelable(false)
                .show()
    }

    @JvmStatic
    fun showDeleteAnywhereDialog(context: Context, ae: AnywhereEntity) {
        val builder = AnywhereDialogBuilder(context)
        builder.setTitle(R.string.dialog_delete_title)
                .setMessage(Html.fromHtml(String.format(context.getString(R.string.dialog_delete_message), "<b>" + ae.appName + "</b>")))
                .setPositiveButton(R.string.dialog_delete_positive_button) { _: DialogInterface?, _: Int ->
                    AnywhereApplication.sRepository.delete(ae)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                        ShortcutsUtils.removeShortcut(ae)
                        builder.setDismissParent(true)
                    }
                }
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
                .show()
    }

    @JvmStatic
    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    fun showAddShortcutDialog(context: Context, builder: AnywhereDialogBuilder, ae: AnywhereEntity, listener: DialogInterface.OnClickListener?) {
        builder.setTitle(R.string.dialog_add_shortcut_title)
                .setMessage(Html.fromHtml(String.format(context.getString(R.string.dialog_add_shortcut_message), "<b>" + ae.appName + "</b>")))
                .setPositiveButton(R.string.dialog_delete_positive_button, listener)
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
                .show()
    }

    @JvmStatic
    fun showCannotAddShortcutDialog(context: Context?, listener: DialogInterface.OnClickListener?) {
        AnywhereDialogBuilder(context)
                .setTitle(R.string.dialog_cant_add_shortcut_title)
                .setMessage(R.string.dialog_cant_add_shortcut_message)
                .setPositiveButton(R.string.dialog_delete_positive_button, null)
                .setNeutralButton(R.string.dialog_add_shortcut_anymore_button, listener)
                .show()
    }

    @JvmStatic
    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    fun showRemoveShortcutDialog(context: Context, ae: AnywhereEntity) {
        val builder = AnywhereDialogBuilder(context)
        builder.setTitle(R.string.dialog_remove_shortcut_title)
                .setMessage(Html.fromHtml(String.format(context.getString(R.string.dialog_remove_shortcut_message), "<b>" + ae.appName + "</b>")))
                .setPositiveButton(R.string.dialog_delete_positive_button) { _: DialogInterface?, _: Int ->
                    ShortcutsUtils.removeShortcut(ae)
                    builder.setDismissParent(true)
                }
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
        builder.show()
    }

    @JvmStatic
    fun showDeleteSelectCardDialog(context: Context?, listener: DialogInterface.OnClickListener?) {
        AnywhereDialogBuilder(context)
                .setTitle(R.string.dialog_delete_selected_title)
                .setMessage(R.string.dialog_delete_selected_message)
                .setPositiveButton(R.string.dialog_delete_positive_button, listener)
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
                .show()
    }

    @JvmStatic
    fun showHasSameCardDialog(context: Context?, listener: DialogInterface.OnClickListener?) {
        AnywhereDialogBuilder(context)
                .setMessage(R.string.dialog_message_same_app_name)
                .setPositiveButton(R.string.dialog_delete_positive_button, listener)
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
                .show()
    }

    fun showHasNotGrantPermYetDialog(activity: Activity, listener: DialogInterface.OnClickListener) {
        AnywhereDialogBuilder(activity)
                .setMessage(R.string.dialog_message_perm_not_ever)
                .setPositiveButton(R.string.dialog_delete_positive_button, listener)
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
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
                            URLSchemeHandler.parse(URLManager.SHIZUKU_COOLAPK_DOWNLOAD_PAGE, context)
                        } catch (e: ActivityNotFoundException) {
                            e.printStackTrace()
                            ToastUtil.makeText(R.string.toast_no_react_url)
                        }
                    }
                }
                .show()
    }

    fun showGotoShizukuManagerDialog(activity: Activity, listener: DialogInterface.OnClickListener?) {
        AnywhereDialogBuilder(activity)
                .setTitle(R.string.dialog_permission_title)
                .setMessage(R.string.dialog_permission_message)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_delete_positive_button, listener)
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
                .show()
    }

    fun showDeletePageDialog(context: Context, title: String, listener: DialogInterface.OnClickListener?, isDeletePageAndItem: Boolean) {
        val message: Spanned = if (isDeletePageAndItem) {
            Html.fromHtml(String.format(context.getString(R.string.dialog_delete_with_sub_item_message), "<b>$title</b>"))
        } else {
            Html.fromHtml(String.format(context.getString(R.string.dialog_delete_message), "<b>$title</b>"))
        }
        AnywhereDialogBuilder(context)
                .setTitle(R.string.dialog_delete_selected_title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_delete_positive_button, listener)
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
                .show()
    }

    @JvmStatic
    fun showPageListDialog(context: Context?, ae: AnywhereEntity) {
        val items: MutableList<String> = ArrayList()

        AnywhereApplication.sRepository.allPageEntities?.value?.let {
            for (pe in it) {
                items.add(pe.title)
            }

            AnywhereDialogBuilder(context).apply {
                setItems(items.toTypedArray()) { _: DialogInterface?, which: Int ->
                    ae.category = it[which].title
                    AnywhereApplication.sRepository.update(ae)
                    setDismissParent(true)
                }
                show()
            }
        }
    }

    @JvmStatic
    fun showColorPickerDialog(context: Context, item: AnywhereEntity) {
        val builder = ColorPickerDialogBuilder.with(context)
        builder.setTitle(context.getString(R.string.dialog_choose_color_title))
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .lightnessSliderOnly()
                .showColorEdit(true)
                .setPositiveButton(context.getString(R.string.dialog_delete_positive_button)) { _: DialogInterface?, i: Int, _: Array<Int?>? ->
                    item.color = i
                    AnywhereApplication.sRepository.update(item)
                    builder.setDismissParent(true)
                }
                .setNeutralButton(context.getString(R.string.btn_reset_color)) { _: DialogInterface?, _: Int ->
                    item.color = 0
                    AnywhereApplication.sRepository.update(item)
                    builder.setDismissParent(true)
                }
                .setNegativeButton(context.getString(R.string.dialog_delete_negative_button), null)
        builder.build().show()
    }

    @JvmStatic
    fun showAddPageDialog(context: Context?, listener: DialogInterface.OnClickListener) {
        val items = arrayOf("Add card page", "Add WebView")
        val builder = AnywhereDialogBuilder(context)
        builder.setItems(items) { dialog: DialogInterface?, which: Int ->
            listener.onClick(dialog, which)
            builder.setDismissParent(true)
        }
        builder.show()
    }

    @JvmStatic
    fun showShellResultDialog(context: Context?, result: String?, posListener: DialogInterface.OnClickListener?, cancelListener: DialogInterface.OnCancelListener?) {
        if (!GlobalValues.sIsShowShellResult) {
            ToastUtil.makeText(R.string.toast_execute_shell_successful)
            return
        }
        val builder = AnywhereDialogBuilder(context)
        builder.setTitle(R.string.dialog_shell_result_title)
                .setMessage(result)
                .setPositiveButton(R.string.dialog_close_button, posListener)
                .setOnCancelListener(cancelListener)
                .show()
    }

    @JvmStatic
    fun showIconPackChoosingDialog(activity: AppCompatActivity) {
        val fragment = IconPackDialogFragment()
        fragment.show(activity.supportFragmentManager, fragment.tag)
    }

    @JvmStatic
    fun showDarkModeTimePickerDialog(activity: AppCompatActivity) {
        val fragment = TimePickerDialogFragment()
        fragment.show(activity.supportFragmentManager, fragment.tag)
    }

    @JvmStatic
    fun showIntervalSetupDialog(activity: AppCompatActivity) {
        val fragment = IntervalDialogFragment()
        fragment.show(activity.supportFragmentManager, fragment.tag)
    }

    @JvmStatic
    fun showRestoreApplyDialog(activity: AppCompatActivity) {
        val dialog = RestoreApplyFragmentDialog()
        dialog.show(activity.supportFragmentManager, dialog.tag)
    }

    @JvmStatic
    fun showCreatePinnedShortcutDialog(activity: AppCompatActivity, ae: AnywhereEntity?) {
        val fragment = CreateShortcutDialogFragment(ae!!)
        fragment.show(activity.supportFragmentManager, fragment.tag)
    }

    @JvmStatic
    fun showCardListDialog(activity: AppCompatActivity): CardListDialogFragment {
        val fragment = CardListDialogFragment()
        fragment.show(activity.supportFragmentManager, fragment.tag)
        return fragment
    }

    fun showRenameDialog(activity: AppCompatActivity, title: String) {
        val dialog = RenameFragmentDialog(title)
        dialog.show(activity.supportFragmentManager, dialog.tag)
    }

    fun showImageDialog(activity: AppCompatActivity, ae: AnywhereEntity) {
        val dialog = ImageDialogFragment(ae)
        dialog.show(activity.supportFragmentManager, dialog.tag)
    }

    @JvmStatic
    fun showImageDialog(activity: AppCompatActivity, ae: AnywhereEntity?, listener: AnywhereDialogFragment.OnDismissListener?) {
        val dialog = ImageDialogFragment(ae!!, listener)
        dialog.show(activity.supportFragmentManager, dialog.tag)
    }

    @JvmStatic
    fun showGrantPrivilegedPermDialog(activity: AppCompatActivity) {
        val dialogFragment = IceBoxGrantDialogFragment()
        dialogFragment.show(activity.supportFragmentManager, dialogFragment.tag)
    }

    fun showGiftPriceDialog(activity: AppCompatActivity) {
        val dialogFragment = GiftPriceDialogFragment()
        dialogFragment.show(activity.supportFragmentManager, dialogFragment.tag)
    }

    @JvmStatic
    fun showCardSharingDialog(activity: AppCompatActivity, text: String?) {
        val dialogFragment = CardSharingDialogFragment(text)
        dialogFragment.show(activity.supportFragmentManager, dialogFragment.tag)
    }

    @JvmStatic
    fun showDynamicParamsDialog(activity: AppCompatActivity, text: String?, listener: OnParamsInputListener?) {
        val dialogFragment = DynamicParamsDialogFragment(text)
        dialogFragment.setListener(listener)
        dialogFragment.show(activity.supportFragmentManager, dialogFragment.tag)
    }

    @JvmStatic
    fun showAdvancedCardSelectDialog(activity: AppCompatActivity, listener: OnClickItemListener?) {
        val dialogFragment = AdvancedCardSelectDialogFragment()
        dialogFragment.setListener(listener)
        dialogFragment.show(activity.supportFragmentManager, dialogFragment.tag)
    }
}