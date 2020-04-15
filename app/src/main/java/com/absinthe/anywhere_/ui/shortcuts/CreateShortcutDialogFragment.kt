package com.absinthe.anywhere_.ui.shortcuts

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.model.AnywhereEntity
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.ShortcutsUtils
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.utils.UiUtils
import com.absinthe.anywhere_.view.AnywhereDialogBuilder
import com.absinthe.anywhere_.view.AnywhereDialogFragment
import com.absinthe.anywhere_.viewbuilder.entity.CreateShortcutDialogBuilder
import com.blankj.utilcode.util.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class CreateShortcutDialogFragment(private val mEntity: AnywhereEntity) : AnywhereDialogFragment() {
    private lateinit var mBuilder: CreateShortcutDialogBuilder
    private val mIcon: Drawable = UiUtils.getAppIconByPackageName(Utils.getApp(), mEntity)
    private val mName: String = mEntity.appName

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mBuilder = CreateShortcutDialogBuilder(requireContext())
        val builder = AnywhereDialogBuilder(requireContext())
        initView()

        return builder.setView(mBuilder.root)
                .setTitle(R.string.dialog_set_icon_and_name_title)
                .setPositiveButton(R.string.dialog_delete_positive_button) { _: DialogInterface?, _: Int ->
                    ShortcutsUtils.addPinnedShortcut(mEntity,
                            mBuilder.ivIcon.drawable, mBuilder.etName.text.toString())
                    setDismissParent(true)
                }
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
                .create()
    }

    private fun initView() {
        mBuilder.ivIcon.setImageDrawable(mIcon)
        mBuilder.etName.setText(mName)
        mBuilder.ivIcon.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "image/*"
                startActivityForResult(intent, Const.REQUEST_CODE_IMAGE_CAPTURE)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
                ToastUtil.makeText(R.string.toast_no_document_app)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Const.REQUEST_CODE_IMAGE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    val iconUri = data.data
                    if (iconUri != null) {
                        Glide.with(this)
                                .load(iconUri)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(mBuilder.ivIcon)
                        AppUtils.takePersistableUriPermission(requireContext(), iconUri, data)
                    }
                }
            }
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}