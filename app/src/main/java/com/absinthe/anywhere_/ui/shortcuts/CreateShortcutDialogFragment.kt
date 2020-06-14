package com.absinthe.anywhere_.ui.shortcuts

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.ShortcutsUtils
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.utils.UiUtils
import com.absinthe.anywhere_.view.app.AnywhereDialogBuilder
import com.absinthe.anywhere_.view.app.AnywhereDialogFragment
import com.absinthe.anywhere_.viewbuilder.entity.CreateShortcutDialogBuilder
import com.blankj.utilcode.util.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class CreateShortcutDialogFragment(private val mEntity: AnywhereEntity) : AnywhereDialogFragment() {
    private lateinit var mBuilder: CreateShortcutDialogBuilder
    private val mIcon: Drawable = UiUtils.getAppIconByPackageName(Utils.getApp(), mEntity)
    private val mName: String = mEntity.appName

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mBuilder = CreateShortcutDialogBuilder(requireContext())
        val builder = AnywhereDialogBuilder(requireContext())
        initView()

        return builder.setView(mBuilder.root)
                .setTitle(R.string.dialog_set_icon_and_name_title)
                .setPositiveButton(R.string.dialog_delete_positive_button) { _: DialogInterface?, _: Int ->
                    if (AppUtils.atLeastO()) {
                        ShortcutsUtils.addPinnedShortcut(mEntity,
                                mBuilder.ivIcon.drawable, mBuilder.etName.text.toString())
                    } else {
                        ShortcutsUtils.addHomeShortcutPreO(mEntity,
                                mBuilder.ivIcon.drawable, mBuilder.etName.text.toString())
                    }
                }
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
                .create()
    }

    private fun initView() {
        mBuilder.apply {
            etName.setText(mName)
            ivIcon.apply {
                setImageDrawable(mIcon)
                setOnClickListener {
                    try {
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "image/*"
                        }
                        startActivityForResult(intent, Const.REQUEST_CODE_IMAGE_CAPTURE)
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                        ToastUtil.makeText(R.string.toast_no_document_app)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Const.REQUEST_CODE_IMAGE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                data.data?.let {
                    Glide.with(this@CreateShortcutDialogFragment)
                            .load(it)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(mBuilder.ivIcon)
                    AppUtils.takePersistableUriPermission(requireContext(), it, data)
                }
            }
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}