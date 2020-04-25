package com.absinthe.anywhere_.ui.backup

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.model.AnywhereEntity
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.model.PageEntity
import com.absinthe.anywhere_.utils.CipherUtils.decrypt
import com.absinthe.anywhere_.utils.ListUtils
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.view.AnywhereDialogBuilder
import com.absinthe.anywhere_.view.AnywhereDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import timber.log.Timber

class RestoreApplyFragmentDialog : AnywhereDialogFragment() {

    private lateinit var mEditText: TextInputEditText

    val text: String
        get() = mEditText.text.toString()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_fragment_restore_apply, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AnywhereDialogBuilder(requireContext())
        val layoutInflater = (requireActivity()).layoutInflater

        @SuppressLint("InflateParams")
        val inflate = layoutInflater.inflate(R.layout.dialog_fragment_restore_apply, null, false)

        mEditText = inflate.findViewById(R.id.tiet_paste)

        val listener = DialogInterface.OnClickListener { _: DialogInterface?, _: Int ->
            val content = decrypt(text)
            Timber.d(content)

            try {
                Gson().fromJson<List<AnywhereEntity>>(content,
                        object : TypeToken<List<AnywhereEntity?>?>() {}.type)?.let { list ->
                    BackupActivity.INSERT_CORRECT = true

                    for (ae in list) {
                        if (!BackupActivity.INSERT_CORRECT) {
                            ToastUtil.makeText(R.string.toast_backup_file_error)
                            break
                        }

                        AnywhereApplication.sRepository.allPageEntities.value?.let { entities ->
                            if (ListUtils.getPageEntityByTitle(ae.category) == null) {
                                AnywhereApplication.sRepository.insertPage(PageEntity.Builder().apply {
                                    title = ae.category
                                    priority = entities.size + 1
                                    type = AnywhereType.CARD_PAGE
                                })
                            }
                        }
                        AnywhereApplication.sRepository.insert(ae)
                    }

                    if (BackupActivity.INSERT_CORRECT) {
                        ToastUtil.makeText(getString(R.string.toast_restore_success))
                    }
                }
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
                ToastUtil.makeText(R.string.toast_backup_file_error)
            }
        }

        return builder.setView(inflate)
                .setTitle(R.string.settings_backup_apply_title)
                .setPositiveButton(R.string.btn_apply, listener)
                .create()
    }
}